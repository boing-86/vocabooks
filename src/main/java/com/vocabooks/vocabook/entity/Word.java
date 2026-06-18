package com.vocabooks.vocabook.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Word {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String english;

	@Column(nullable = false)
	private String meaning;

	private String example;

	// weight 제거 → UserWord로 이동
}
