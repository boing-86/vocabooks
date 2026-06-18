package com.vocabooks.vocabook.repository;

import com.vocabooks.vocabook.entity.User;
import com.vocabooks.vocabook.entity.UserWord;
import com.vocabooks.vocabook.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWordRepository extends JpaRepository<UserWord, Long> {

	// 특정 유저의 모든 단어+가중치 조회
	List<UserWord> findByUser(User user);

	// 특정 유저 + 특정 단어의 가중치 조회
	Optional<UserWord> findByUserAndWord(User user, Word word);

	// 특정 유저의 단어가 존재하는지 확인
	boolean existsByUserAndWord(User user, Word word);

	List<UserWord> findByUserAndWeightGreaterThanOrderByWeightDesc(User user, int weight);
}
