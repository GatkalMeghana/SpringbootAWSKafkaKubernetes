package com.forrester.research;

public class Constants {

    private Constants() {
        //Constants class need not be initialized.
    }

    // Messages that are frequently returned as part of the response
    public static final String RES_PREFIX = "RES";
    public static final String MISSING_AUTH_TOKEN_MSG = "Please provide authorization token.";
    public static final String PROVIDE_VALID_AUTH_TOKEN_MSG = "Please provide valid authorization token";
    public static final String RESEARCH_NOT_FOUND_FOR_ID = "Could not find research for given id.";
    public static final String USER_NOT_FOUND_FOR_EMAIL = "Could not find user for given email.";
    public static final String PERMISSION_INFORMATION_NOT_FOUND_FOR_EMAIL_CONTENT_ID = "Could not find permission information for given email and content id";
    public static final String ID_IS_INVALID = "Given Content Id is empty or invalid.";
    public static final String EMAIL_IS_INVALID = "Given email is empty or invalid.";
    public static final String DATA_NOT_FOUND_MSG = "Unable to find the content with the given details";
    public static final String GENERIC_EXCEPTION_MSG = "Generic exception. Please check stacktrace.";
    public static final String CONTENT_TYPE_NOT_SUPPORTED = "Content type is not supported";
    public static final String SERVICE_EXCEPTION_FROM_CONTENTFUL = "Service exception while getting content from contentful";
    public static final String APIKEY_HEADER_TOKEN = "apiKey";
    public static final String MEDIA_IMAGES = "/images/";
    public static final String MEDIA_ASSETS = "/assets/";
    public static final String RESEARCH = "research";
    public static final String EDITORIAL_IMAGE = "editorialImage";
    public static final String HERO_IMAGE = "editorialImage";
    public static final String RESEARCH_ABSTRACT = "researchAbstract";
    public static final String CONTRIBUTORS = "CONTRIBUTORS";
    public static final String AUTHORS = "AUTHORS";
    public static final String SLASH = "/";
    public static final String PUBLISHEDDATE = "publishedDate";
    public static final String CONTENTTYPE = "contentType";
    public static final String PRICE_CONFIG = "priceConfig";
    public static final String PRICE_STANDARD = "Standard";
    public static final String PRICE_FREE = "Free";
    public static final String PRICE_NOT_FOR_SALE = "Not for sale";
    public static final String PRICE_CUSTOM = "Custom";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT_TYPE = "[application/pdf]";
    public static final String APPSOURCE = "appSource";
    public static final String PDF_VCURL = "/Research/PDF";
    public static final String REPORT = "/report";
    public static final String FORRESTER_DECISIONS = "Forrester Decisions";
    public static final String SERVICE = "Service";
    public static final String PRIORITY = "Priority";
    public static final String PHASE = "Phase";
    public static final String FMI_ACCESS = "FMI Access";
    public static final String PHASE_DEFINE = "Define";
    public static final String CONST_MI_SERVICE = "FD:MI";
    public static final String CONST_MI_EG_SERVICE = "FD:MIEG";
    public static final String VISION = "Vision";
    public static final String HERITAGE_FORRESTER = "Heritage Forrester";
    public static final String PRODUCTS = "Products";
    public static final String ROLES = "Roles";
    public static final String HERITAGE_SIRIUSDECISIONS = "Heritage SiriusDecisions";
    public static final String LINE_SEPARATOR = "\n";
    public static final String ANALYST = "analyst";

	public static final String IS_FLEX_ACCESS_REDEEMED = "isFlexAccessRedeemed";
    public static final String MARKET = "Market";
    public static final String MARKET_INSIGHTS = "Market Insights (FMI)";
}
