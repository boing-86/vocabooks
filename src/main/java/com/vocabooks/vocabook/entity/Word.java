package com.vocabooks.vocabook.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "word")
public class Word {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String english;

	@Column(nullable = false, length = 255)
	private String meaning;

	@Column(length = 500)
	private String example;

	@Column(nullable = false)
	private int weight = 0;


	protected Word() {

	}

	public Word(String english, String meaning, String example) {
		this.english = english;
		this.meaning = meaning;
		this.example = example;
		this.weight = 0;
	}

	public Long getId() {
		return id;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public void increaseWeight() {
		this.weight++;
	}

	public void decreaseWeight() {
		if (this.weight > 0) this.weight--;
	}
}
