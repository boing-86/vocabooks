package com.vocabooks.vocabook.controller;

import com.vocabooks.vocabook.dto.QuizResult;
import com.vocabooks.vocabook.entity.Word;
import com.vocabooks.vocabook.service.QuizService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

	private final QuizService quizService;
	private static final int QUIZ_SIZE = 10;

	/**
	 * 퀴즈 시작 — 모드 선택 후 10개 단어 출제
	 */
	@GetMapping
	public String quizPage(@RequestParam(defaultValue = "EN_TO_KR") String mode,
	                       Model model,
	                       HttpSession session) {
		// 로그인 체크
		if (session.getAttribute("loginUser") == null) {
			return "redirect:/login";
		}

		List<Word> words = quizService.selectQuizWords(QUIZ_SIZE);
		model.addAttribute("words", words);
		model.addAttribute("mode", mode);
		return "quiz";
	}

	/**
	 * 퀴즈 제출 — 채점 후 결과 페이지로
	 */
	@PostMapping("/submit")
	public String submit(@RequestParam List<Long> wordIds,
	                     @RequestParam List<String> answers,
	                     @RequestParam String mode,
	                     Model model,
	                     HttpSession session) {
		if (session.getAttribute("loginUser") == null) {
			return "redirect:/login";
		}

		QuizResult result = quizService.grade(wordIds, answers, mode);
		model.addAttribute("result", result);
		return "result";
	}
}
