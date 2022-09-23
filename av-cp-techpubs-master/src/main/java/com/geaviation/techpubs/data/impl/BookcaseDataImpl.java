package com.geaviation.techpubs.data.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.data.api.techlib.IBookcaseData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.BookcaseEntity;
import com.geaviation.techpubs.models.techlib.dto.BookcaseBookcaseVersionDto;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionStatusDTO;
import com.geaviation.techpubs.services.impl.BookcaseApp;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class BookcaseDataImpl {

    private static final Logger logger = LogManager.getLogger(BookcaseApp.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    IBookcaseVersionData iBookCaseVersionData;

    @Autowired
    IBookcaseData iBookCaseData;

    @Autowired
    private S3Config s3Config;

    @Autowired
    private AmazonS3ClientFactory amazonS3ClientFactory;



    public List<BookcaseVersionStatusDTO> getBookcaseVersionStatuses(String bookcaseKey) {
        List<BookcaseEntity> bookcases = iBookCaseData.findByBookcaseKeyIgnoreCase(bookcaseKey);

        List<BookcaseVersionStatusDTO> bookcaseVersionStatusDTOS = new ArrayList<>();

        for (BookcaseEntity bookcaseEntity : bookcases) {
            List<BookcaseVersionStatusDTO> bookcaseVersionStatusDTOs = iBookCaseVersionData
                .findBookcaseVersionAndStatus(bookcaseEntity);

            for (BookcaseVersionStatusDTO bookcaseVersionStatusDTO : bookcaseVersionStatusDTOs) {

                boolean fileExists = false;

                AmazonS3 amazonS3Client = null;
                try {
                    amazonS3Client = amazonS3ClientFactory.getS3Client();
                } catch (TechpubsException techpubsException) {
                    logger.info(techpubsException.getMessage());
                }
                String s3ObjKey =
                    bookcaseEntity.getBookcaseKey() + "/" + bookcaseVersionStatusDTO
                        .getVersion() + "/xml/toc.xml";
                fileExists = amazonS3Client
                    .doesObjectExist(s3Config.getS3Bucket().getBucketName(), s3ObjKey);

                if (fileExists) {
                    bookcaseVersionStatusDTOS.add(bookcaseVersionStatusDTO);
                }
            }
        }

        bookcaseVersionStatusDTOS.sort((bv1,bv2) ->
            (bv2.getVersion().toUpperCase()).compareTo(bv1.getVersion().toUpperCase()));

        return bookcaseVersionStatusDTOS;
    }

    //TODO deperecate
    public List<BookcaseBookcaseVersionDto> getBookcasesOnlineVersions() {
        TypedQuery<BookcaseBookcaseVersionDto> query = entityManager
            .createNamedQuery("BookcaseBookcaseVersionDto.findAllWithStatus",
                BookcaseBookcaseVersionDto.class);
        return query.setParameter("status", AppConstants.ONLINE).getResultList();
    }
}
