package com.vocabooks.vocabook.service;

import com.vocabooks.vocabook.entity.User;
import com.vocabooks.vocabook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User login(String username, String password) {
		return userRepository.findByUsername(username)
				.filter(u -> u.getPassword().equals(password))
				.orElse(null);
	}
}
