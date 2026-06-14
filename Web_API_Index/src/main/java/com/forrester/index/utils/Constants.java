package com.forrester.index.utils;

public class Constants {

    public static final String APIKEY_HEADER_TOKEN = "apiKey";
    public static final String COMMA = ",";
    public static final String PRI_PREFIX = "PRI";
    public static final String SER_PREFIX = "SER";

    //ResearchContainerServiceImpl
    public static final String TYPE = "type";
    public static final String RELATEDMATLIST = "RELATEDMATLIST";
    public static final String VALUE = "value";
    public static final String RELATED_MAT_LIST_ITEM = "relatedMatListItem";

    //SurveyIndexServiceImpl
    public static final String SURVEY_PREFIX = "SUS";
    public static final String TEXT = "text";
    public static final String SYNONYM_FILE_PATH = "SYNONYM-FILE-PATH";
    public static final String SYNONYMS_ANALYZER_SETTING_JSON = "synonyms-analyzer-setting.json";
    public static final String SURVEY_ALIAS_NAME = "readAliasSurveyIndex";
    public static final String SURVEY = "survey";

    //SurveyClient
    public static final String CONTENT_IDS = "contentIds";
    public static final String SURVEY_IDS = "surveyIds";

    //Webinar client
    public static final String SKIP = "skip";
    public static final String LIMIT = "limit";
    public static final String FORMATTED_CONTENT = "formattedContent";
    public static final String IDS = "ids";
    public static final String ID = "id";

    //AnalystIndexServiceImpl
    public static final String ANALYST_PREFIX = "BIO";
    public static final String ANALYST_BIO = "/analyst-bio/";
    public static final int API_RECORDS_LIMIT = 1000;

    //ForumIndexServiceImpl
    public static final String FORUM_PREFIX = "FRM";
    public static final String ALIAS_NAME = "readAliasForumIndex";
    public static final String FORUM_TYPE = "forum";
    public static final int FORUM_API_RECORD_LIMIT = 25;

    //WebinarIndexServiceImpl
    public static final String CONTENT_ID = "contentId";
    public static final int WEBINAR_SERVICE_API_LIMIT = 25;
    public static final int WEBINAR_ALL_ID_LIMIT = 1000;
    public static final String WEBINAR_PREFIX = "WEB";
    public static final String AMERICA_NEW_YORK_TIMEZONE = "America/New_York";

    //FeedResponseHandler
    public static final String BLOG_PREFIX = "BLG";
    public static final String PODCAST_PREFIX = "PDC";

    //CacheInvalidateConsumer
    public static final String ENDICA_ID = "Endica_Id";
}
