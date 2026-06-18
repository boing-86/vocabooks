package com.vocabooks.vocabook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll()       // 모든 요청 허용
				)
				.formLogin(form -> form.disable())  // Security 기본 로그인 폼 끄기
				.logout(logout -> logout.disable())
				.headers(headers -> headers
						.frameOptions(frame -> frame.sameOrigin())
				);

		return http.build();
	}
}
