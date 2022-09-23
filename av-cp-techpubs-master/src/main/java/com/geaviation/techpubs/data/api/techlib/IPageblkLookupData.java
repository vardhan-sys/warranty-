package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.PageblkLookupEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.geaviation.techpubs.models.techlib.dto.PageblkLookupDto;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPageblkLookupData extends CrudRepository<PageblkLookupEntity, UUID> {

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.PageblkLookupDto(pb.bookcaseKey, pb.bookcaseVersion, pb.bookKey, pb.onlineFilename, pb.target) " +
            "FROM PageblkLookupEntity pb " +
            "WHERE pb.bookcaseKey = :bookcaseKey " +
            "AND pb.bookcaseVersion = :bookcaseVersion " +
            "AND pb.bookKey = :bookKey " +
            "AND pb.target = :target")
    List<PageblkLookupDto> lookupPageblkByTarget(
            @Param("bookcaseKey") String bookcaseKey,
            @Param("bookcaseVersion") String bookcaseVersion,
            @Param("bookKey") String bookKey,
            @Param("target") String target
    );
    
}
