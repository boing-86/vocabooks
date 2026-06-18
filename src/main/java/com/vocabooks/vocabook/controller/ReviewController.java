package com.vocabooks.vocabook.controller;

import com.vocabooks.vocabook.entity.User;
import com.vocabooks.vocabook.entity.UserWord;
import com.vocabooks.vocabook.entity.Word;
import com.vocabooks.vocabook.repository.WordRepository;
import com.vocabooks.vocabook.service.QuizService;
import com.vocabooks.vocabook.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

	private final ReviewService reviewService;
	private final QuizService quizService;
	private final WordRepository wordRepository;

	@GetMapping
	public String reviewPage(HttpSession session, Model model) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) return "redirect:/login";

		List<UserWord> reviewWords = reviewService.getReviewWords(loginUser);

		model.addAttribute("reviewWords", reviewWords);
		model.addAttribute("reviewCount", reviewWords.size());

		return "review";
	}

	@GetMapping("/quiz")
	public String reviewQuiz(@RequestParam(defaultValue = "EN_TO_KR") String mode,
	                         HttpSession session, Model model) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) return "redirect:/login";

		List<Word> words = reviewService.getReviewQuizWords(loginUser, 10);
		if (words.isEmpty()) return "redirect:/review";

		model.addAttribute("words", words);
		model.addAttribute("mode", mode);
		model.addAttribute("pageTitle", "📚 오답 문제만 풀기");
		model.addAttribute("formAction", "/review/submit");

		return "quiz";
	}

	@PostMapping("/submit")
	public String submitReview(@RequestParam String mode,
	                           @RequestParam List<Long> wordIds,
	                           @RequestParam List<String> answers,
	                           HttpSession session, Model model) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) return "redirect:/login";

		List<Map<String, Object>> items = new ArrayList<>();
		int wrongCount = 0;

		for (int i = 0; i < wordIds.size(); i++) {
			Word word = wordRepository.findById(wordIds.get(i)).orElse(null);
			if (word == null) continue;

			String userAnswer = answers.get(i).trim();
			boolean pass = "EN_TO_KR".equals(mode)
					? word.getMeaning().trim().equalsIgnoreCase(userAnswer)
					: word.getEnglish().trim().equalsIgnoreCase(userAnswer);

			quizService.updateWeight(loginUser, word, pass);

			if (!pass) wrongCount++;

			Map<String, Object> item = new HashMap<>();
			item.put("word", word);
			item.put("answer", userAnswer);
			item.put("pass", pass);
			item.put("weight", quizService.getUserWeight(loginUser, word));
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
}
