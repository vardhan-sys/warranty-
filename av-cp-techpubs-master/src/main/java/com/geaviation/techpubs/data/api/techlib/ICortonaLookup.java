package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.CortonaLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.CortonaTargetDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICortonaLookup  extends JpaRepository<CortonaLookupEntity, String> {

  @Query("SELECT c FROM CortonaLookupEntity c " +
      "WHERE c.htmlFileName = :html_filename " +
      "AND (c.bookcaseVersion = :bookcase_version or c.bookcaseVersion = 'program' )" +
      "AND c.bookcase = :bookcase " +
      "AND c.book = :book")
  CortonaLookupEntity findCortonaFile(@Param("html_filename") String html_filename,
      @Param("bookcase_version") String bookcase_version, @Param("bookcase") String bookcase, @Param("book") String book);

  @Query("SELECT DISTINCT(c.book) FROM CortonaLookupEntity c")
  List<String> findDistinctBooks();

  @Query("SELECT NEW com.geaviation.techpubs.models.techlib.dto.CortonaTargetDto(pb.pageblkKey) " +
          "FROM CortonaLookupEntity c " +
          "INNER JOIN PageblkLookupEntity pb ON " +
          "c.bookcase = pb.bookcaseKey " +
          "AND (c.bookcaseVersion = pb.bookcaseVersion or c.bookcaseVersion = 'program' ) " +
          "AND c.book = pb.bookKey " +
          "AND c.htmlFileName = pb.onlineFilename " +
          "WHERE pb.bookcaseKey = :bookcaseKey " +
          "AND pb.bookcaseVersion = :bookcaseVersion " +
          "AND pb.bookKey = :bookKey " +
          "AND pb.target = :target")
  List<CortonaTargetDto> findCortonaFilesByTarget(
          @Param("bookcaseKey") String bookcaseKey,
          @Param("bookcaseVersion") String bookcaseVersion,
          @Param("bookKey") String bookKey,
          @Param("target") String target
  );
}
