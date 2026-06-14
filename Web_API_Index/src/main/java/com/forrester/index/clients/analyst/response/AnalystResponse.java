package com.forrester.index.clients.analyst.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.forrester.index.clients.taxonomy.response.Tag;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AnalystResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("analystBioId")
    private String analystBioId;
    @JsonProperty("publishedDate")
    private String publishedDate;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("middleName")
    private String middleName;
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("title")
    private String title;
    @JsonProperty("urlName")
    private String urlName;
    @JsonProperty("userType")
    private String userType;
    @JsonProperty("image")
    private String image;
    @JsonProperty("education")
    private String education;
    @JsonProperty("externallyActive")
    private Boolean externallyActive;
    @JsonProperty("socialMedia")
    private Map<String,String> socialMedia;
    @JsonProperty("industries")
    private List<String> industries = null;
    @JsonProperty("topics")
    private List<String> topics = null;
    @JsonProperty("primaryRoles")
    private List<Object> primaryRoles = null;
    @JsonProperty("roles")
    private List<Object> roles = null;
    @JsonProperty("bioTitle")
    private String bioTitle;
    @JsonProperty("analystBlog")
    private String analystBlog;
    @JsonProperty("isClient")
    private Boolean isClient;
    @JsonProperty("researchFocus")
    private String researchFocus;
    @JsonProperty("previousWorkExperience")
    private String previousWorkExperience;
    @JsonProperty("dynamicAnalystImage")
    private String dynamicAnalystImage;
    @JsonProperty("analystImage")
    private String analystImage;
    @JsonProperty("tags")
    private Map<String,List<Tag>> tags;
}
