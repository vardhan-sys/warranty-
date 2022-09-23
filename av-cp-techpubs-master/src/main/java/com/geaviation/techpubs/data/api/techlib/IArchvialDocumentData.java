package com.geaviation.techpubs.data.api.techlib;

import java.util.List;
import java.util.UUID;

import com.geaviation.techpubs.models.ArchivalPdfS3DataDAO;
import com.geaviation.techpubs.models.PageblkDetailsDAO;
import com.geaviation.techpubs.models.techlib.ArchivalDocumentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IArchvialDocumentData extends JpaRepository<ArchivalDocumentsEntity, UUID> {

    @Query ("select new com.geaviation.techpubs.models.ArchivalPdfS3DataDAO(ad.fileName, ad.filePath)\n" +
            "from ArchivalDocumentsEntity as ad\n" +
            "JOIN ad.engineModels m " +
            "where m.model = :model \n" +
            "and ad.type = :type \n" +
            "and ad.fileName = :fileName\n")
    List<ArchivalPdfS3DataDAO> findArchiveDocument(
            @Param("model") String model,
            @Param("type") String type,
            @Param("fileName") String fileName);

}
