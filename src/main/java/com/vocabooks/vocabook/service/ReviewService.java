package com.vocabooks.vocabook.service;

import com.vocabooks.vocabook.entity.User;
import com.vocabooks.vocabook.entity.UserWord;
import com.vocabooks.vocabook.entity.Word;
import com.vocabooks.vocabook.repository.UserWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final UserWordRepository userWordRepository;

	public List<UserWord> getReviewWords(User user) {
		return userWordRepository.findByUserAndWeightGreaterThanOrderByWeightDesc(user, 0);
	}

	public List<Word> getReviewQuizWords(User user, int count) {
		return getReviewWords(user).stream()
				.limit(count)
				.map(UserWord::getWord)
				.toList();
	}
}
