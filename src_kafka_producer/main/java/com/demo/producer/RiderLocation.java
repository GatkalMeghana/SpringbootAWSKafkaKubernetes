package com.demo.producer;

public class RiderLocation{

	private String name;
	private double langitude;
	private double longitude;

	public RiderLocation(String name, double langitude, double longitude) {
		super();
		this.name = name;
		this.langitude = langitude;
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLangitude() {
		return langitude;
	}

	public void setLangitude(double langitude) {
		this.langitude = langitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
