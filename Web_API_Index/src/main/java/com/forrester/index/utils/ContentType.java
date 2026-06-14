package com.forrester.index.utils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ContentType {

	RESEARCH_DOCUMENT("RES"),
	WEBINARS("WEB"),
	PLAYBOOKS("PLA"),
	BLOGS("BLOG"),
	SURVEYS("SUS"),
	EVENTS("EVE"),
	FORUMS("FRM"),
	WORKSHOPS("WOR"),
	ANALYST("BIO");

	private String type;
	private String metaID;

	ContentType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getMetaID() {
		return metaID;
	}

	public static ContentType fromMessage(String message) {
		Optional<ContentType> optionalContentType = Arrays.asList(values()).stream().filter(s -> message.startsWith(s.getType())).findFirst();
		if(optionalContentType.isPresent()) {
			ContentType contentType = optionalContentType.get();
			contentType.metaID = message.replace(contentType.getType(), "");
			return contentType;
		}
		throw new IllegalArgumentException(String.format("Invalid value '%s' for content type given! Has to be either %s (case insensitive).", message,
				Arrays.asList(values()).stream().map(contentType -> " '" + contentType.name().toLowerCase(Locale.US) + "' ")
						.collect(Collectors.joining("or"))));
	}
}
