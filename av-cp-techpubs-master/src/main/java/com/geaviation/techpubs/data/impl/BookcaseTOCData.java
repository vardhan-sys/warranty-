package com.geaviation.techpubs.data.impl;

import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.techlib.IBookData;
import com.geaviation.techpubs.data.api.techlib.IBookVersionData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.IPageBlkData;
import com.geaviation.techpubs.data.util.*;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookcaseTocDAO;
import com.geaviation.techpubs.models.PageblkDetailsDAO;
import com.geaviation.techpubs.models.TocDocModel;
import com.geaviation.techpubs.models.TocModel;
import com.geaviation.techpubs.models.techlib.BookEntity;
import com.geaviation.techpubs.models.techlib.BookVersionEntity;
import com.geaviation.techpubs.models.techlib.BookcaseEntity;
import com.geaviation.techpubs.models.techlib.BookcaseVersionEntity;
import com.geaviation.techpubs.services.util.StringUtils;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import static com.geaviation.techpubs.data.util.DataConstants.*;

@Component
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@RefreshScope
public class BookcaseTOCData {

    @Value("${techpubs.services.downloadOverlayReviewer}")
    private boolean downloadOverlayFeatureFlag;

    @Autowired
    private IProgramData programDataSvc;

    @Autowired
    private IBookData iBookData;

    @Autowired
    private IBookcaseData iBookCaseData;

    @Autowired
    private IBookVersionData iBookVersionData;

    @Autowired
    private IBookcaseVersionData iBookcaseVersionData;

    @Autowired
    private IPageBlkData iPageBlkData;

    @Autowired
    private SearchLoaderUtil searchLoaderUtil;

    @Autowired
    @Qualifier("techLibJDBCTemplate")
    private NamedParameterJdbcTemplate techLibJDBCTemplate;

    private static final Logger LOGGER = LogManager.getLogger(BookcaseTOCData.class);
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    private Ehcache archivedSBCache;

    private Ehcache cesmTPSCache;

    CacheManager cm = CacheManager
            .newInstance(this.getClass().getResource("/ehcacheTechpubs.xml"));

    private static final String ARCHIVESBDATACACHENAME = "TechpubsArchiveSBData";

    private static final String CESMDATACACHENAME = "TechpubsCESMData";

    @Cacheable("TechpubsBookcaseCache")
    public List<BookcaseTocDAO> getBookData(String bookcaseKey, String bookKey, String version) {
        List<BookcaseTocDAO> bookcaseTocDAOList = null;
        try {
            //get online version if no version passed in
            String tocVersion = StringUtils.isEmpty(version) ? iBookcaseVersionData.findOnlineBookcaseVersion(bookcaseKey) : version;

            if (StringUtils.isEmpty(tocVersion)) {
                LOGGER.error("No online or passed in version for " + bookcaseKey);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
            }

            //retrieve a list of TOC items from TechLib DB
            List<Map<String, Object>> bookCaseTOCItemList = getTOCItemListFromTechLibDB(bookcaseKey, tocVersion);

            //Convert the TOC items into a list of tree structures with each node in the list representing a "book"
            bookcaseTocDAOList = createBookcaseTOCModelList(bookCaseTOCItemList, bookcaseKey, bookKey, tocVersion);

            //Recursively sort the items within every level of the tree
            sortBookcaseTOCModelList(bookcaseTocDAOList);
            addDataFromTPSDB(bookcaseKey, bookcaseTocDAOList, tocVersion);

        } catch (Exception e) {
            LOGGER.error("Exception thrown from getBookcaseTOC() for bookcase " + bookcaseKey, e);
            throw new TechnicalException("Exception thrown from getBookcaseTOC for bookcase " + bookcaseKey, e);
        }

        if (!StringUtils.isEmpty(bookKey)) {
            bookcaseTocDAOList = filterByBook(bookKey, bookcaseTocDAOList);
        }

        return bookcaseTocDAOList;
    }

    @Cacheable("TechpubsBookcaseCache")
    public List<BookcaseTocDAO> getBooks(String bookcaseKey, String version) throws TechpubsException {
        List<BookcaseTocDAO> bookcaseTocDAOList = new ArrayList<>();

        //get online version if no version passed in
        String tocVersion = StringUtils.isEmpty(version) ? iBookcaseVersionData.findOnlineBookcaseVersion(bookcaseKey) : version;

        if (StringUtils.isEmpty(tocVersion)) {
            LOGGER.error("No online or passed in version for " + bookcaseKey);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        BookcaseEntity bookcase = iBookCaseData.findByBookcaseKeyAndBookcaseVersion(bookcaseKey, tocVersion);
        List<BookEntity> books = iBookData.findByBookcaseAndBookcaseVersion(bookcase, tocVersion);

        List<BookVersionEntity> bookVersions = new ArrayList<>();

        for (BookEntity book : books) {
            bookVersions.addAll(iBookVersionData.findByBookAndBookcaseVersion(book, tocVersion));
        }

        for (BookVersionEntity bookVersion : bookVersions) {
            BookcaseTocDAO bookDAO = new BookcaseTocDAO(bookVersion);

            //if ic or tr check bookcase and version has ic / tr pageblks, otherwise check book id and version for pageblks
            Boolean hasChildren = (TR_PAGEBLK_TYPE.equalsIgnoreCase(bookVersion.getBook().getBookType()) ||
                    IC_PAGEBLK_TYPE.equalsIgnoreCase(bookVersion.getBook().getBookType())) ?
                    iPageBlkData.hasPageblksForBookcaseIdAndVersionAndPubType(bookcase.getId(), tocVersion,
                            bookVersion.getBook().getBookType()) :
                    iPageBlkData.hasPageblksForBookIdAndVersion(bookVersion.getBook().getId(), tocVersion);

            bookDAO.setHasChildren(hasChildren);

            bookcaseTocDAOList.add(bookDAO);
        }

        sortBookcaseTOCModelList(bookcaseTocDAOList);

        if(!CollectionUtils.isEmpty(bookcaseTocDAOList))
            bookcaseTocDAOList.addAll(getCESMFromTPS(bookcaseKey, tocVersion));

        return bookcaseTocDAOList;
    }

    private List<BookcaseTocDAO> filterByBook(String bookKey, List<BookcaseTocDAO> bookcaseTocDAOList) {
        //filter the response by the passed in book
        return bookcaseTocDAOList.stream()
                .filter(e -> e.getKey().equals(bookKey))
                .collect(Collectors.toList());
    }

    private List<BookcaseTocDAO> addDataFromTPSDB(String bookcaseKey, List<BookcaseTocDAO> bookcaseTocDAOList, String tocVersion){
        //Add the books which are only stored in TPS (i.e. CESMs)
        if(bookcaseTocDAOList != null && !bookcaseTocDAOList.isEmpty()){
            bookcaseTocDAOList.addAll(getCESMFromTPS(bookcaseKey, tocVersion));
        }

        addArchivedSBsFromTPS(bookcaseKey, bookcaseTocDAOList, tocVersion);
        return bookcaseTocDAOList;
    }

    private List<BookcaseTocDAO> addArchivedSBsFromTPS(String bookcaseKey,List<BookcaseTocDAO> bookcaseTocDAOList, String tocVersion){
        BookcaseTocDAO sbBook = getBookByType(SB, bookcaseTocDAOList);
        if(sbBook != null){
            List<Map<String, String>> archivedSbsFromTPS = getArchivedSBsFromTPS(bookcaseKey,  sbBook.getLeaves());

            if(!archivedSbsFromTPS.isEmpty()){
                BookcaseTocDAO archivedSBSection = new BookcaseTocDAO();
                archivedSBSection.setTitle(ARCHIVED_SB_BOOK_TITLE);
                archivedSBSection.setTocTitle(ARCHIVED_SB_BOOK_TITLE);
                archivedSBSection.setApprovedForPublish(true);
                sbBook.addChild(archivedSBSection);

                for(Map<String, String> archivedSbFromTPS: archivedSbsFromTPS){
                    archivedSBSection.addChild(createArchivedSBBookcaseTOCDAO(archivedSbFromTPS, bookcaseKey, tocVersion));
                }
            }

        }

        return bookcaseTocDAOList;
    }

    private BookcaseTocDAO createArchivedSBBookcaseTOCDAO(Map<String, String> archivedSbFromTPS, String bookcaseKey, String tocVersion){
        BookcaseTocDAO archivedSbPageblk = new BookcaseTocDAO();
        archivedSbPageblk.setTocTitle(archivedSbFromTPS.get(TPS_TITLE));
        archivedSbPageblk.setTitle(archivedSbFromTPS.get(TPS_TITLE));
        archivedSbPageblk.setKey(archivedSbFromTPS.get(TPS_TOC_KEY));
        archivedSbPageblk.setRevisionDate(archivedSbFromTPS.get(TPS_REV_DATE));
        archivedSbPageblk.setType(DataUtil.isSBAlert(archivedSbFromTPS.get(TPS_TYPE)) ? SBALERT_PAGEBLK_TYPE : SB);
        archivedSbPageblk.setFileName(archivedSbFromTPS.get(TPS_VIEW_FILENAME));
        archivedSbPageblk.setApprovedForPublish(true);
        archivedSbPageblk.setFileExtension(DataUtil.getFileType(archivedSbFromTPS.get(TPS_VIEW_FILENAME)));
        archivedSbPageblk.setResourceUri(DataUtil.createFileResourceUri(bookcaseKey, SB_BOOK_KEY, archivedSbFromTPS.get(TPS_VIEW_FILENAME), tocVersion));

        return archivedSbPageblk;
    }

    private List<Map<String, String>> getArchivedSBsFromTPS(String bookcaseKey, List<BookcaseTocDAO> sbsFromTechLibDB){
        Map<String, Map<String, String>> sbsFromTpsDBMap = getTPSArichiveSB(bookcaseKey);
        List<Map<String, String>> archivedSBsList = new LinkedList<>();
        if(sbsFromTpsDBMap != null){
            if(sbsFromTechLibDB.isEmpty()){
                archivedSBsList.addAll(sbsFromTpsDBMap.values());
            } else {
                for (Map.Entry<String, Map<String, String>> sBFromTPS : sbsFromTpsDBMap.entrySet()) {
                    boolean sbExistsInTechLib = false;

                    for (BookcaseTocDAO sbFromTechLibDB : sbsFromTechLibDB) {
                        if (sbFromTechLibDB.getFileName().equals(sBFromTPS.getKey())) {
                            sbExistsInTechLib = true;
                            break;
                        }
                    }

                    if (!sbExistsInTechLib)
                        archivedSBsList.add(sBFromTPS.getValue());
                }
            }
        }

        return archivedSBsList;
    }

    public Map<String, String> getTPSArichiveSB(String bookcaseKey, String filename){
        Map<String, Map<String, String>> archiveSBs = getTPSArichiveSB(bookcaseKey);
        if(archiveSBs != null) {
            return archiveSBs.get(filename);
        } else {
            return null;
        }
    }

    private Map<String, Map<String, String>> getTPSArichiveSB(String bookcaseKey) {
        Map<String, Map<String, String>> sbsFromTpsDBMap = null;
        if (archivedSBCache == null){
            this.archivedSBCache = cm.getCache(ARCHIVESBDATACACHENAME);
        }
        if (archivedSBCache != null) {
            net.sf.ehcache.Element element = archivedSBCache.get(bookcaseKey);
            if (element != null) {
                sbsFromTpsDBMap = (Map<String, Map<String, String>>) element.getObjectValue();
            } else {
                sbsFromTpsDBMap = programDataSvc.getCatalogItemFilename(bookcaseKey, SB_BOOK_KEY, SB);

                archivedSBCache.put(new net.sf.ehcache.Element(bookcaseKey, sbsFromTpsDBMap));
            }
        }
        return sbsFromTpsDBMap;
    }

    private BookcaseTocDAO getBookByType(String type, List<BookcaseTocDAO> books){
        BookcaseTocDAO bookWithType = null;
        for(BookcaseTocDAO book: books){
            if(book.getType() != null && book.getType().equals(type))
                bookWithType = book;
        }

        return bookWithType;
    }

    //Query TechLib DB for a flat list of TOC items (retrieved from book, section, and pageblk tables)
    @Cacheable("TechpubsBookcaseCache")
    private  List<Map<String, Object>> getTOCItemListFromTechLibDB(String bookcaseName, String bookcaseVersion){
        SqlParameterSource functionParameters = new MapSqlParameterSource()
                .addValue(FUNCTION_PARAM_ORGID, "")
                .addValue(FUNCTION_PARAM_BOOKCASEKEY, bookcaseName)
                .addValue(FUNCTION_PARAM_BOOKCASEVERSION, bookcaseVersion);

        return techLibJDBCTemplate.queryForList(BOOKCASETOCFUNCTION, functionParameters);
    }

    private List<BookcaseTocDAO> createBookcaseTOCModelList(List<Map<String, Object>> bookCaseTOCItemList, String bookcaseKey, String bookKey, String tocVersion){
        Map<String, BookcaseTocDAO> bookcaseTOCModelMap = new HashedMap();
        List<String> icIdList = new ArrayList<>();
        List<String> trIdList = new ArrayList<>();

        List<PageblkDetailsDAO> pageblkKeyAndRevisionList = new ArrayList<>();

        if (downloadOverlayFeatureFlag) {
            //Return all approved SB pageblk keys and revisions for any of the versions
            pageblkKeyAndRevisionList = iPageBlkData.findPageBlksKeysAndRevisionForSbType(bookcaseKey);
        }

        if (bookCaseTOCItemList != null) {
            for (Map<String, Object> bookCaseTOCItem : bookCaseTOCItemList) {
                BookcaseTocDAO bookcaseTOCDAO = createBookcaseTOCModel(bookCaseTOCItem,pageblkKeyAndRevisionList);
                bookcaseTOCModelMap.put(bookcaseTOCDAO.getId(), bookcaseTOCDAO);

                String nodeType = bookcaseTOCDAO.getType();

                //if item is a ic pageblk, then add id to the iclist
                if (DataConstants.IC_PAGEBLK_TYPE.equalsIgnoreCase(nodeType) && !bookcaseTOCDAO.getIsRoot())
                    icIdList.add(bookcaseTOCDAO.getId());

                    //if item is a tr pageblk, then add tr to the trlist
                else if (DataConstants.TR_PAGEBLK_TYPE.equalsIgnoreCase(nodeType) && !bookcaseTOCDAO.getIsRoot())
                    trIdList.add(bookcaseTOCDAO.getId());
            }
        }

        //Phase 1 from Techlib previous version release date
        //Phase 2 from toc.xml for book to Techlib
        Date previousBookRevisedDate = new Date(Long.MIN_VALUE);

        List<BookcaseVersionEntity> bookcaseVersionEntitys = iBookcaseVersionData.findPreviousBookcaseVersion(bookcaseKey, tocVersion);

        if(!bookcaseVersionEntitys.isEmpty()){
            previousBookRevisedDate = bookcaseVersionEntitys.get(0).getVersionTimestamp();
        }

        //convert the TOC items into a tree structure list
        List<BookcaseTocDAO> bookcaseTocDAOList = createTreeStructureList(bookcaseTOCModelMap, previousBookRevisedDate);

        //set the resource URI of pageblks
        if (LR_BOOK_TYPE.equalsIgnoreCase(bookKey)) {
            setResourceURIOfLRPageblks(bookcaseKey, bookcaseTocDAOList, tocVersion);
        }
        else {
            setResourceURIOfPageblks(bookcaseKey, bookcaseTocDAOList, tocVersion);
        }

        //if there are TRs in the bookcase, then create a "book" of all the trs and add it to the bookcaseTocDAOList
        if (!trIdList.isEmpty()) {
            createBookByPageblkType(trIdList, bookcaseTOCModelMap, bookcaseTocDAOList, DataConstants.TR_PAGEBLK_TYPE);
        }

        //if there are ICs in the bookcase, then create a "book" of all the ics and add it to the bookcaseTocDAOList
        if (!icIdList.isEmpty()) {
            createBookByPageblkType(icIdList, bookcaseTOCModelMap, bookcaseTocDAOList, DataConstants.IC_PAGEBLK_TYPE);
        }

        return bookcaseTocDAOList;
    }

    //create a list of books containing corresponding TOC items
    private  List<BookcaseTocDAO> createTreeStructureList(Map<String, BookcaseTocDAO> bookcaseTOCModelMap, Date previousBookRevisedDate) {
        List<BookcaseTocDAO> bookcaseTocDAOList = new ArrayList<>();

        //add children to parent TOC items and parents to the children
        for (Entry<String, BookcaseTocDAO> bookcaseTocDAO : bookcaseTOCModelMap.entrySet()){
            BookcaseTocDAO bookcaseTOCDAO = bookcaseTocDAO.getValue();

            if (bookcaseTOCDAO.getTreeDepth() > 0) { //this item is not a dummy section (it is part of the TOC)
                String parentid = bookcaseTOCDAO.getParentId();

                //item is a "book"
                if (bookcaseTOCDAO.getIsRoot()){
                    bookcaseTocDAOList.add(bookcaseTOCDAO);
                } else { //item has a parent (it is a section or pageblk)
                    BookcaseTocDAO parent = bookcaseTOCModelMap.get(parentid);
                    createParentChildRelationshipBetweenTOCItems(parent, bookcaseTOCDAO, bookcaseTOCModelMap);

                    //If child date after book date set revision true
                    Date childRevisionDate = new Date(Long.MIN_VALUE);
                    try {
                        childRevisionDate = format.parse(bookcaseTOCDAO.getRevisionDate());
                    } catch (ParseException e) {
                        LOGGER.debug("Error parsing date from database: " + e.getMessage());
                    }
                    bookcaseTOCDAO.setRevised(!childRevisionDate.before(previousBookRevisedDate));
                }
            }

        }
        return bookcaseTocDAOList;
    }

    private void createParentChildRelationshipBetweenTOCItems(
            BookcaseTocDAO parent, BookcaseTocDAO child, Map<String, BookcaseTocDAO> bookcaseTOCModelMap){
        //if the parent is a dummy section (sole purpose is to create join between book and pageblk in DB) then set the dummy section's parent as parent
        BookcaseTocDAO tocParent = parent.getTreeDepth() > 0 ? parent : bookcaseTOCModelMap.get(parent.getParentId());
        tocParent.addChild(child);
        child.setParent(tocParent);
    }

    //Constructs a "book" of the specified type (IC or TR) with all the items of the specified type in the bookcase (across all books)
    private BookcaseTocDAO createBookByPageblkType(List<String> idList, Map<String, BookcaseTocDAO> bookcaseTOCModelMap, List<BookcaseTocDAO> books, String type) {

        BookcaseTocDAO rootBookcaseTocDAO = getBookByType(books, type);
        if(rootBookcaseTocDAO != null) {
            Map<String, BookcaseTocDAO> bookLevelbookcaseTOCModelMap = new HashedMap();

            //for each id create a 3-leveled tree (book, section, and pageblk)
            for (String id : idList) {
                BookcaseTocDAO pageblkBookcaseTocDAO = new BookcaseTocDAO(bookcaseTOCModelMap.get(id));

                //create a new root node (book level node)
                BookcaseTocDAO bookLevelBookcaseTocDAO = pageblkBookcaseTocDAO.getRoot();

                BookcaseTocDAO newBookLevelBookcaseTocDAO = bookLevelbookcaseTOCModelMap.get(
                        bookLevelBookcaseTocDAO.getType());

                if (newBookLevelBookcaseTocDAO == null) { //new root node was not created in a previous iteration
                    //do not create lr book
                    if (LR_TYPE.equalsIgnoreCase(bookLevelBookcaseTocDAO.getType()))
                        continue;

                    String title = bookLevelBookcaseTocDAO.getType() == null ? bookLevelBookcaseTocDAO
                            .getType() : bookLevelBookcaseTocDAO
                            .getType().toUpperCase();

                    newBookLevelBookcaseTocDAO =
                            new BookcaseTocDAO(bookLevelBookcaseTocDAO.getId(), title, title);

                    newBookLevelBookcaseTocDAO.setParent(rootBookcaseTocDAO);
                    newBookLevelBookcaseTocDAO.setOrder(bookLevelBookcaseTocDAO.getOrder());
                    newBookLevelBookcaseTocDAO.setType(bookLevelBookcaseTocDAO.getType());
                }

                newBookLevelBookcaseTocDAO.setRevised(bookLevelBookcaseTocDAO.isRevised());
                newBookLevelBookcaseTocDAO.setApprovedForPublish(bookLevelBookcaseTocDAO.isApprovedForPublishICTR());
                newBookLevelBookcaseTocDAO.setPendingApproval(bookLevelBookcaseTocDAO.isPendingApproval());

                //create a new parent node (direct parent section)
                BookcaseTocDAO parentBookcaseTocDAO = pageblkBookcaseTocDAO.getParent();
                BookcaseTocDAO newparentBookcaseTocDAO = getBookByTitle(newBookLevelBookcaseTocDAO.getChildren(), parentBookcaseTocDAO.getKey());

                if (newparentBookcaseTocDAO == null) { //new parent node was not created in a previous iteration
                    String parentSectionTitle = parentBookcaseTocDAO.getIsRoot() ? PLACEHOLDER_SECTION_TITLE : parentBookcaseTocDAO.getKey();
                    newparentBookcaseTocDAO = new BookcaseTocDAO(parentBookcaseTocDAO.getId(), parentSectionTitle, parentSectionTitle);

                    newparentBookcaseTocDAO.setOrder(parentBookcaseTocDAO.getOrder());

                    //add the parent-child relationships (between book and section)
                    newBookLevelBookcaseTocDAO.addChild(newparentBookcaseTocDAO);
                    newparentBookcaseTocDAO.setParent(newBookLevelBookcaseTocDAO);
                }

                newparentBookcaseTocDAO.setRevised(parentBookcaseTocDAO.isRevised());
                newparentBookcaseTocDAO.setApprovedForPublish(parentBookcaseTocDAO.isApprovedForPublishICTR());
                newparentBookcaseTocDAO.setPendingApproval(parentBookcaseTocDAO.isPendingApproval());

                //change the pageblk's parent to new section
                pageblkBookcaseTocDAO.setParent(newparentBookcaseTocDAO);

                //add the pageblk to the tree
                newparentBookcaseTocDAO.addChild(pageblkBookcaseTocDAO);

                bookLevelbookcaseTOCModelMap.put(newBookLevelBookcaseTocDAO.getType(), newBookLevelBookcaseTocDAO);
            }

            rootBookcaseTocDAO.setChildren(new ArrayList<>(bookLevelbookcaseTOCModelMap.values()));
            if (!rootBookcaseTocDAO.getChildren().isEmpty()) rootBookcaseTocDAO.setHasChildren(true);

            for (BookcaseTocDAO child : rootBookcaseTocDAO.getChildren()) {
                if (child.isRevised()) {
                    rootBookcaseTocDAO.setRevised(child.isRevised());
                    break;
                }
            }

            //Set the order field of each section (direct parent section of pageblks) so that
            //when pageblks from multiple books are in the same book section, the sections are still sorted by title
            //This happens when there are multiple books of the same type in the TOC
            //Sort all children down to leaves if the book is an ic or tr type
            sortAllChildrenByTitle(rootBookcaseTocDAO.getChildren());
        }

        return rootBookcaseTocDAO;
    }

    private void sortAllChildrenByTitle(List<BookcaseTocDAO> children){
        for (BookcaseTocDAO child : children) {
            if (!child.getChildren().isEmpty()) {
                sortAllChildrenByTitle(child.getChildren());
            }
        }

        children.sort(new BookcaseTocTitleComparator());
        int order = 1;
        for (BookcaseTocDAO child :  children) {
            child.setOrder(order++);
        }
    }

    private BookcaseTocDAO getBookByType(List<BookcaseTocDAO> books, String type){
        BookcaseTocDAO bookWithSpecifiedType = null;
        for (BookcaseTocDAO book: books){
            if(book.getType() != null && book.getType().equals(type))
                bookWithSpecifiedType = book;
        }
        return bookWithSpecifiedType;
    }

    private BookcaseTocDAO getBookByTitle(List<BookcaseTocDAO> books, String title){
        BookcaseTocDAO bookWithTitle = null;
        for (BookcaseTocDAO book: books){
            if(book.getTitle() != null && book.getTitle().equals(title))
                bookWithTitle = book;
        }
        return bookWithTitle;
    }

    //recursively sort items within every level of the tree
    private void sortBookcaseTOCModelList (List<BookcaseTocDAO> bookcaseTocDAOList){
        for (BookcaseTocDAO book : bookcaseTocDAOList) {
            book.sortChildren();
        }

        bookcaseTocDAOList.sort(new BookcaseTocDaoNodeOrderComparator());
    }

    private BookcaseTocDAO createBookcaseTOCModel(Map<String, Object> bookCaseTOCItem, List<PageblkDetailsDAO> pageblkKeyList) {
        String id = null;
        if (bookCaseTOCItem.get(TECHLIB_ID) != null)
            id = ((UUID) bookCaseTOCItem.get(TECHLIB_ID)).toString();

        String parentId = null;
        if (bookCaseTOCItem.get(TECHLIB_PARENT_ID) != null)
            parentId = ((UUID) bookCaseTOCItem.get(TECHLIB_PARENT_ID)).toString();

        String nodeType = null;
        if (bookCaseTOCItem.get(TECHLIB_NODE_TYPE) != null)
            nodeType = (String) bookCaseTOCItem.get(TECHLIB_NODE_TYPE);

        String title = null;
        if (bookCaseTOCItem.get(TECHLIB_TITLE) != null)
            title = (String) bookCaseTOCItem.get(TECHLIB_TITLE);

        String tocTitle = null;
        if (bookCaseTOCItem.get(TECHLIB_TOC_TITLE) != null)
            tocTitle = (String) bookCaseTOCItem.get(TECHLIB_TOC_TITLE);

        String revDate = null;
        if (bookCaseTOCItem.get(TECHLIB_REVISION_DATE) != null)
            revDate = (String) bookCaseTOCItem.get(TECHLIB_REVISION_DATE);

        String nodeKey = null;
        if (bookCaseTOCItem.get(TECHLIB_NODE_KEY) != null)
            nodeKey = (String) bookCaseTOCItem.get(TECHLIB_NODE_KEY);

        String filename = null;
        if (bookCaseTOCItem.get(TECHLIB_FILENAME) != null)
            filename = (String) bookCaseTOCItem.get(TECHLIB_FILENAME);

        int order = 0;
        if (bookCaseTOCItem.get(TECHLIB_NODE_ORDER) != null)
            order = ((Integer) bookCaseTOCItem.get(TECHLIB_NODE_ORDER)).intValue();

        int treeDepth = 0;
        if (bookCaseTOCItem.get(TECHLIB_TREE_DEPTH) != null)
            treeDepth = ((Integer) bookCaseTOCItem.get(TECHLIB_TREE_DEPTH)).intValue();

        String publicationTypeCode = null;
        if (bookCaseTOCItem.get(TECHLIB_PUBLICATION_TYPE_CODE) != null)
            publicationTypeCode = (String) bookCaseTOCItem.get(TECHLIB_PUBLICATION_TYPE_CODE);

        String revision = null;
        if (bookCaseTOCItem.get(TECHLIB_REVISION) != null)
            revision = (String) bookCaseTOCItem.get(TECHLIB_REVISION);

        boolean isApprovedForPublish = false;
        if (downloadOverlayFeatureFlag) {
            if (SB_PAGEBLK_TYPE.equals(bookCaseTOCItem.get(TECHLIB_PUBLICATION_TYPE_CODE))) {
                if (CollectionUtils.isNotEmpty(pageblkKeyList)) {
                    for(PageblkDetailsDAO pageblkDetailsDAO : pageblkKeyList){

                        if(!isApprovedForPublish) {
                            if (pageblkDetailsDAO.getKey().equals(nodeKey)
                                    && pageblkDetailsDAO.getRevision().equals(revision)) {
                                isApprovedForPublish = true;
                            } else {
                                isApprovedForPublish = (boolean) bookCaseTOCItem.get(TECHLIB_APPROVE_PUBLISH_FLAG);
                            }
                        }
                    }
                }
            } else if (MANUAL_PAGEBLK_TYPE.equals(bookCaseTOCItem.get(TECHLIB_PUBLICATION_TYPE_CODE))) {
                if (bookCaseTOCItem.get(TECHLIB_APPROVE_PUBLISH_FLAG) != null)
                    isApprovedForPublish = true;
            } else {
                if (bookCaseTOCItem.get(TECHLIB_APPROVE_PUBLISH_FLAG) != null)
                    isApprovedForPublish = (boolean) bookCaseTOCItem.get(TECHLIB_APPROVE_PUBLISH_FLAG);
            }
        } else {
            isApprovedForPublish = true;
        }

        BookcaseTocDAO bookcaseTOCDAO = new BookcaseTocDAO();
        bookcaseTOCDAO.setId(id);
        bookcaseTOCDAO.setParentId(parentId);
        bookcaseTOCDAO.setIsRoot(parentId == null);
        bookcaseTOCDAO.setTitle(!TechpubsAppUtil.isNullOrEmpty(title) ? title : tocTitle);
        bookcaseTOCDAO.setTocTitle(!TechpubsAppUtil.isNullOrEmpty(tocTitle) ? tocTitle : title);
        bookcaseTOCDAO.setType(nodeType);
        bookcaseTOCDAO.setFileExtension(DataUtil.getFileType(filename));
        bookcaseTOCDAO.setIsLeaf(DataUtil.isPageBlkType(nodeType));
        bookcaseTOCDAO.setRevisionDate(revDate);
        bookcaseTOCDAO.setOrder(order);
        bookcaseTOCDAO.setKey(nodeKey);
        bookcaseTOCDAO.setFileName(filename);
        bookcaseTOCDAO.setTreeDepth(treeDepth);
        bookcaseTOCDAO.setApprovedForPublish(StringUtils.isEmpty(nodeType) ? true : isApprovedForPublish);
        bookcaseTOCDAO.setPublicationTypeCode(publicationTypeCode);

        return bookcaseTOCDAO;
    }

    //Construct a resource URI and assign it to each leaf node of the items in the list
    private void setResourceURIOfPageblks(String bookcaseKey, List<BookcaseTocDAO> listOfBooks, String tocVersion){
        for (BookcaseTocDAO book : listOfBooks){
            for(BookcaseTocDAO pageblk : book.getLeaves()){
                pageblk.setResourceUri(
                        DataUtil.createFileResourceUri(bookcaseKey, pageblk.getRoot().getKey(), pageblk.getFileName(), tocVersion));
                if(pageblk.isRevised()) {
                    setParentAsRevised(pageblk.getParent());
                }
                if (pageblk.isApprovedForPublish() && !pageblk.getParent()
                    .isApprovedForPublish()) {
                    setParentApprovedPublishFlag(pageblk.getParent());
                }

                if (IC_PAGEBLK_TYPE.equalsIgnoreCase(pageblk.getType())
                    || TR_PAGEBLK_TYPE.equalsIgnoreCase(pageblk.getType())
                    || SB_PAGEBLK_TYPE.equalsIgnoreCase(pageblk.getType())
                    || SBALERT_PAGEBLK_TYPE.equalsIgnoreCase(pageblk.getType())){

                    if (!pageblk.isApprovedForPublish()) {
                        if (!pageblk.getParent().isPendingApproval()) {
                            setParentPendingApprovalFlag(pageblk.getParent());
                        }
                        pageblk.setPendingApproval(true);
                    }
                    if (pageblk.isApprovedForPublish() && !pageblk.getParent()
                        .isApprovedForPublishICTR()) {
                        setParentApprovedPublishFlagICTR(pageblk.getParent());
                    }
                }
            }
        }
    }

    private boolean setParentAsRevised(BookcaseTocDAO bookcaseTocDAOParentNode){
        // TODO check for performance
        if(bookcaseTocDAOParentNode.getIsRoot()){
            bookcaseTocDAOParentNode.setRevised(true);
            return false;
        } else {
            boolean isParentRoot = bookcaseTocDAOParentNode.getParent().getIsRoot();
            if (isParentRoot) {
                bookcaseTocDAOParentNode.getParent().setRevised(true);
                bookcaseTocDAOParentNode.setRevised(true);
                return false;
            } else {
                bookcaseTocDAOParentNode.setRevised(true);
                if (!setParentAsRevised(bookcaseTocDAOParentNode.getParent())) {
                    return false;
                }
                return true;
            }
        }
    }

    private boolean setParentPendingApprovalFlag(BookcaseTocDAO bookcaseTocDAOParentNode) {
        if (bookcaseTocDAOParentNode.getIsRoot()) {
            bookcaseTocDAOParentNode.setPendingApproval(true);
            return false;
        } else {
            boolean isParentRoot = bookcaseTocDAOParentNode.getParent().getIsRoot();
            if (isParentRoot) {
                bookcaseTocDAOParentNode.getParent().setPendingApproval(true);
                bookcaseTocDAOParentNode.setPendingApproval(true);
                return false;
            } else {
                bookcaseTocDAOParentNode.setPendingApproval(true);
                if (!setParentPendingApprovalFlag(bookcaseTocDAOParentNode.getParent())) {
                    return false;
                }
                return true;
            }
        }
    }

    private boolean setParentApprovedPublishFlag(BookcaseTocDAO bookcaseTocDAOParentNode) {
        if (bookcaseTocDAOParentNode.getIsRoot()) {
            bookcaseTocDAOParentNode.setApprovedForPublish(true);
            return false;
        } else {
            boolean isParentRoot = bookcaseTocDAOParentNode.getParent().getIsRoot();
            if (isParentRoot) {
                bookcaseTocDAOParentNode.getParent().setApprovedForPublish(true);
                bookcaseTocDAOParentNode.setApprovedForPublish(true);
                return false;
            } else {
                bookcaseTocDAOParentNode.setApprovedForPublish(true);
                if (!setParentApprovedPublishFlag(bookcaseTocDAOParentNode.getParent())) {
                    return false;
                }
                return true;
            }
        }
    }

    private boolean setParentApprovedPublishFlagICTR(BookcaseTocDAO bookcaseTocDAOParentNode) {
        if (bookcaseTocDAOParentNode.getIsRoot()) {
            bookcaseTocDAOParentNode.setApprovedForPublishICTR(true);
            return false;
        } else {
            boolean isParentRoot = bookcaseTocDAOParentNode.getParent().getIsRoot();
            if (isParentRoot) {
                bookcaseTocDAOParentNode.getParent().setApprovedForPublishICTR(true);
                bookcaseTocDAOParentNode.setApprovedForPublishICTR(true);
                return false;
            } else {
                bookcaseTocDAOParentNode.setApprovedForPublishICTR(true);
                if (!setParentApprovedPublishFlagICTR(bookcaseTocDAOParentNode.getParent())) {
                    return false;
                }
                return true;
            }
        }
    }

    //Construct a resource URI and assign it to each leaf node of the items in the list for LR pageblks
    private void setResourceURIOfLRPageblks(String bookcaseKey, List<BookcaseTocDAO> listOfBooks, String tocVersion){
        BookcaseTocDAO book = getBookByType(listOfBooks, LR_TYPE);

        for(BookcaseTocDAO bookSection : book.getChildren()) {
            BookcaseTocDAO parentBook = getBookByType(listOfBooks, bookSection.getTitle().toLowerCase());

            for (BookcaseTocDAO pageblk : bookSection.getLeaves()) {
                pageblk.setResourceUri(
                        DataUtil.createFileResourceUri(bookcaseKey, parentBook.getKey(), pageblk.getFileName(), tocVersion));
                if(pageblk.isRevised()) {
                    setParentAsRevised(pageblk.getParent());
                }

                if (pageblk.isApprovedForPublish() && !pageblk.getParent()
                    .isApprovedForPublish()) {
                    setParentApprovedPublishFlag(pageblk.getParent());
                }

                if (IC_PAGEBLK_TYPE.equalsIgnoreCase(pageblk.getType())
                        || TR_PAGEBLK_TYPE.equalsIgnoreCase(pageblk.getType())
                        || SB_PAGEBLK_TYPE.equalsIgnoreCase(pageblk.getType())
                        || SBALERT_PAGEBLK_TYPE.equalsIgnoreCase(pageblk.getType())){

                    if (!pageblk.isApprovedForPublish()) {
                        if (!pageblk.getParent().isPendingApproval()) {
                            setParentPendingApprovalFlag(pageblk.getParent());
                        }
                        pageblk.setPendingApproval(true);
                    }
                    if (pageblk.isApprovedForPublish() && !pageblk.getParent()
                            .isApprovedForPublishICTR()) {
                        setParentApprovedPublishFlagICTR(pageblk.getParent());
                    }
                }
            }
        }
    }

    public List<BookcaseTocDAO> getCESMFromTPS(String bookcaseKey, String tocVersion){
        List<BookcaseTocDAO> cesmFromTpsDBMap = null;
        if (cesmTPSCache == null){
            this.cesmTPSCache = cm.getCache(CESMDATACACHENAME);
        }
        if (cesmTPSCache != null) {
            net.sf.ehcache.Element element = cesmTPSCache.get(bookcaseKey+tocVersion);
            if (element != null) {
                cesmFromTpsDBMap = (List<BookcaseTocDAO>) element.getObjectValue();
            } else {
                cesmFromTpsDBMap = getBooksFromTpsDB( bookcaseKey,  tocVersion);

                cesmTPSCache.put(new net.sf.ehcache.Element(bookcaseKey+tocVersion, cesmFromTpsDBMap));
            }
        }
        return cesmFromTpsDBMap;
    }

    private List<BookcaseTocDAO> getBooksFromTpsDB(String bookcaseKey, String tocVersion){
        List<BookcaseTocDAO> booksFromTPS = new ArrayList<>();
        List<TocModel> tocModelsFromTPS = programDataSvc.getTocsByProgram(bookcaseKey);

        for(TocModel tocModel : tocModelsFromTPS){
            BookcaseTocDAO bookFromTPS = new BookcaseTocDAO();
            bookFromTPS.setTitle(tocModel.getTitle());
            bookFromTPS.setTocTitle(tocModel.getTitle());
            bookFromTPS.setKey(tocModel.getManualDocnbr());
            bookFromTPS.setApprovedForPublish(true);

            for (TocDocModel tocDocModel : tocModel.getTocDocList()){
                BookcaseTocDAO pageblkFromTPS = new BookcaseTocDAO();
                pageblkFromTPS.setTocTitle(tocDocModel.getTitle());
                pageblkFromTPS.setTitle(tocDocModel.getTitle());
                pageblkFromTPS.setFileName(tocDocModel.getViewFileName());
                pageblkFromTPS.setResourceUri(DataUtil.createFileResourceUri(bookcaseKey, bookFromTPS.getKey(), tocDocModel.getViewFileName(), tocVersion));
                pageblkFromTPS.setApprovedForPublish(true);
                bookFromTPS.addChild(pageblkFromTPS);
            }

            if (!CollectionUtils.isEmpty(bookFromTPS.getChildren()))
                bookFromTPS.setHasChildren(Boolean.TRUE);

            booksFromTPS.add(bookFromTPS);
        }
        return booksFromTPS;
    }

    public void publishPageblkDocument(String bookcase, String book, String bookType , String version, String fileName,
                                       String pageblkKey, boolean emailNotification) {

        List<PageblkDetailsDAO> pageblkDetailsDAOList = iPageBlkData.findPageBlkByVersionAndFileName(bookcase, version, fileName);

        if (!CollectionUtils.isEmpty(pageblkDetailsDAOList)) {
            for(PageblkDetailsDAO pageblkDetailsDAO : pageblkDetailsDAOList){
                iPageBlkData.updateApprovedForPublishFlagToTrue(emailNotification, pageblkDetailsDAO.getPageblkId(), pageblkKey);

                //Invoke pageblk lambda to update approvedForPublishFlag to True
                LOGGER.info("Invoke av-cp-techpubs-update-search-pageblk-index Lambda for  : {}",bookcase);
                searchLoaderUtil.invokePageblkUpdateLoader(bookcase,book,bookType,version,fileName);

                LOGGER.info("Completed  av-cp-techpubs-update-search-pageblk-index Lambda call for  : {}" ,bookcase);
            }

        }
    }

}
