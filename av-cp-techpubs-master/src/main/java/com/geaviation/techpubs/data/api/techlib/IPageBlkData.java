package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.reviewer.NotifIc;
import com.geaviation.techpubs.models.reviewer.NotifSb;
import com.geaviation.techpubs.models.reviewer.NotifTr;
import com.geaviation.techpubs.models.PageblkDetailsDAO;
import com.geaviation.techpubs.models.SbData;
import com.geaviation.techpubs.models.techlib.PageblkEntity;
import com.geaviation.techpubs.models.techlib.dto.PageBlkDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IPageBlkData extends JpaRepository<PageblkEntity, UUID> {

        @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.PageBlkDto(pageblk.id, publicationType.code, "
            + "pageblk.pageblkKey, booksection.sectionKey, booksection.id, book.bookKey, pageblk.title, "
            + "pageblk.tocTitle, technologyLevel.level, technologyLevel.id) "
            + "FROM PageblkEntity pageblk "
            + "JOIN pageblk.bookSection booksection "
            + "JOIN pageblk.publicationType publicationType "
            + "JOIN pageblk.versions versions "
            + "JOIN versions.technologyLevel technologyLevel "
            + "JOIN booksection.bookId book "
            + "JOIN book.bookcase bookcase "
            + "WHERE versions.technologyLevel IS NOT NULL "
            + "AND (publicationType.code = :pubTypeMan or publicationType.code = :pubTypeIc) "
            + "AND bookcase.bookcaseKey = :bookcaseKey "
            + "AND UPPER(book.bookType) = :bookType "
            + "AND versions.bookcaseVersion = :version "
            + "ORDER BY technologyLevel.level")
        List<PageBlkDto> findSMMDocsByBookcaseKeyAndPublicationTypeAndVersion(
            @Param("bookcaseKey") String bookcaseKey, @Param("bookType") String bookType,
            @Param("pubTypeMan") String pubTypeMan, @Param("pubTypeIc") String pubTypeIc,
            @Param("version") String version);

        @Query("SELECT new com.geaviation.techpubs.models.PageblkDetailsDAO(book.bookKey, "
                + "pageblk.pageblkKey,bookcase.bookcaseKey, publicationType.code,"
                + " pageblk.approvedForPublish, versions.onlineFilename, bcVersion.bookcaseVersion ) "
                + "FROM PageblkEntity pageblk "
                + "JOIN pageblk.bookSection booksection "
                + "JOIN pageblk.publicationType publicationType "
                + "JOIN pageblk.versions versions "
                + "JOIN booksection.bookId book "
                + "JOIN book.bookcase bookcase "
                + "JOIN bookcase.bookcaseVersionId bcVersion "
                + "WHERE bookcase.bookcaseKey = :bookcaseKey "
                + "AND publicationType.code = :publicationType "
                + "AND pageblk.approvedForPublish = :approvedForPublish "
                + "GROUP BY book.bookKey, pageblk.pageblkKey, bookcase.bookcaseKey, "
                + "publicationType.code , pageblk.approvedForPublish, versions.onlineFilename, bcVersion.bookcaseVersion")
        @Cacheable("TechpubsWidgetSbCache")
        List<PageblkDetailsDAO> findSbPageBlksByBookcaseAndTypeAndApprovedForPublish(
                @Param("bookcaseKey") String bookcaseKey, @Param("publicationType") String publicationType ,
                @Param("approvedForPublish") Boolean approvedForPublish);

        @Query("Select COUNT(p) > 0 FROM PageblkEntity p "
            + "JOIN p.bookSection bookSection "
            + "JOIN p.publicationType publicationType "
            + "WHERE publicationType.code = :pubTypeMan "
            + "AND p.pageblkKey = :pageblkKey "
            + "AND bookSection.id = :sectionId")
        Boolean hasManWithPageBlkKeyAndSectionId(@Param("pageblkKey") String pageblkKey,
            @Param("sectionId") UUID sectionId, @Param("pubTypeMan") String pubTypeMan);

        @Query("SELECT count(pageblkv) > 0 "
            + "FROM PageblkVersionEntity pageblkv "
            + "JOIN pageblkv.pageBlk pageblk "
            + "JOIN pageblk.bookSection bookSection "
            + "JOIN bookSection.bookId book "
            + "WHERE book.id = :bookId "
            + "AND pageblkv.bookcaseVersion = :version")
        boolean hasPageblksForBookIdAndVersion(
            @Param("bookId") UUID bookId, @Param("version") String version);

        @Query("SELECT count(pageblkv) > 0 "
            + "FROM PageblkVersionEntity pageblkv "
            + "JOIN pageblkv.pageBlk pageblk "
            + "JOIN pageblk.bookSection bookSection "
            + "JOIN pageblk.publicationType publicationType "
            + "JOIN bookSection.bookId book "
            + "JOIN book.bookcase bookcase "
            + "JOIN bookcase.bookcaseVersionId bookcaseVersion "
            + "WHERE bookcase.id = :bookcaseId "
            + "AND publicationType.code = :publicationType "
            + "AND pageblkv.bookcaseVersion = :version")
        boolean hasPageblksForBookcaseIdAndVersionAndPubType(@Param("bookcaseId") UUID bookcaseId,
            @Param("version") String version, @Param("publicationType") String publicationType);

        @Procedure ("techlib.pageblkisenabledforicao")
        boolean pageblkIsEnabledForIcao(
            @Param("icaoCode") String icaoCode, @Param("filename") String filename,
            @Param("bookcaseKey") String bookcaseKey, @Param("bookKey") String bookKey,
            @Param("bookcaseVersion") String bookcaseVersion);

        @Query("SELECT bookcase.bookcaseKey "
            + "FROM PageblkEntity pageblk "
            + "JOIN pageblk.bookSection bookSection "
            + "JOIN bookSection.bookId book "
            + "JOIN book.bookcase bookcase "
            + "WHERE pageblk.id = :pageblkId")
        String findBookcaseKeyFromPageBlkId(@Param("pageblkId") UUID pageblkId);

        @Query("SELECT book.bookKey "
            + "FROM PageblkEntity pageblk "
            + "JOIN pageblk.bookSection booksection "
            + "JOIN booksection.bookId book "
            + "WHERE pageblk.id = :pageblkId")
        String findBookKeyFromPageBlk(@Param("pageblkId") UUID pageblkId);

        @Query("SELECT new com.geaviation.techpubs.models.SbData(pageblkv.onlineFilename, "
                + "pageblkv.revisionNumber) "
                + "FROM PageblkVersionEntity pageblkv "
                + "WHERE pageblkv.onlineFilename LIKE %:sbnbr% "
                + "AND pageblkv.bookcaseVersion = :bookcaseVersion "
                + "AND pageblkv.filepath LIKE :filepath%")
        List<SbData> findOnlineSbFilesFromPageBlk(@Param("filepath") String filepath,
            @Param("bookcaseVersion") String bookcaseVersion,
            @Param("sbnbr") String sbnbr);

        @Query("SELECT new com.geaviation.techpubs.models.PageblkDetailsDAO(book.bookKey, bVersion.title, "
            + "versions.onlineFilename,  pageblk.id, pageblk.approvedForPublish, pageblk.pageblkKey, pageblk.title, versions.revisionDate, "
            + "bookcase.bookcaseKey, bcVersion.title, publicationType.code, pageblk.metadata, bcVersion.bookcaseVersion,versions.revisionNumber ) "
            + "FROM PageblkEntity pageblk "
            + "JOIN pageblk.bookSection booksection "
            + "JOIN pageblk.publicationType publicationType "
            + "JOIN pageblk.versions versions "
            + "JOIN booksection.bookId book "
            + "JOIN book.bookcase bookcase "
            + "JOIN book.versions bVersion "
            + "JOIN bookcase.bookcaseVersionId bcVersion "
            + "WHERE bookcase.bookcaseKey = :bookcaseKey "
            + "AND book.bookKey = :bookKey "
            + "AND versions.onlineFilename = :onlineFilename "
            + "AND versions.bookcaseVersion = :version "
            + "AND bVersion.bookcaseVersion = :version "
            + "AND bcVersion.bookcaseVersion = :version "
            + "ORDER BY pageblk.publicationType desc")
        List<PageblkDetailsDAO> findPageBlkByVersionFileName(
            @Param("bookcaseKey") String bookcaseKey,
            @Param("version") String version,
            @Param("bookKey") String bookKey,
            @Param("onlineFilename") String onlineFilename);

        @Query("SELECT new com.geaviation.techpubs.models.PageblkDetailsDAO(book.bookKey, bVersion.title, "
            + "'', '', '', "
            + "bookcase.bookcaseKey, bcVersion.title, '', bcVersion.bookcaseVersion) "
            + "FROM BookEntity book "
            + "JOIN book.bookcase bookcase "
            + "JOIN book.versions bVersion "
            + "JOIN bookcase.bookcaseVersionId bcVersion "
            + "WHERE bookcase.bookcaseKey = :bookcaseKey "
            + "AND book.bookKey = :bookKey "
            + "AND bVersion.bookcaseVersion = :version "
            + "AND bcVersion.bookcaseVersion = :version ")
        List<PageblkDetailsDAO> findBookByVersion(
            @Param("bookcaseKey") String bookcaseKey,
            @Param("version") String version,
            @Param("bookKey") String bookKey);

            @Modifying
            @Transactional
            @Query("UPDATE PageblkEntity pageblk \n" +
                    "SET approvedForPublish = true, \n" +
                    "email_notification = :emailNotification \n" +
                    "WHERE pageblk.id = :pageblkId \n" +
                    "AND pageblk_key = :pageblkKey\n")
            int updateApprovedForPublishFlagToTrue(@Param("emailNotification") boolean emailNotification,@Param("pageblkId") UUID pageblkId,
                                                   @Param("pageblkKey") String pageblkKey);


        @Query("SELECT new com.geaviation.techpubs.models.reviewer.NotifIc(" +
                "      pageblk.metadata  \n" +
                "     , pageblk_version.onlineFilename \n" +
                "     , book.bookType \n" +
                "     , book.bookKey \n" +
                "     , pageblk.tocTitle \n" +
                "     , bookcase.bookcaseKey \n" +
                "     , ep.program \n" +
                ", pageblk_version.revisionDate) \n" +
                "FROM PageblkEntity pageblk \n" +
                "         INNER JOIN pageblk.versions pageblk_version \n" +
                "         INNER JOIN pageblk.bookSection book_section \n" +
                "         INNER JOIN book_section.bookId book \n" +
                "         INNER JOIN book.bookcase bookcase \n" +
                "         INNER JOIN bookcase.bookcaseVersionId bookcase_version \n" +
                "         inner JOIN EngineProgramEntity as ep on bookcase.bookcaseKey = ep.bookcaseKey \n" +
                "where pageblk.publicationType.code = 'ic' \n" +
                "and pageblk.approvedForPublish = true \n" +
                "and pageblk.emailNotification = true \n" +
                "and bookcase_version.bookcaseVersion = pageblk_version.bookcaseVersion \n" +
                "and bookcase_version.bookcaseVersionStatus = 'online'"
        )
        ArrayList<NotifIc> getIcNotifications();


        @Query("SELECT  new com.geaviation.techpubs.models.reviewer.NotifSb( ep.program \n" +
                "     , pageblk.metadata \n" +
                "     , pageblk_version.onlineFilename \n" +
                "     , pageblk_version.revisionDate \n" +
                "     , pageblk_version.revisionNumber \n" +
                "     , pageblk.tocTitle  \n" +
                "     , bookcase.bookcaseKey \n" +
                "     , book.bookKey \n" +
                "     , pageblk.publicationType.code" +
                "     , pageblk_version.bookcaseVersion) \n" +
                "FROM PageblkEntity pageblk \n" +
                "         INNER JOIN pageblk.versions pageblk_version \n" +
                "         INNER JOIN pageblk.bookSection book_section \n" +
                "         INNER JOIN book_section.bookId book \n" +
                "         INNER JOIN book.bookcase bookcase \n" +
                "         INNER JOIN bookcase.bookcaseVersionId bookcase_version \n" +
                "         inner JOIN EngineProgramEntity as ep on bookcase.bookcaseKey = ep.bookcaseKey \n" +
                "where pageblk.publicationType.code = 'sb' \n" +
                "and pageblk.approvedForPublish = true \n" +
                "and pageblk.emailNotification = true \n" +
                "and bookcase_version.bookcaseVersion = pageblk_version.bookcaseVersion \n" +
                "and bookcase_version.bookcaseVersionStatus = 'online'"
        )
        List<NotifSb> getSbNotifications();

        @Query("SELECT  new com.geaviation.techpubs.models.reviewer.NotifTr( \n" +
                "       ep.program \n " +
                "     , pageblk.metadata \n" +
                "     , pageblk_version.onlineFilename \n" +
                "     , book.bookType \n" +
                "     , pageblk_version.revisionDate \n" +
                "     , pageblk.pageblkKey \n" +
                "     , pageblk.tocTitle  \n" +
                "     , bookcase.bookcaseKey \n" +
                "     , book.bookKey \n" +
                "     , pageblk.publicationType.code) \n" +
                "FROM PageblkEntity pageblk \n" +
                "         INNER JOIN pageblk.versions pageblk_version \n" +
                "         INNER JOIN pageblk.bookSection book_section \n" +
                "         INNER JOIN book_section.bookId book \n" +
                "         INNER JOIN book.bookcase bookcase \n" +
                "         INNER JOIN bookcase.bookcaseVersionId bookcase_version \n" +
                "         inner JOIN EngineProgramEntity as ep on bookcase.bookcaseKey = ep.bookcaseKey \n" +
                "where pageblk.publicationType.code = 'tr'" +
                "and pageblk.approvedForPublish = true \n " +
                "and pageblk.emailNotification = true \n" +
                "and bookcase_version.bookcaseVersion = pageblk_version.bookcaseVersion \n" +
                "and bookcase_version.bookcaseVersionStatus = 'online'"
                )
        List<NotifTr> getTRNotifications();

        @Modifying
        @Transactional
        @Query("UPDATE PageblkEntity pageblk " +
                "SET emailNotification = false " +
                "WHERE pageblk.publicationType.code = :type " +
                "and pageblk.approvedForPublish = true")
        int disableNotificationFlag(@Param("type") String type);

        @Cacheable("TechpubsSbCache")
        @Query("SELECT distinct pageblk.pageblkKey "
                + "FROM PageblkEntity pageblk "
                + "JOIN pageblk.bookSection booksection "
                + "JOIN pageblk.publicationType publicationType "
                + "JOIN pageblk.versions versions "
                + "JOIN booksection.bookId book "
                + "JOIN book.bookcase bookcase "
                + "JOIN bookcase.bookcaseVersionId bcVersion "
                + "WHERE bookcase.bookcaseKey = :bookcaseKey "
                + "AND publicationType.code = 'sb' "
                + "AND pageblk.approvedForPublish = true")
        List<String> findPageBlksKeysForSbType(
                @Param("bookcaseKey") String bookcaseKey);

        //Group by for performance enhancements
        @Query("SELECT new com.geaviation.techpubs.models.PageblkDetailsDAO(book.bookKey, "
                +"pageblk.pageblkKey, bookcase.bookcaseKey, publicationType.code,"
                + "pageblk.approvedForPublish, versions.onlineFilename, bcVersion.bookcaseVersion ) "
                + "FROM PageblkEntity pageblk "
                + "JOIN pageblk.bookSection booksection "
                + "JOIN pageblk.publicationType publicationType "
                + "JOIN pageblk.versions versions "
                + "JOIN booksection.bookId book "
                + "JOIN book.bookcase bookcase "
                + "JOIN bookcase.bookcaseVersionId bcVersion "
                + "WHERE bookcase.bookcaseKey = :bookcaseKey "
                + "AND publicationType.code = :publicationType "
                + "AND pageblk.approvedForPublish = :approvedForPublish "
                + "AND bcVersion.bookcaseVersion = :version "
                + "AND versions.bookcaseVersion = :version "
                + "GROUP BY book.bookKey, pageblk.pageblkKey, bookcase.bookcaseKey, "
                + "publicationType.code , pageblk.approvedForPublish, versions.onlineFilename, bcVersion.bookcaseVersion")
        @Cacheable("TechpubsWidgetCache")
        List<PageblkDetailsDAO> findGroupByPageBlksByBookcaseAndTypeAndApprovedForPublish(
                @Param("bookcaseKey") String bookcaseKey, @Param("publicationType") String publicationType ,
                @Param("approvedForPublish") Boolean approvedForPublish,
                @Param("version") String version);

        @Query("SELECT new com.geaviation.techpubs.models.PageblkDetailsDAO(book.bookKey, bVersion.title, "
                + "versions.onlineFilename,  pageblk.id, pageblk.approvedForPublish, pageblk.pageblkKey, pageblk.title, versions.revisionDate, "
                + "bookcase.bookcaseKey, bcVersion.title, publicationType.code, pageblk.metadata, bcVersion.bookcaseVersion, versions.revisionNumber ) "
                + "FROM PageblkEntity pageblk "
                + "JOIN pageblk.bookSection booksection "
                + "JOIN pageblk.publicationType publicationType "
                + "JOIN pageblk.versions versions "
                + "JOIN booksection.bookId book "
                + "JOIN book.bookcase bookcase "
                + "JOIN book.versions bVersion "
                + "JOIN bookcase.bookcaseVersionId bcVersion "
                + "WHERE bookcase.bookcaseKey = :bookcaseKey "
                + "AND versions.onlineFilename = :onlineFilename "
                + "AND versions.bookcaseVersion = :version "
                + "AND bVersion.bookcaseVersion = :version "
                + "AND bcVersion.bookcaseVersion = :version "
                + "ORDER BY pageblk.publicationType desc")
        List<PageblkDetailsDAO> findPageBlkByVersionAndFileName(
                @Param("bookcaseKey") String bookcaseKey,
                @Param("version") String version,
                @Param("onlineFilename") String onlineFilename);

        @Cacheable("TechpubsTocSbCache")
        @Query("SELECT distinct new com.geaviation.techpubs.models.PageblkDetailsDAO(pageblk.pageblkKey , versions.revisionNumber ) "
                + "FROM PageblkEntity pageblk "
                + "JOIN pageblk.bookSection booksection "
                + "JOIN pageblk.publicationType publicationType "
                + "JOIN pageblk.versions versions "
                + "JOIN booksection.bookId book "
                + "JOIN book.bookcase bookcase "
                + "JOIN bookcase.bookcaseVersionId bcVersion "
                + "WHERE bookcase.bookcaseKey = :bookcaseKey "
                + "AND publicationType.code = 'sb' "
                + "AND pageblk.approvedForPublish = true")
        List<PageblkDetailsDAO> findPageBlksKeysAndRevisionForSbType(
                @Param("bookcaseKey") String bookcaseKey);

}

