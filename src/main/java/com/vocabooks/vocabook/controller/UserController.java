package com.vocabooks.vocabook.controller;

import com.vocabooks.vocabook.entity.User;
import com.vocabooks.vocabook.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/")
	public String root() {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String username,
	                    @RequestParam String password,
	                    HttpSession session,
	                    Model model) {
		User user = userService.login(username, password);
		if (user == null) {
			model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
			return "login";
		}
		session.setAttribute("loginUser", user);
		return "redirect:/quiz";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}
