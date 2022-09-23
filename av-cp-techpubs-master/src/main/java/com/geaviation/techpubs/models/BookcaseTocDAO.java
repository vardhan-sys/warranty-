package com.geaviation.techpubs.models;

import com.geaviation.techpubs.data.util.BookcaseTocDaoNodeOrderComparator;
import com.geaviation.techpubs.data.util.DataUtil;
import com.geaviation.techpubs.models.techlib.BookSectionEntity;
import com.geaviation.techpubs.models.techlib.BookSectionVersionEntity;
import com.geaviation.techpubs.models.techlib.BookVersionEntity;
import com.geaviation.techpubs.models.techlib.PageblkVersionEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class BookcaseTocDAO implements Serializable {

    private String id;
    private String parentId;
    private String fileExtension;
    private String type;
    private String revisionDate;
    private String title;
    private String tocTitle;
    private String resourceUri;
    private BookcaseTocDAO parentNode;
    private int order;
    private boolean isRoot;
    private boolean isLeaf;
    private String fileName;
    private String key;
    private int treeDepth;
    private List<BookcaseTocDAO> children = new ArrayList<>();
    private boolean hasChildren = false;
    private boolean isRevised = false;
    //This Flag is used for reviewer role: if false then pageblk is pending approval
    private boolean isApprovedForPublish = false;

    @XmlTransient
    @JsonIgnore
    public boolean isApprovedForPublishICTR() {
        return isApprovedForPublishICTR;
    }

    public void setApprovedForPublishICTR(boolean approvedForPublishICTR) {
        isApprovedForPublishICTR = approvedForPublishICTR;
    }

    private boolean isApprovedForPublishICTR = false;
    private String publicationTypeCode;
    //This Flag is used for reviewer role: if true then book is not approved
    private boolean isPendingApproval = false;

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    public BookcaseTocDAO() {
    }

    public BookcaseTocDAO(BookcaseTocDAO bookcaseTocDAO) {
        setId(bookcaseTocDAO.getId());
        setParentId(bookcaseTocDAO.getParentId());
        setFileExtension(bookcaseTocDAO.getFileExtension());
        setType(bookcaseTocDAO.getType());
        setRevisionDate(bookcaseTocDAO.getRevisionDate());
        setTitle(bookcaseTocDAO.getTitle());
        setTocTitle(bookcaseTocDAO.getTocTitle());
        setResourceUri(bookcaseTocDAO.getResourceUri());
        setParent(bookcaseTocDAO.getParent());
        setOrder(bookcaseTocDAO.getOrder());
        setIsRoot(bookcaseTocDAO.getIsRoot());
        setIsLeaf(bookcaseTocDAO.getIsLeaf());
        setFileName(bookcaseTocDAO.getFileName());
        setKey(bookcaseTocDAO.getKey());
        setTreeDepth(bookcaseTocDAO.getTreeDepth());
        setRevised(bookcaseTocDAO.isRevised());
        setApprovedForPublish(bookcaseTocDAO.isApprovedForPublish());
        setPublicationTypeCode(bookcaseTocDAO.getPublicationTypeCode());
        setPendingApproval(bookcaseTocDAO.isPendingApproval());

        //not including children, only shallow copy here
    }

    public BookcaseTocDAO(String id, String title, String tocTitle) {
        setId(id);
        setTitle(title);
        setTocTitle(tocTitle);
    }

    public BookcaseTocDAO(BookVersionEntity bookVersionEntity) {
        this.id = bookVersionEntity.getBook().getId().toString();
        this.key = bookVersionEntity.getBook().getBookKey();
        this.title = bookVersionEntity.getTitle();
        this.tocTitle = bookVersionEntity.getTitle();
        this.revisionDate = format.format(bookVersionEntity.getRevisionDate());
        this.type = bookVersionEntity.getBook().getBookType();
        this.fileExtension = "";
        this.order = bookVersionEntity.getBookOrder();
        this.isRoot = true;
    }

    //TODO to be used when toc uses jpa
    public BookcaseTocDAO(BookSectionVersionEntity bookSectionVersionEntity, BookSectionEntity bookSectionEntity, BookcaseTocDAO bookDAO) {
        if (bookSectionEntity.getParentSection() != null) {
            BookSectionEntity parentEntity = bookSectionEntity.getParentSection();

            Optional<BookSectionVersionEntity> parentOpt = parentEntity.getVersions().stream()
                    .filter(e -> e.getBookcaseVersion().equalsIgnoreCase(bookSectionVersionEntity.getBookcaseVersion()))
                    .findFirst();

            if (parentOpt.isPresent()) {
                BookcaseTocDAO parent = new BookcaseTocDAO(parentOpt.get(), parentEntity, bookDAO);
                parent.addChild(this);
                this.setParent(parent);
            }
        }
        // no parent section, set the parent as the book DAO
        else {
            this.setParent(bookDAO);
            bookDAO.addChild(this);
        }

        this.id = bookSectionEntity.getId().toString();
        this.key = bookSectionEntity.getSectionKey();
        this.title = bookSectionEntity.getTitle();
        this.tocTitle = bookSectionEntity.getTitle();
        this.fileExtension = "";
        this.order = bookSectionVersionEntity.getBookSectionOrder();
    }

    //TODO to be used when toc uses jpa
    public BookcaseTocDAO(PageblkVersionEntity pageblkEntity, Date bookRevisionDate) {
        this.id = pageblkEntity.getPageBlk().getId().toString();
        this.key = pageblkEntity.getPageBlk().getPageblkKey();
        this.title = pageblkEntity.getPageBlk().getTitle();
        this.tocTitle = pageblkEntity.getPageBlk().getTocTitle();
        this.revisionDate = format.format(pageblkEntity.getRevisionDate());
        this.type = pageblkEntity.getPageBlk().getPublicationType().getCode();
        this.fileName = pageblkEntity.getOnlineFilename();
        this.fileExtension = DataUtil.getFileType(pageblkEntity.getOnlineFilename());
        this.order = pageblkEntity.getPageblkOrder();

        String pubType = pageblkEntity.getPageBlk().getPublicationType().getCode();
        JSONObject json = new JSONObject(pageblkEntity.getPageBlk().getMetadata());

        if ("sb".equalsIgnoreCase(pubType) && ("alert".equalsIgnoreCase(json.getString("type")) ||
                "alert-cover".equalsIgnoreCase(json.getString("type")))) {
            this.type = "sbalert";
        }

        //is a revision if IC or TR and revision date after the books revision date
        if (("ic".equalsIgnoreCase(pubType) || "tr".equalsIgnoreCase(pubType)) &&
                pageblkEntity.getRevisionDate().after(bookRevisionDate)) {
            this.isRevised = true;
        }

        this.isLeaf = true;
    }

    @XmlTransient
    @JsonIgnore
    public int getTreeDepth() {
        return treeDepth;
    }

    public void setTreeDepth(int treeDepth) {
        this.treeDepth = treeDepth;
    }

    @XmlTransient
    @JsonIgnore
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlTransient
    @JsonIgnore
    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @XmlTransient
    @JsonIgnore
    public BookcaseTocDAO getParent() {
        return this.parentNode;
    }

    public void setParent(BookcaseTocDAO bookcaseTOCDAO) {
        this.parentNode = bookcaseTOCDAO;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlTransient
    @JsonIgnore
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @XmlTransient
    @JsonIgnore
    public boolean getIsLeaf() {
        return this.isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    @XmlTransient
    @JsonIgnore
    public boolean getIsRoot() {
        return this.isRoot;
    }

    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @XmlTransient
    @JsonIgnore
    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getRevisionDate() {
        return this.revisionDate == null ? "" : this.revisionDate;
    }

    public void setRevisionDate(String revdate) {
        this.revisionDate = revdate;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTocTitle() {
        return this.tocTitle;
    }

    public void setTocTitle(String tocTitle) {
        this.tocTitle = tocTitle;
    }

    public String getResourceUri() {
        return this.resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public List<BookcaseTocDAO> getChildren() {
        return this.children;
    }

    public void setChildren(List<BookcaseTocDAO> children) {
        this.children = children;
    }

    public void addChild(BookcaseTocDAO child) {
        children.add(child);
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean isApprovedForPublish() {
        return isApprovedForPublish;
    }

    public String getPublicationTypeCode() {
        return publicationTypeCode;
    }

    public void setPublicationTypeCode(String publicationTypeCode) {
        this.publicationTypeCode = publicationTypeCode;
    }

    public void setApprovedForPublish(boolean approvedForPublish) {
        isApprovedForPublish = approvedForPublish;
    }

    @XmlTransient
    @JsonIgnore
    //recursively find and return the root node
    public BookcaseTocDAO getRoot() {
        return this.getIsRoot() || parentNode == null ? this : parentNode.getRoot();
    }

    //recursively sort children
    public void sortChildren() {
        if (!children.isEmpty()) {
            for (BookcaseTocDAO child : children)
                child.sortChildren();

            children.sort(new BookcaseTocDaoNodeOrderComparator());
        }
    }

    @XmlTransient
    @JsonIgnore
    //return the leaves of this node
    public List<BookcaseTocDAO> getLeaves() {
        List<BookcaseTocDAO> leaves = new ArrayList<>();
        for (BookcaseTocDAO child : this.children) {
            if (child.getIsLeaf())
                leaves.add(child);
            else
                leaves.addAll(child.getLeaves());
        }

        return leaves;
    }

    public boolean isRevised() {
        return isRevised;
    }

    public void setRevised(boolean revised) {
        isRevised = revised;
    }

    public boolean isPendingApproval() {
        return isPendingApproval;
    }

    public void setPendingApproval(boolean isPendingApproval) {
        this.isPendingApproval = isPendingApproval;
    }
}
