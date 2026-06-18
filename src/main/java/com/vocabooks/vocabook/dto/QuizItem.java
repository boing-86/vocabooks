package com.vocabooks.vocabook.dto;

import com.vocabooks.vocabook.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizItem {
	private Word word;        // 출제된 단어
	private String answer;    // 사용자가 입력한 답
	private boolean pass;     // 정답 여부
}
