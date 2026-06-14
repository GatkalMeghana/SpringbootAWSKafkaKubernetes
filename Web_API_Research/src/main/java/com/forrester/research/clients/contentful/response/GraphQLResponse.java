package com.forrester.research.clients.contentful.response;

import java.io.Serializable;

public class GraphQLResponse implements Serializable {

	private static final long serialVersionUID = -8168460913962941774L;
	
	private ResponseData data;

	public ResponseData getData() {
		return data;
	}
}
