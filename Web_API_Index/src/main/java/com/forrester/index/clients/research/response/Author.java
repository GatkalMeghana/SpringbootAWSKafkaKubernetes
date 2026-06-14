package com.forrester.index.clients.research.response;

import java.io.Serializable;

public class Author implements Serializable {
	
	private static final long serialVersionUID = -5641971316098843268L;
	private String id;
	private String title;
	private String fullName;
	private String active;
	private String imageUrl;
	private String firstName;
	private String lastName;

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getFullName() {
		return fullName;
	}

	public String getActive() {
		return active;
	}

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
	
}
