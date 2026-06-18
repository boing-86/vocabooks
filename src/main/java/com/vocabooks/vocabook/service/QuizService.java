package com.vocabooks.vocabook.service;

import com.vocabooks.vocabook.dto.QuizItem;
import com.vocabooks.vocabook.dto.QuizResult;
import com.vocabooks.vocabook.entity.Word;
import com.vocabooks.vocabook.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {

	private final WordRepository wordRepository;

	/**
	 * 가중치 기반 랜덤으로 count개 단어 선택
	 * weight가 높을수록 더 자주 출제됨
	 */
	public List<Word> selectQuizWords(int count) {
		List<Word> all = wordRepository.findAll();

		if (all.size() <= count) {
			Collections.shuffle(all);
			return all;
		}

		// 가중치 풀 구성: weight + 1 만큼 반복 삽입
		List<Word> pool = new ArrayList<>();
		for (Word w : all) {
			int freq = w.getWeight() + 1;
			for (int i = 0; i < freq; i++) {
				pool.add(w);
			}
		}

		Collections.shuffle(pool);

		// 중복 제거하며 선택
		Set<Long> pickedIds = new LinkedHashSet<>();
		List<Word> result = new ArrayList<>();
		for (Word w : pool) {
			if (pickedIds.add(w.getId())) {
				result.add(w);
			}
			if (result.size() >= count) break;
		}

		return result;
	}

	/**
	 * 채점 + weight 갱신
	 * mode: "EN_TO_KR" → 영어 보고 한국어 답
	 *        "KR_TO_EN" → 한국어 보고 영어 답
	 */
	public QuizResult grade(List<Long> wordIds, List<String> answers, String mode) {
		List<QuizItem> items = new ArrayList<>();
		int wrongCount = 0;

		for (int i = 0; i < wordIds.size(); i++) {
			Word word = wordRepository.findById(wordIds.get(i)).orElseThrow();
			String userAnswer = answers.get(i).trim();

			// 정답 판별
			String correctAnswer = mode.equals("EN_TO_KR")
					? word.getMeaning()
					: word.getEnglish();

			boolean pass = correctAnswer.trim().equalsIgnoreCase(userAnswer);

			// weight 갱신
			if (pass) {
				word.setWeight(Math.max(0, word.getWeight() - 1));
			} else {
				word.setWeight(word.getWeight() + 1);
				wrongCount++;
			}
			wordRepository.save(word);

			items.add(new QuizItem(word, userAnswer, pass));
		}

		return new QuizResult(items, wrongCount, items.size(), mode);
	}
}
