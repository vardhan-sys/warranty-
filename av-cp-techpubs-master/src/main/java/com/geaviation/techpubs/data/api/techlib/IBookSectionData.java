package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.BookSectionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookSectionData extends JpaRepository<BookSectionEntity, UUID> {
        List<BookSectionEntity> findByBookId(UUID bookId);
}
