package com.forrester.research.clients.contentful.parsers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.TransformQuery;
import com.contentful.java.cda.rich.CDARichNode;
import com.forrester.research.clients.contentful.parsers.models.RichNode;
import com.forrester.research.clients.contentful.response.ContentfulModelStore;
import com.forrester.research.clients.contentful.utils.PackageUtil;

@Component
public class ContentfulParser implements InitializingBean {

    protected static Logger logger = LoggerFactory.getLogger(ContentfulParser.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ContentfulModelStore modelStore;

    private Map<String, Object> parserStore = new ConcurrentHashMap<>(16);

    public final RichNode parse(CDARichNode node) {

        RichNodeParser parser = (RichNodeParser) parserStore.get(node.getClass().getSimpleName());
        if(null != parser) {
            return parser.parse(node);
        } else {
            logger.info("Parser not found for component : {}", node.getClass().getSimpleName());
        }
        return null;
    }

    public final Object entryToModelObject(CDAEntry e) {
        String id = e.contentType().id();

        Class c = modelStore.getModel(id);
        if (null == c) {
            logger.info("Could not found model class for content type : {}", id);
            return null;
        }

        Object o;
        try {
            o = c.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            logger.info("Cannot create new instance of {} class", c.getSimpleName());
            return null;
        }

        for (final Field f : c.getDeclaredFields()) {
            try {
                final TransformQuery.ContentfulField cField = f.getAnnotation(TransformQuery.ContentfulField.class);
                if (cField != null) {
                    Method m = c.getDeclaredMethod("set" + StringUtils.capitalize(f.getName()), f.getType());
                    m.invoke(o, f.getType().cast(e.getField(cField.value())));
                } else {
                    final TransformQuery.ContentfulSystemField cSysField = f.getAnnotation(TransformQuery.ContentfulSystemField.class);
                    if (cSysField != null) {
                        Method m = c.getDeclaredMethod("set" + StringUtils.capitalize(f.getName()), f.getType());
                        m.invoke(o, f.getType().cast(e.getAttribute(cSysField.value())));
                    }
                }
            } catch (ReflectiveOperationException ex) {
                logger.info("Cannot set value using setter for field {}", f.getName());
            }
        }

        return c.cast(o);
    }

    @Override
    public void afterPropertiesSet() {
        List<Class<?>> parsers = PackageUtil.scanClasses("com.forrester.research.clients.contentful.parsers.components", Collections.singletonList(Component.class), Collections.emptyList());

        parsers.stream().forEach(modelClass -> {
            final Component component = modelClass.getDeclaredAnnotation(Component.class);
            if (null != component) {
                parserStore.put(component.value(), applicationContext.getBean(modelClass));
            }
        });

        if (parserStore.isEmpty()) {
            logger.info("Cannot read any parser with component annotation");
            throw new BeanInitializationException("Cannot read any parser with component annotation");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Current parsers supported : {}", parserStore.keySet().stream().collect(Collectors.joining(", ")));
        }
    }
}
