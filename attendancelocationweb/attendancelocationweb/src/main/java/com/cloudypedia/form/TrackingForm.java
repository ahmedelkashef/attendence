package com.cloudypedia.form;

public class TrackingForm {
	private String email;
	private double longitude;
	private double latitude;
	private String providers;
	private String idToken;
	private String userIdToken;
	private String date;
	
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

	public TrackingForm(){}
	
//	public String getDomain(){
//		String[] temp = email.split("@");
//		
//		if(temp.length > 1)
//			return temp[1];
//		else
//			return null;
//	}
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

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	public String getUserIdToken() {
		return userIdToken;
	}

	public void setUserIdToken(String userIdToken) {
		this.userIdToken = userIdToken;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
