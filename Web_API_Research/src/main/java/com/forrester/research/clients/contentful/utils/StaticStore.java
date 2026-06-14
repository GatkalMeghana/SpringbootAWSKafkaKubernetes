package com.forrester.research.clients.contentful.utils;

import com.contentful.java.cda.TransformQuery;

import java.util.List;
import java.util.Map;

public interface StaticStore {

    Map<String, Class> getStore();

    default void loadStore(String packageName, List<Class> includeFilter, List<Class> excludeFilter) {
        List<Class<?>> modelClasses = PackageUtil.scanClasses(packageName, includeFilter, excludeFilter);
        modelClasses.stream().forEach(modelClass -> {
            final TransformQuery.ContentfulEntryModel entryModel = modelClass.getDeclaredAnnotation(TransformQuery.ContentfulEntryModel.class);
            if (null != entryModel) {
                getStore().put(entryModel.value(), modelClass);
            }
        });
    }


}
