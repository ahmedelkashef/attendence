package com.cloudypedia.form;

import java.util.Set;

public class WorkLocationForm {
	private Long id;
	private String name;
	private double longitude;
	private double latitude;
	private int areaRaduis;// in meters 
	private String wifiIDs;
	private Set<Long> groupIds;

	public WorkLocationForm(){}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getAreaRaduis() {
		return areaRaduis;
	}

	public void setAreaRaduis(int areaRaduis) {
		this.areaRaduis = areaRaduis;
	}

	public String getWifiIDs() {
		return wifiIDs;
	}

	public void setWifiIDs(String wifiIDs) {
		this.wifiIDs = wifiIDs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Set<Long> groupIds) {
		this.groupIds = groupIds;
	}
	
}
