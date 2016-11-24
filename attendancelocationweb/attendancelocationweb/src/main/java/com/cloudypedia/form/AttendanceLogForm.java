package com.cloudypedia.form;

public class AttendanceLogForm {
	private String email;
	private String user;
	private String status;
	private double longitude;
	private double latitude;
	private String providers;
	private String imagekeyStr;
	private String date;
	private String idToken;
	private String userIdToken;
	
	/**
	 * user id
	 */
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public AttendanceLogForm(){}
	
	public String getDomain(){
		String[] temp = email.split("@");
		
		if(temp.length > 1)
			return temp[1];
		else
			return null;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getProviders() {
		return providers;
	}

	public void setProviders(String providers) {
		this.providers = providers;
	}

	public String getName() {
		return user;
	}

	public void setName(String name) {
		this.user = name;
	}

	public String getStatus() {
		return status;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImagekeyStr() {
		return imagekeyStr;
	}

	public void setImagekeyStr(String imagekeyStr) {
		this.imagekeyStr = imagekeyStr;
	}

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserIdToken() {
		return userIdToken;
	}

	public void setUserIdToken(String userIdToken) {
		this.userIdToken = userIdToken;
	}

	
}
