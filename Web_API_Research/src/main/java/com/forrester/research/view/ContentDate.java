package com.forrester.research.view;

import java.io.Serializable;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @author sgopal
 *
 */

@JsonComponent
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentDate implements Serializable {
    private static final long serialVersionUID = 8954899274006858277L;

    private String dateType;
    private String dateValue;

    public String getDateType() {
		return dateType;
	}
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	public String getDateValue() {
		return dateValue;
	}
	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}
	@Override
	public String toString() {
		return "ContentDate [dateType=" + dateType + ", dateValue=" + dateValue + "]";
	}
    
}