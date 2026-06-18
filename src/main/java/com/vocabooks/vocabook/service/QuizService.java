package com.vocabooks.vocabook.service;

import com.vocabooks.vocabook.entity.*;
import com.vocabooks.vocabook.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {

	private final WordRepository wordRepository;
	private final UserWordRepository userWordRepository;

	/**
	 * 유저별 가중치 기반으로 퀴즈 단어 10개 선택
	 */
	public List<UserWord> getQuizWords(User user, int count) {
		// 1) 해당 유저의 UserWord가 없으면 초기화
		initUserWordsIfNeeded(user);

		// 2) 유저의 모든 UserWord 조회
		List<UserWord> userWords = userWordRepository.findByUser(user);

		// 3) 가중치 기반 랜덤 선택
		return weightedRandomSelect(userWords, count);
	}

	/**
	 * 첫 퀴즈 시 유저-단어 매핑 초기화
	 */
	@Transactional
	public void initUserWordsIfNeeded(User user) {
		List<Word> allWords = wordRepository.findAll();

		for (Word word : allWords) {
			if (!userWordRepository.existsByUserAndWord(user, word)) {
				UserWord uw = UserWord.builder()
						.user(user)
						.word(word)
						.weight(0)
						.build();
				userWordRepository.save(uw);
			}
		}
	}

	/**
	 * 가중치 기반 랜덤 선택
	 */
	private List<UserWord> weightedRandomSelect(List<UserWord> userWords, int count) {
		List<UserWord> pool = new ArrayList<>(userWords);

		userWords.stream()
            .sorted(Comparator.comparingInt(UserWord::getWeight).reversed())
            .filter(uw -> uw.getWeight() > 0)
            .forEach(uw -> {
                for (int i = 0; i < uw.getWeight(); i++) {
                    pool.add(uw);
                }
            });

		Collections.shuffle(pool);

		// 중복 제거하면서 count개 선택
		Set<Long> selected = new LinkedHashSet<>();
		List<UserWord> result = new ArrayList<>();

		for (UserWord uw : pool) {
			if (selected.add(uw.getId())) {
				result.add(uw);
				if (result.size() >= count) break;
			}
		}

		return result;
	}

	/**
	 * 채점 후 가중치 업데이트
	 */
	@Transactional
	public void updateWeight(User user, Word word, boolean correct) {
		UserWord uw = userWordRepository.findByUserAndWord(user, word)
				.orElseThrow(() -> new RuntimeException("UserWord not found"));

		if (correct) {
			uw.setWeight(uw.getWeight() - 1);
		} else {
			uw.setWeight(uw.getWeight() + 1);
		}

		userWordRepository.save(uw);
	}

	/**
	 * 특정 유저의 특정 단어 가중치 조회
	 */
	public int getUserWeight(User user, Word word) {
		return userWordRepository.findByUserAndWord(user, word)
				.map(UserWord::getWeight)
				.orElse(0);
	}

}
