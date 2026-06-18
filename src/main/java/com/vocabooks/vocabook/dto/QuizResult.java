package com.vocabooks.vocabook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class QuizResult {
	private List<QuizItem> items;
	private int wrongCount;
	private int totalCount;
	private String mode;
}
