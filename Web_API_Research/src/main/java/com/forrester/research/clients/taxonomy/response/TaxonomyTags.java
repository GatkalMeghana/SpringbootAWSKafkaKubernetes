package com.forrester.research.clients.taxonomy.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaxonomyTags {

    private List<TaxonomyTag> tags;

    public List<TaxonomyTag> getTags() {
        return CollectionUtils.isEmpty(tags) ? Collections.emptyList() : tags;
    }

    public void setTags(List<TaxonomyTag> tags) {
        this.tags = tags;
    }
}
