package com.vocabooks.vocabook.controller;

import com.vocabooks.vocabook.entity.User;
import com.vocabooks.vocabook.entity.UserWord;
import com.vocabooks.vocabook.entity.Word;
import com.vocabooks.vocabook.repository.WordRepository;
import com.vocabooks.vocabook.service.QuizService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class QuizController {

	private final QuizService quizService;
	private final WordRepository wordRepository;

	@GetMapping("/quiz")
	public String quizPage(@RequestParam(defaultValue = "EN_TO_KR") String mode,
	                       HttpSession session,
	                       Model model) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) return "redirect:/login";

		// 유저별 가중치 기반 10문제 선택
		List<UserWord> quizWords = quizService.getQuizWords(loginUser, 10);

		// 템플릿에 전달할 단어 목록 추출
		List<Word> words = quizWords.stream()
				.map(UserWord::getWord)
				.toList();

		model.addAttribute("words", words);
		model.addAttribute("mode", mode);

		return "quiz";
	}

	@PostMapping("/quiz/submit")
	public String submitQuiz(@RequestParam String mode,
	                         @RequestParam List<Long> wordIds,
	                         @RequestParam List<String> answers,
	                         HttpSession session,
	                         Model model) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) return "redirect:/login";

		List<Map<String, Object>> items = new ArrayList<>();
		int wrongCount = 0;

		for (int i = 0; i < wordIds.size(); i++) {
			Word word = wordRepository.findById(wordIds.get(i)).orElse(null);
			if (word == null) continue;

			String userAnswer = answers.get(i).trim();
			boolean pass;

			if ("EN_TO_KR".equals(mode)) {
				pass = containsAnswer(word.getMeaning(), userAnswer);
			} else {
				pass = containsAnswer(word.getEnglish(), userAnswer);
			}

			// 유저별 가중치 업데이트
			quizService.updateWeight(loginUser, word, pass);

			// 업데이트 후 현재 가중치 조회
			int currentWeight = quizService.getUserWeight(loginUser, word);

			if (!pass) wrongCount++;

			Map<String, Object> item = new HashMap<>();
			item.put("word", word);
			item.put("answer", userAnswer);
			item.put("pass", pass);
			item.put("weight", currentWeight);
			items.add(item);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("items", items);
		result.put("totalCount", wordIds.size());
		result.put("wrongCount", wrongCount);
		result.put("mode", mode);

		model.addAttribute("result", result);

		return "result";
	}

	private boolean containsAnswer(String correctValue, String userAnswer) {
		return Arrays.stream(correctValue.split("[,/]"))
				.map(String::trim)
				.anyMatch(s -> s.equalsIgnoreCase(userAnswer));
	}
}
