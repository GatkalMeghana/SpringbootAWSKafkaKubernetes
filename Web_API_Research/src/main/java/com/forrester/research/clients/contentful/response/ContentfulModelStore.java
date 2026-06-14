package com.forrester.research.clients.contentful.response;

import com.contentful.java.cda.TransformQuery;
import com.forrester.research.clients.contentful.utils.StaticStore;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public final class ContentfulModelStore implements StaticStore, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ContentfulModelStore.class);

    private static Map<String, Class> contentMODELSTORE = new ConcurrentHashMap<>(16);

    public Map<String, Class> getStore() {
        return contentMODELSTORE;
    }

    public Class getModel(String id) {
        return getStore().getOrDefault(id, null);
    }

    public void afterPropertiesSet() {

        loadStore("com.forrester.research.clients.contentful.response.models", Lists.newArrayList(TransformQuery.ContentfulEntryModel.class), Collections.emptyList());

        if (getStore().isEmpty()) {
            logger.info("Cannot read any model class with ContentfulEntryModel annotation");
            throw new BeanInitializationException("Cannot read any model class with ContentfulEntryModel annotation");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Current models supported : {}", String.join(", ", getStore().keySet()));
        }
    }
}
