package com.forrester.research.view;

import java.io.Serializable;

/**
 * @author dsayyaparaju
 * @version 2.0
 */

public class ResearchView implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7698614985226421899L;
	private Object research;

	public Object getResearch() {
		return research;
	}

	public void setResearch(Object research) {
		this.research = research;
	}	
}