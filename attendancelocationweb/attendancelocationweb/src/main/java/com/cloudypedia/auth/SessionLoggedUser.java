package com.cloudypedia.auth;

import java.io.Serializable;

import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Employee;
import com.cloudypedia.domain.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Key;

public class SessionLoggedUser implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public static String SESSION_KEY = "sessionLoggedUser"; 
	
	private String email;
	private Role role;
	private String hostedDomain;
	private String userId;
	private String pictureUrl;
	private String logoUrl;
	

	@JsonIgnore
	private Key<Company> companyKey;
	@JsonIgnore
	private Key<Employee> userKey;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Key<Company> getCompanyKey() {
		return companyKey;
	}

	public void setCompanyKey(Key<Company> companyKey) {
		this.companyKey = companyKey;
	}

	public Key<Employee> getUserKey() {
		return userKey;
	}

	public void setUserKey(Key<Employee> userKey) {
		this.userKey = userKey;
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

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	
	public String getLogoUrl() {
		return logoUrl;
	}

}
