package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.BookDAO;
import com.geaviation.techpubs.models.techlib.BookEntity;
import com.geaviation.techpubs.models.techlib.BookcaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public interface IBookData extends JpaRepository<BookEntity, UUID> {
        @Query("SELECT book "
                   + "FROM BookEntity as book "
                   + "JOIN book.bookcase as bookcase "
                   + "WHERE bookcase.bookcaseKey = :bookcaseKey")
        List<BookEntity> findByBookcaseKey(@Param("bookcaseKey") String bookcaseKey);

        @Query("SELECT new com.geaviation.techpubs.models.BookDAO(book.bookKey, version.title, version.revisionDate, "
            + "version.revision, bookcase.bookcaseKey, bcVersion.title, bcVersion.bookcaseVersion) "
                   + "FROM BookEntity as book "
                   + "JOIN book.versions as version "
                   + "JOIN book.bookcase as bookcase "
            + "JOIN bookcase.bookcaseVersionId bcVersion "
                   + "WHERE bookcase.bookcaseKey = :bookcaseKey "
            + "AND version.bookcaseVersion = :version "
            + "AND bcVersion.bookcaseVersion = :version")
        List<BookDAO> findByBookcaseKeyAndVersion(@Param("bookcaseKey") String bookcaseKey, @Param("version") String version);

        @Query("SELECT COUNT(c) > 0 FROM CompanyEngineBookEnablementEntity c WHERE " +
                   "c.bookId = :id and c.icaoCode = :icaoCode and c.engineModel = :engineModel")
        Boolean isPreviouslyEnabled(@Param("id") UUID id, String icaoCode, String engineModel);

        @Query("SELECT book "
                + "FROM BookEntity as book "
                + "JOIN book.versions as versions "
                + "WHERE book.bookcase = :bookcase "
                + "AND versions.bookcaseVersion = :version")
        List<BookEntity> findByBookcaseAndBookcaseVersion(BookcaseEntity bookcase, String version);

        @Query("SELECT book "
                + "FROM BookEntity as book "
                + "JOIN book.versions as versions "
                + "WHERE book.bookcase = :bookcase "
                + "AND versions.bookcaseVersion = :version "
                + "AND book.bookKey = :bookKey")
        List<BookEntity> findByBookcaseAndBookKeyAndVersion(BookcaseEntity bookcase, String bookKey, String version);

        @Query("SELECT  distinct ep.program AS bookcase_title \n" +
                "FROM BookcaseEntity bookcase \n" +
                "INNER JOIN bookcase.bookcaseVersionId bookcase_version " +
                "INNER JOIN EngineProgramEntity as ep on bookcase.bookcaseKey = ep.bookcaseKey \n")
        ArrayList<String> getEngineModelsNotificationList();
}
