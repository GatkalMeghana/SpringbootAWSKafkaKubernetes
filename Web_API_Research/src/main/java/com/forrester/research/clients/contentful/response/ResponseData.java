package com.forrester.research.clients.contentful.response;

import java.io.Serializable;

public class ResponseData implements Serializable{
	
	private static final long serialVersionUID = -3328441722674056625L;
	
	private ResearchCollection researchCollection;

    public ResearchCollection getResearchCollection() {
        return researchCollection;
    }
}
