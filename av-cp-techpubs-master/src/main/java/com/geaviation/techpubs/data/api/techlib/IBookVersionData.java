package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.BookEntity;
import com.geaviation.techpubs.models.techlib.BookVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IBookVersionData extends JpaRepository<BookVersionEntity, UUID> {
        List<BookVersionEntity> findByBookAndBookcaseVersion(BookEntity book, String bookcaseVersion);
}
