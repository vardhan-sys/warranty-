package com.geaviation.techpubs.models.techlib;

import com.geaviation.techpubs.models.techlib.dto.BookcaseWithOnlineVersionDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "bookcase", schema = "techlib")
@SqlResultSetMapping(
        name = "publisherSummaryMapping",
        classes = {
                @ConstructorResult(
                        targetClass = BookcaseWithOnlineVersionDto.class,
                        columns = {
                                @ColumnResult(name = "bookcaseTitle"),
                                @ColumnResult(name = "bookcaseKey"),
                                @ColumnResult(name = "engineFamily"),
                                @ColumnResult(name = "onlineVersion"),
                                @ColumnResult(name = "sendEmail"),
                                @ColumnResult(name = "lastEmailSentDate")
                        }
                )
        }
)
@NamedNativeQuery(
        name = "BookcaseEntity.findBookcasesWithOnlineVersion",
        query = "SELECT pub_summary.bookcaseTitle, pub_summary.bookcaseKey, pub_summary.engineFamily, pub_summary.onlineVersion, pub_summary.sendEmail, pub_summary.lastEmailSentDate " +
                "FROM ( " +
                "SELECT bookcases.title AS bookcaseTitle, bookcases.bookcase_key AS bookcaseKey, bookcases.family AS engineFamily, online_bookcases.bookcase_version AS onlineVersion, bookcases.send_email as sendEmail, bookcases.last_email_sent_date as lastEmailSentDate " +
                "FROM ( " +
                "SELECT DISTINCT ON (b.bookcase_key) b.bookcase_key, bv.title, em.family, b.send_email, b.last_email_sent_date " +
                "FROM bookcase b " +
                "JOIN bookcase_version bv on b.id = bv.bookcase_id " +
                "JOIN engine_model_program emp on b.bookcase_key = emp.bookcase_key " +
                "JOIN engine_model em on emp.engine_model = em.model " +
                ") bookcases " +
                "LEFT OUTER JOIN ( " +
                "SELECT DISTINCT ON (b.bookcase_key) b.bookcase_key, bv.title, bv.bookcase_version, bv.bookcase_version_status_code, em.family, b.send_email, b.last_email_sent_date " +
                "FROM bookcase b " +
                "JOIN bookcase_version bv on b.id = bv.bookcase_id " +
                "JOIN engine_model_program emp on b.bookcase_key = emp.bookcase_key " +
                "JOIN engine_model em on emp.engine_model = em.model " +
                "WHERE bv.bookcase_version_status_code = 'online' " +
                ") online_bookcases ON bookcases.bookcase_key = online_bookcases.bookcase_key " +
                "UNION " +
                "SELECT spm.title, spm.bookcase_key, 'SPM', spm.bookcase_version, spm.send_email, spm.last_email_sent_date " +
                "FROM ( " +
                "SELECT DISTINCT ON (b.bookcase_key) b.bookcase_key, bv.title, bv.bookcase_version, bv.bookcase_version_status_code, b.send_email, b.last_email_sent_date " +
                "FROM bookcase b " +
                "JOIN bookcase_version bv on b.id = bv.bookcase_id " +
                "JOIN engine_program ep on b.bookcase_key = ep.bookcase_key " +
                "WHERE ep.bookcase_key = 'gek119360' OR ep.bookcase_key = 'gek108792' " +
                "AND bv.bookcase_version_status_code = 'online' " +
                ") spm " +
                "LEFT OUTER JOIN ( " +
                "SELECT DISTINCT ON(b.bookcase_key) b.bookcase_key, bv.bookcase_version, bv.bookcase_version_status_code, b.send_email, b.last_email_sent_date " +
                "FROM bookcase b " +
                "JOIN bookcase_version bv on b.id = bv.bookcase_id " +
                "JOIN engine_program ep on b.bookcase_key = ep.bookcase_key " +
                ") spm_online ON spm.bookcase_key = spm_online.bookcase_key ) AS pub_summary " +
                "WHERE :searchTerm = '' OR " +
                "(LOWER(pub_summary.bookcaseTitle) LIKE LOWER(CONCAT('%',:searchTerm,'%')) OR " +
                "LOWER(pub_summary.bookcaseKey) LIKE LOWER(CONCAT('%',:searchTerm,'%')) OR " +
                "LOWER(pub_summary.engineFamily) LIKE LOWER(CONCAT('%',:searchTerm,'%')) OR " +
                "LOWER(pub_summary.onlineVersion) LIKE LOWER(CONCAT('%',:searchTerm,'%'))) " +
                "ORDER BY " +
                "CASE WHEN :column = 'bookcaseTitle' AND :order = 'asc' THEN pub_summary.bookcaseTitle END, " +
                "CASE WHEN :column = 'bookcaseTitle' AND :order = 'desc' THEN pub_summary.bookcaseTitle END DESC, " +
                "CASE WHEN :column = 'bookcaseKey' AND :order = 'asc' THEN pub_summary.bookcaseKey END, " +
                "CASE WHEN :column = 'bookcaseKey' AND :order = 'desc' THEN pub_summary.bookcaseKey END DESC, " +
                "CASE WHEN :column = 'engineFamily' AND :order = 'asc' THEN pub_summary.engineFamily END, " +
                "CASE WHEN :column = 'engineFamily' AND :order = 'desc' THEN pub_summary.engineFamily END DESC, " +
                "CASE WHEN :column = 'onlineVersion' AND :order = 'asc' THEN (LENGTH(pub_summary.onlineVersion), pub_summary.onlineVersion) END, " +
                "CASE WHEN :column = 'onlineVersion' AND :order = 'desc' THEN (LENGTH(pub_summary.onlineVersion), pub_summary.onlineVersion) END DESC;",
        resultSetMapping = "publisherSummaryMapping"
)
public class BookcaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "bookcase_key", nullable = false, length = -1)
    private String bookcaseKey;

    @Column(name = "foldername", nullable = false, length = -1)
    private String foldername;

    @Column(name = "sb_model", length = -1)
    private String sbModel;

    @Column(name = "info", length = -1)
    private String info;

    @Column(name = "send_email")
    private Boolean sendEmail;

    @UpdateTimestamp
    @Column(name = "last_email_sent_date")
    private Date lastEmailSentDate;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "bookcase_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private List<BookcaseVersionEntity> bookcaseVersionId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBookcaseKey() { return bookcaseKey; }

    public void setBookcaseKey(String bookcaseKey) {
        this.bookcaseKey = bookcaseKey;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public String getSbModel() {
        return sbModel;
    }

    public void setSbModel(String sbModel) { this.sbModel = sbModel; }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public Date getLastEmailSentDate() { return lastEmailSentDate; }

    public void setLastEmailSentDate(Date lastEmailSentDate) {
        this.lastEmailSentDate = lastEmailSentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookcaseEntity that = (BookcaseEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookcaseKey, that.bookcaseKey) &&
                Objects.equals(foldername, that.foldername) &&
                Objects.equals(sbModel, that.sbModel) &&
                Objects.equals(info, that.info) &&
                Objects.equals(sendEmail, that.sendEmail) &&
                Objects.equals(lastEmailSentDate, that.lastEmailSentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookcaseKey, foldername, sbModel, info, sendEmail, lastEmailSentDate);
    }
}
