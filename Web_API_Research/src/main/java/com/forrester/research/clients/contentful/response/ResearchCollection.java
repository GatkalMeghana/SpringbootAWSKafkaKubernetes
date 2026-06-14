package com.forrester.research.clients.contentful.response;

import java.io.Serializable;
import java.util.List;

public class ResearchCollection implements Serializable{
   
	private static final long serialVersionUID = -76245133657865861L;
	
	private long total;
	
    private List<ResearchItem> items;

    public long getTotal() {
        return total;
    }

    public List<ResearchItem> getItems() {
        return items;
    }
}
