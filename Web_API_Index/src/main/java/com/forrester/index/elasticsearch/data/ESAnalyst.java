package com.forrester.index.elasticsearch.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Document(indexName = "analyst-#{@indexNameProvider.getIndexSuffix()}")
public class ESAnalyst extends AbstractTaggableContent implements Serializable {
    private static final long serialVersionUID = -7369484242060458316L;
    @Id
    private String analystBioID;
    private String publishedDate;
    private String firstName;
    private String lastName;
    private String middleName;
    @Field(analyzer = "synonym_analyzer", type = FieldType.Text, searchAnalyzer = "synonym_search_analyzer")
    private String fullName;
    private String title;
    private String urlName;
    private String userType;
    private String email;
    private String image;
    private String imageHighRes;
    private String researchFocus;
    private String previousWorkExperience;
    private String education;
    private boolean internallyActive;
    private boolean externallyActive;
    private Map<String, String> socialMedia = new LinkedHashMap<>();
    private List<String> primaryRoles = new LinkedList<>();
    private String contentId;
    private String contentURL;
    private String bioTitle;
    private String analystBlog;
    private boolean isClient;
    private String dynamicAnalystImage;
    private String analystImage;
    private String entryId;

    public String getAnalystBioID() {
        return analystBioID;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTitle() {
        return title;
    }

    public String getUrlName() {
        return urlName;
    }

    public String getUserType() {
        return userType;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getImageHighRes() {
        return imageHighRes;
    }

    public String getResearchFocus() {
        return researchFocus;
    }

    public String getPreviousWorkExperience() {
        return previousWorkExperience;
    }

    public String getEducation() {
        return education;
    }

    public boolean isInternallyActive() {
        return internallyActive;
    }

    public boolean isExternallyActive() {
        return externallyActive;
    }

    public Map<String, String> getSocialMedia() {
        return socialMedia;
    }

    public List<String> getPrimaryRoles() {
        return primaryRoles;
    }
   
    public String getContentId() {
		    return contentId;
	}

    public String getContentURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public void setAnalystBioID(String analystBioID) {
        this.analystBioID = analystBioID;
    }

    public String getBioTitle() {
        return bioTitle;
    }

    public String getAnalystBlog() {
        return analystBlog;
    }

    public boolean isClient() {
        return isClient;
    }

    public String getDynamicAnalystImage() {
        return dynamicAnalystImage;
    }

    public String getAnalystImage() {
        return analystImage;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }
}
