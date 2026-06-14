package com.forrester.research.clients.contentful.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PackageUtil {

    public static final Logger logger = LoggerFactory.getLogger(PackageUtil.class);

    private PackageUtil() {}

    public static final List<Class<?>> scanClasses(String packageName, List<Class> includeClasses, List<Class> excludeClasses) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        includeClasses.stream().forEach(c -> scanner.addIncludeFilter(new AnnotationTypeFilter(c)));
        excludeClasses.stream().forEach(c -> scanner.addExcludeFilter(new AnnotationTypeFilter(c)));

        return scanner.findCandidateComponents(packageName).stream().map(rb -> {
            try {
                return cl.loadClass(rb.getBeanClassName());
            } catch (ClassNotFoundException e) {
                logger.error("Unable to load class {}.", rb.getBeanClassName());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }

        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }

        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}