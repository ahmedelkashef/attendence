package com.cloudypedia.form;

import java.util.Set;

public class AttendanceQueryByGroup {
	public static final String TAB_GROUP = "group";
	public static final String TAB_EMPLOYEE = "employee";
	/**
	 * there is no parameters
	 */
	private boolean defaulteRequest = true;
	
	private int groupIndex;
	
	private String activeTab;
	private String email;
	/**
	 * start date in milliseconds 
	 */
	private long startDate;
	
	/**
	 * end date in milliseconds 
	 */
	private long endDate;
	
	/**
	 * groupId
	 */
	private Set<Long> groupIds;

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public Set<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Set<Long> groupIds) {
		this.groupIds = groupIds;
	}

	public boolean isDefaulteRequest() {
		return defaulteRequest;
	}

	public void setDefaulteRequest(boolean defaulteRequest) {
		this.defaulteRequest = defaulteRequest;
	}

	public int getGroupIndex() {
		return groupIndex;
	}

	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	}

	public String getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
