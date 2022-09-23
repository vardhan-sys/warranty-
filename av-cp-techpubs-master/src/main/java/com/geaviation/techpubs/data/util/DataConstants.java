package com.geaviation.techpubs.data.util;

public class DataConstants {

    public static final String DB_TECHPUBS = "techpubs";
    public static final String EMPTY_SPACE = " ";
    public static final String HYPHEN = " - ";
    public static final String QUESTION = "?";
    public static final String EQUAL = "=";

    public static final String COLLECTION_ARTIFACTS = "artifacts";
    public static final String ARTIFACTS_ID = "_id";
    public static final String ARTIFACTS_METADATA_TYPE = "type";

    public static final String COLLECTION_DOCUMENTS = "documents";
    public static final String DOCUMENTS_ID = "_id";
    public static final String DOCUMENTS_ID_VAL = "$_id";
    public static final String DOCUMENTS_SUBSYSTEM = "subsystem";
    public static final String DOCUMENTS_MODELS = "models";
    public static final String DOCUMENTS_MODELS_VAL = "$models";
    public static final String DOCUMENTS_TITLE = "title";
    public static final String DOCUMENTS_TITLE_VAL = "$title";
    public static final String DOCUMENTS_FILEID = "fileid";
    public static final String DOCUMENTS_FILEID_VAL = "$fileid";
    public static final String DOCUMENTS_FAMILY = "family";
    public static final String DOCUMENTS_DOCTYPE = "doctype";
    public static final String DOCUMENTS_DOCTYPE_VAL = "$doctype";
    public static final String DOCUMENTS_DOCTYPE_ID = "doctypeid";
    public static final String DOCUMENTS_DOCNBR = "docnbr";
    public static final String DOCUMENTS_DOCNBR_VAL = "$docnbr";
    public static final String DOCUMENTS_CMMPUBNBR = "cmmpubnbr";
    public static final String DOCUMENTS_TRREASON = "trreason";
    public static final String DOCUMENTS_GROUPNAME = "groupname";
    public static final String DOCUMENTS_RELEASEDATE = "releasedate";
    public static final String DOCUMENTS_CREATIONDATE = "creationdate";
    public static final String DOCUMENTS_CREATIONDATE_VAL = "$creationdate";
    public static final String DOCUMENTS_REVDATE = "revdate";
    public static final String DOCUMENTS_REVNBR = "revnbr";
    public static final String DOCUMENTS_REVDATE_VAL = "$revdate";
    public static final String DOCUMENTS_ACLS = "acls";
    public static final String DOCUMENTS_DELETED = "deleted";
    public static final String DOCUMENTS_PARENTID = "parentid";
    public static final String DOCUMENTS_PARTS = "parts";
    public static final String DOCUMENTS_PARTS_GEPARTNBR = "gepartnbr";
    public static final String DOCUMENTS_PARTS_VINPARTNBR = "vinpartnbr";
    public static final String DOCUMENTS_MULTIBROWSER = "multibrowser";
    public static final String DOCUMENTS_ALERTS = "alert";
    public static final String DOCUMENTS_CATEGORY = "category";

    public static final String DOCUMENTS_UPLOADMONTH = "uploadmonth";
    public static final String DOCUMENTS_UPLOADQUARTER = "uploadquarter";
    public static final String DOCUMENTS_UPLOADYEAR = "uploadyear";

    public static final String COLLECTION_ENTITLEMENT = "entitlements";
    public static final String DOCTYPE = "doctype";
    public static final String TOKENTYPE = "tokentype";
    public static final String ENTITLEMENTS = "entitlements";
    public static final String ENTITLEMENTS_ENDDATE = "entitlements.enddate";
    public static final String ENTITLEMENTS_STARTDATE = "entitlements.startdate";
    public static final String ENTITLEMENTS_ID = "entitlementid";
    public static final String ENTITLEMENTS_SUBSYSTEM = "entitlements.subsystem";
    public static final String ENTITLEMENTS_PORTALID = "entitlements.portalid";
    public static final String ENTITLEMENTS_TOKENTYPE = "entitlements.tokentype";
    public static final String ENTITLEMENTS_TOKEN = "entitlements.token";
    public static final String TOKEN = "token";
    public static final String DOCUMENT = "document";
    public static final String MFILE = "mfile";
    public static final String FIELD_TYPE_HTML = "HTML";
    public static final String FILE_EXTENSION_HTM = "htm";
    public static final String METADATA_PREFIX = "metadata.";
    public static final String METADATA = "metadata";
    public static final String ELEMNAMES_MATCH = "match";
    public static final String NO_CHANGE = "no-change";
    public static final String PARENT_NODE_ID = "parentnodeid";
    public static final String SB = "sb";
    public static final String IC = "ic";
    public static final String SMM = "smm";
    public static final String TR_SMALL = "tr";
    public static final String PREPARE_DOCUMENT_TD = "prepareDocumentTD  (";
    public static final String SYM_ENTITLEMENTS_TOKEN = "$entitlements.token";

    public static final String METADATA_LASTUPDATEDDATE = "lastupdateddate";
    public static final String METADATA_LASTUPDATEDBY = "lastupdatedby";
    public static final String METADATA_FILENAME = "filename";
    public static final String METADATA_KEY = "key";
    public static final String METADATA_FILE = "file";
    public static final String TOC = "toc";
    public static final String RESOURCE_SCT = "/sct/";
    public static final String RESOURCE_MANS = "/mans/";
    public static final String RESOURCE_FILE = "/file/";
    public static final String RESOURCE_DOC = "/doc/";
    public static final String METADATA_TYPE = "metadata.type";
    public static final String METADATA_SUBSYSTEM = "metadata.subsystem";

    public static final String PREPARED_WRAPPED_BIN = "prepareWrappedBinResource";

    public static final String EMPTY_STRING = "";
    public static final String UNDEFINED_NUMBER_VALUE = "0";

    public static final String TYPE_PARAM = "?type=";

    public static final String PART_NUMBER_TEXT = "Part Number List for:";

    public static final String GET_BINARY_RESOURCE = "getBinaryResource - Requested file is not contained in a subdirectory of the program. (";
    public static final String PREPARE_DOCUMENT_TD_LOGGER = "prepareDocumentTD - Requested file is not contained in a subdirectory of the program. (";
    public static final String GET_BINARY_RESOURCE_LOGGER = "getBinaryResource (";
    public static final String GET_BINARY_RESOURCE_SUMMARY_LOGGER = "getHTMLResourceSummaryTD  (";
    public static final String DOCUMENT_LOGGER = "Document not available (";
    public static final String LOOKUP_TARGET_LOGGER = "lookupTarget.collect (";
    public static final String DOCUMENT_NOT_AVAILABLE = "Document not available (";
    public static final String DOCUMENT_SUMMARY_NOT_AVAILABLE = "Document Summary not available (";
    public static final String LOOKUP_TARGET = "lookupTarget (";
    public static final String DOCUMENT_HTML = "html/select-document.htm";
    public static final String DOCUMENT_HTML_NOT_FOUND = "displayDocumentNotFound (html/document-not-found.htm)";
    public static final String LOGO_LOGGER = "getLogo (";

    public static final String EXISTS = "$exists";
    public static final String OR = "$or";
    public static final String IN = "$in";
    public static final String LT = "$lt";
    public static final String LTE = "$lte";
    public static final String GTE = "$gte";
    public static final String AND = "$and";
    public static final String NE = "$ne";
    public static final String MATCH = "$match";
    public static final String UNWIND = "$unwind";
    public static final String AS = "as";
    public static final String FOREIGN_FIELD = "foreignField";
    public static final String LOCAL_FIELD = "localField";
    public static final String FROM = "from";
    public static final String LOOKUP = "$lookup";
    public static final String EQ = "$eq";
    public static final String COND = "$cond";
    public static final String PROJECT = "$project";
    public static final String ADD_TO_SET = "$addToSet";
    public static final String PRESERVE_NULL_AND_EMPTY_ARRAYS = "preserveNullAndEmptyArrays";
    public static final String PATH = "path";
    public static final String GROUP = "$group";
    public static final String ARRAY_ELEM_AT = "$arrayElemAt";
    public static final String ADD = "$add";
    public static final String MULTIPLY = "$multiply";
    public static final String UPLOADYEAR_VAL = "$uploadyear";
    public static final String DOCUMENTS_UPLOADMONTH_VAL = "$uploadmonth";
    public static final String JANUARY = "January";
    public static final String FEBRUARY = "February";
    public static final String MARCH = "March";
    public static final String APRIL = "April";
    public static final String MAY = "May";
    public static final String JUNE = "June";
    public static final String JULY = "July";
    public static final String AUGUST = "August";
    public static final String SEPTEMBER = "September";
    public static final String OCTOBER = "October";
    public static final String NOVEMBER = "November";
    public static final String DECEMBER = "December";
    public static final String ENTITLEMENT = "$entitlements";
    public static final String CAP_ADMIN = "ADMIN";
    public static final String SMALL_ADMIN = "admin";
    public static final String TOKENS = "tokens";
    public static final String ID = "id";

    // CMM constants
    public static final String RESOURCE_URI_CMM = "/techpubs/techdocs/";
    public static final String SERVICE_URL = "/services/techpubs/techdocs";
    public static final String SERVICE_URL_CSS = "/services/techpubs/techdocs/css/";
    public static final String RESOURCE_URI_CMMTR = "/techpubs/techdocs/cmms/tr/";
    public static final String RESOURCE_URI_PGMS = "/techpubs/techdocs/pgms/";
    public static final String RESOURCE_DOCUMENTS_ASSOCIATED = "/associated";
    public static final String RESOURCE_DOCUMENTS_CMM_PARTS = "/parts";
    public static final String DOCUMENTS_SUBSYSTEM_VALUE_CMM = "CMM";
    public static final String DOCUMENTS_DOCTYPE_VALUE_CMM = "CMM";
    public static final String RESOURCE_CMM = "cmms/";
    public static final String RESOURCE_CMM_LOWER_CASE = "cmm";

    public static final String QUERY_PARAM_RELDATE = "reldate";
    public static final String QUERY_PARAM_RELDATEFROM = "reldatefrom";
    public static final String QUERY_PARAM_RELDATETO = "reldateto";
    public static final String ALERT_COVER = "alert-cover";

    public static final String UTF_8 = "UTF-8";
    public static final String CSS = "CSS";

    public static final String EMBED = "embed";
    public static final String SRC = "src";
    public static final String TYPE = "type";
    public static final String DATA = "data";
    public static final String BODY = "body";
    public static final String V = "v";
    public static final String M = "M";
    public static final String Q = "Q";
    public static final String URN_SCHEMAS = "urn:schemas-microsoft-com:vml";
    public static final String SPAN = "span";
    public static final String TR = "TR";

    public static final String Y = "Y";

    public static final String LOGGER_GETCMMTR = "getCMMTRs";
    public static final String LOGGER_GETCMMPARTS = "getCMMParts";
    public static final String LOGGER_JSONEXCEPTION = "JsonMappingException";
    public static final String LOGGER_GETARTIFACT = "getArtifact";
    public static final String LOGGER_ASSOCIATED_SM = "getAssociatedDocumentsSM";
    public static final String LOGGER_GETCSSRESOURCE = "getCSSResource (";
    public static final String LOGGER_MONGOERROR = ") - mongoDB Error";
    public static final String LOGGER_NOCSSFILE = ") - Can not find CSS File";
    public static final String LOGGER_NOSTREAMMONGO = ") - Can not stream from mongoDB";

    // AOW Constants
    public static final String REF_DOCS = "refdocs";
    public static final String REF_DOCS_VAL = "$refdocs";
    public static final String ASSOC_DOCS_VAL = "$assocdocs";
    public static final String ASSOC_DOCS = "assocdocs";
    public static final String REF_WIRES_VAL = "$refwires";
    public static final String REF_WIRES = "refwires";
    public static final String ASSOCIATED = "/associated";
    public static final String REF_DOCS_VAL_DOCUMENTS_DOCNBR = "$refdocs.docnbr";
    public static final String REF_DOCS_VAL_DOCUMENTS_FILEID = "$refdocs.fileid";
    public static final String REF_DOCS_VAL_DOCUMENTS_TITLE = "$refdocs.title";
    public static final String REF_DOCS_VAL_DOCUMENTS_MODELS = "$refdocs.models";
    public static final String REF_DOCS_VAL_DOCUMENTS_REVDATE = "$refdocs.revdate";
    public static final String REF_DOCS_VAL_DOCUMENTS_CREATIONDATE = "$refdocs.creationdate";
    public static final String DOCUMENTS_ID_VAL_DOCUMENTS_ID = "$_id._id";
    public static final String DOCUMENTS_ID_VAL_DOCUMENTS_DOCNBR = "$_id.docnbr";
    public static final String DOCUMENTS_ID_VAL_DOCUMENTS_MODELS = "$_id.models";
    public static final String DOCUMENTS_ID_VAL_DOCUMENTS_TITLE = "$_id.title";
    public static final String DOCUMENTS_ID_VAL_DOCUMENTS_FILEID = "$_id.fileid";
    public static final String DOCUMENTS_ID_VAL_DOCUMENTS_REVDATE = "$_id.revdate";
    public static final String DOCUMENTS_ID_VAL_DOCUMENTS_CREATIONDATE = "$_id.creationdate";
    public static final String LOGGER_GETAOWDOCUMENTS = "getAOWDocument";

    // LL Constants
    public static final String DOCUMENTS_REVISION = "revision";
    public static final String STATUS = "status";
    public static final String INIT = "/init";
    public static final String DOCUMENTS_DOCTYPEID_VAL = "$doctypeid";
    public static final String LOGGER_GETLLDOC = "getLLDocument";
    public static final String LOGGER_GETLLCONFPRES = "getAssociatedDocumentsLLConfPres";
    public static final String LOGGER_GETLLUPDATES = "getAssociatedDocumentsLLUpdates";
    public static final String LOGGER_GETLLREFMATERIAL = "getAssociatedDocumentsLLRefMaterial";
    public static final String CONFERENCE_PRESENTATIONS = "Conference Presentations";
    public static final String RESOURCE_URI_LL = "/techpubs/techdocs/ll/";

    // WSPG Constants
    public static final String LOGGER_GETWSPGDOCUMENTS = "getWSPGDocument";

    // FH Constants
    public static final String FHS = "FHs";
    public static final String SORTFIELD = "sortField";
    public static final String SORTFIELD_VAL = "$sortField";
    public static final String NEWSLETTER = "Newsletter";
    public static final String STATISTICS = "Statistics";
    public static final String OPERATION = "Operation";
    public static final String DOCUMENTS = "Documents";
    public static final String GENERAL = "General";
    public static final String SHOP = "Shop";
    public static final String LINE = "Line";
    public static final String SCT = "/sct/";
    public static final String ART = "/art/";
    public static final String DOC_SEQ = "docseq";
    public static final String DISPLAYORDER = "displayOrder";
    public static final String ASC = "asc";
    public static final String LOGGER_GETFHDOC = "getFHDocument";
    public static final String LOGGER_GETFHSECDOC = "getFHSectionDocuments";
    public static final String LOGGER_GETFHARTDOC = "getFHArticleDocuments";
    public static final String OVERALL = "Overall";
    public static final String RESOURCE_URI_FHS = "/techpubs/techdocs/fhs/";
    public static final String LOGGER_GETFHDOCSCT = "getFHDocumentSCT";
    public static final String SERVICES = "/services";
    public static final String PDF = "/pdf";
    public static final String LOGGER_GETFHDOCART = "getFHDocumentART";

    // WSPG Constants
    public static final String LOGGER_GETSMASSOCIATEDDOCUMENTS = "getAssociatedDocuments";
    public static final String LOGGER_GETSMDOCUMENTS = "getWSPGDocument";

    // TP Constants
    public static final String LOGGER_GETTPDOCS = "getTPDocument";
    public static final String FLEET_RELIABILITY_SCORECARD = "Fleet Reliability Scorecards";
    public static final String CONFERENCE_PRESENTATION = "Conference Presentations";
    public static final String REFERENCE_MATERIALS = "Reference Materials";
    public static final String UPDATES = "Updates";
    public static final String DOC = "doc";
    public static final String STATUS_VAL = "$status";
    public static final String SORT_VAL = "$sort";
    public static final String ID_SORTFIELD = "_id.sortField";
    public static final String LIMIT = "$limit";
    public static final String ID_SORTFIELD_VAL = "$_id.sortField";
    public static final String DOC_FILE_ID = "$doc.fileid";
    public static final String DOC_STATUS = "$doc.status";
    public static final String DOC_CREATIONDATE = "$doc.creationdate";
    public static final String DOC_TITLE = "$doc.title";
    public static final String DOC_DOCTYPE_ID = "$doc.doctypeid";
    public static final String DOC_DOCTYPE = "$doc.doctype";
    public static final String DOC_VAL = "$doc";
    public static final String RESOURCE_URI_GENERIC = "/techpubs/techdocs/";
    public static final String LOGGER_GETASSOCIATEDDOCUMENTSTPSCORECARD = "getAssociatedDocumentsTPScorecard";
    public static final String LOGGER_GETASSOCIATEDDOCUMENTSTPCONFPRES = "getAssociatedDocumentsTPConfPres";
    public static final String LOGGER_GETASSOCIATEDDOCUMENTSTPUPDATES = "getAssociatedDocumentsTPUpdates";
    public static final String LOGGER_GETASSOCIATEDDOCUMENTSTPREFMATERIAL = "getAssociatedDocumentsTPRefMaterial";

    //TPS Constants
    public static final String TPS_VIEW_FILENAME = "VIEW_FILENAME";
    public static final String TPS_TOC_KEY = "TOC_KEY";
    public static final String TPS_REV_DATE = "REV_DATE";
    public static final String TPS_TITLE = "TITLE";
    public static final String TPS_CATEGORY = "CATEGORY";
    public static final String TPS_TYPE = "TYPE";


    //URI Constants
    public static final String  BOOKCASE_KEY_URI_PARAMETER= "{bookcaseKey}";
    public static final String  BOOK_KEY_URI_PARAMETER= "{bookKey}";
    public static final String  FILENAME_URI_PARAMETER= "{fileName}";
    public static final String  TOC_VERSION_URI_PARAMETER = "{version}";
    public static final String VERSIONS_URI_PATH = "/versions/";
    public static final String TECHPUBS_FILE_URI = "/techpubs/techdocs/pgms/" + BOOKCASE_KEY_URI_PARAMETER + VERSIONS_URI_PATH
        + TOC_VERSION_URI_PARAMETER + "/mans/"  + BOOK_KEY_URI_PARAMETER+ "/file/" + FILENAME_URI_PARAMETER;
    public static final String SB_BOOK_KEY = "sbs";
    public static final String TECHPUBS_DOCS_PGMS = "/techpubs/techdocs/pgms/";
    public static final String MANS_SBS_FILE = "/mans/sbs/file/";
    public static final String ALERT = "alert";

    //TOC Constants
    public static final String IC_PAGEBLK_TYPE = "ic";
    public static final String TR_PAGEBLK_TYPE = "tr";
    public static final String SB_PAGEBLK_TYPE = "sb";
    public static final String LR_TYPE = "lr";
    public static final String LR_BOOK_TYPE = "lrs";
    public static final String MANUAL_PAGEBLK_TYPE = "manual";
    public static final String SBALERT_PAGEBLK_TYPE = "sbalert";
    public static final String BOOK_TYPE = "book";
    public static final String BOOK_TYPE_PHOTO_GUIDE = "da";


    public static final String IC_BOOKCASE_TOC_MODEL_TITLE = "Incremental Changes (ICs)";
    public static final String TR_BOOKCASE_TOC_MODEL_TITLE = "Temporary Revisions (TRs)";
    public static final String ARCHIVED_SB_BOOK_TITLE = "Archived SBs";
    public static final String PLACEHOLDER_SECTION_TITLE = "--";

    //Techlib DB Constants
    public static final String TECHLIB_NODE_KEY = "node_key";
    public static final String TECHLIB_ID = "id";
    public static final String TECHLIB_PARENT_ID = "parent_id";
    public static final String TECHLIB_NODE_TYPE = "node_type";
    public static final String TECHLIB_TITLE = "title";
    public static final String TECHLIB_TOC_TITLE = "toc_title";
    public static final String TECHLIB_REVISION_DATE = "revision_date";
    public static final String TECHLIB_FILENAME = "filename";
    public static final String TECHLIB_NODE_ORDER = "node_order";
    public static final String TECHLIB_TREE_DEPTH = "tree_depth";
    public static final String TECHLIB_PUBLICATION_TYPE_CODE = "publication_type_code";
    public static final String TECHLIB_REVISION = "revision";
    public static final String TECHLIB_APPROVE_PUBLISH_FLAG = "approve_publish_flag";
    public static final String TECHLIB_FILE_NAME= "file_name";
    public static final String TECHLIB_PAGEBLK_KEY= "pageblk_key";
    public static final String TECHLIB_BOOK_KEY= "book_key";
    public static final String TECHLIB_BOOK_TITLE = "book_title";
    public static final String TECHLIB_BOOK_REVISION_DATE = "book_revision_date";
    public static final String TECHLIB_BOOK_REVISION_NUM = "book_revision_num";
    public static final String TECHLIB_BOOKCASE_KEY = "bookcase_key";
    public static final String TECHLIB_BOOKCASE_TITLE = "bookcase_title";
    public static final String TECHLIB_BOOK_ORDER = "book_order";
    public static final String TECHLIB_SB_TYPE = "sb_type";
    public static final String TECHLIB_TYPE = "type";

    public static final String FUNCTION_PARAM_ORGID = "orgid";
    public static final String FUNCTION_PARAM_BOOKCASEKEY = "bookcasekey";
    public static final String FUNCTION_PARAM_BOOKCASEVERSION = "bookcaseversion";
    public static final String FUNCTION_PARAM_PAGEBLKTYPE = "pageblkType";
    public static final String BOOKCASETOCFUNCTION="SELECT * FROM techlib.loadTOCItemsByBookcase(:orgid, :bookcasekey, :bookcaseversion)";
    public static final String BOOKFUNCTION= "SELECT * FROM techlib.loadBooksByBookcaseKeyAndVersion(:bookcasekey, :bookcaseversion)";
    public static final String PAGEBLKFUNCTION="select * from techlib.loadPageblksByTypeBookcaseKeyAndVersion(:pageblkType, :bookcasekey, :bookcaseversion)";


    // VI Constants
    public static final String LOGGER_VIDOC = "getVIDocument";
    public static final String LOGGER_VIDOC_TYPES = "getVIDocumentTypes";

    public static final String HEIGHT = "height";
    public static final String STYLE = "style";

    // Doc Admin Audit Trail Constants
    public static final String APP_ID = "doc-admin";
    public static final String STRING = "String";
    public static final String CATEGORY = "category";
    public static final String ACTION = "action";
    public static final String MODIFIED_BY = "modifiedBy";

    private DataConstants() {

    }

}
