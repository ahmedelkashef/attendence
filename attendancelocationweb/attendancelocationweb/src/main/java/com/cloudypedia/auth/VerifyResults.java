package com.cloudypedia.auth;

public class VerifyResults {
	private boolean valid;
	private String message;
	private String email;
	private String hostedDomain;
	private String userId;
	private String pictureUrl;
	
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHostedDomain() {
		return hostedDomain;
	}
	public void setHostedDomain(String hostedDomain) {
		this.hostedDomain = hostedDomain;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPictureUrl() {
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

}
