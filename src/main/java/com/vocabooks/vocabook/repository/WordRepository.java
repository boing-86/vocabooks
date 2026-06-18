package com.vocabooks.vocabook.repository;

import com.vocabooks.vocabook.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
}
