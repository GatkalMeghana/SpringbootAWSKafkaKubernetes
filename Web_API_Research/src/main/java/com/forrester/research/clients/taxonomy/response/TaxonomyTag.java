package com.forrester.research.clients.taxonomy.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.forrester.research.utils.TaggingInfoTypeEnum;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaxonomyTag {
    private String name;
    private String id;
    private TaxonomyTag parent;
    private String referenceSourceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaxonomyTag getParent() {
        return parent;
    }

    public void setParent(TaxonomyTag parent) {
        this.parent = parent;
    }

    public TaggingInfoTypeEnum getType(boolean isLegacy, String tagId) {
        if (null == parent) {
            return TaggingInfoTypeEnum.fromTypeId(id);
        }

        if (isLegacy && parent.getId().equals(tagId)) {
            return TaggingInfoTypeEnum.fromTypeId(tagId);
        }

        return parent.getType(isLegacy, tagId);
    }

    public void getRowsNames(List<String> rows, TaxonomyTag parentTag) {
        if (null == parentTag.getParent()) {
            return;
        }
        rows.add(parentTag.getName());
        getRowsNames(rows, parentTag.getParent());
    }

    public boolean isTaggedAtParentLevel() {
        return parent != null && (TaggingInfoTypeEnum.TYPE_SD_SERVICES.getTypeId().equals(parent.getId())
                || TaggingInfoTypeEnum.TYPE_SERVICES.getTypeId().equals(parent.getId())
                || TaggingInfoTypeEnum.TYPE_OLD_ROLE.getTypeId().equals(parent.getId())
                || TaggingInfoTypeEnum.TYPE_OLD_FORRESTER_PRODUCTS.getTypeId().equals(parent.getId())
                || TaggingInfoTypeEnum.TYPE_VISION.getTypeId().equals(parent.getId()));
    }

    public boolean isTaggedAtChildLevel() {
        return parent != null && parent.getParent() != null && (TaggingInfoTypeEnum.TYPE_SD_SERVICES.getTypeId().equals(parent.getParent().getId())
                || TaggingInfoTypeEnum.TYPE_SERVICES.getTypeId().equals(parent.getParent().getId())
                || TaggingInfoTypeEnum.TYPE_OLD_FORRESTER_PRODUCTS.getTypeId().equals(parent.getParent().getId())
                || TaggingInfoTypeEnum.TYPE_OLD_ROLE.getTypeId().equals(parent.getParent().getId())
                || TaggingInfoTypeEnum.TYPE_VISION.getTypeId().equals(parent.getParent().getId()));
    }

    public String getReferenceSourceId() {
        return referenceSourceId;
    }

    public void setReferenceSourceId(String referenceSourceId) {
        this.referenceSourceId = referenceSourceId;
    }
}
