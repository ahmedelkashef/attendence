package com.cloudypedia.form;

import com.cloudypedia.domain.Role;

public class EmployeeForm {
	
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private Role role;
	private String userIdToken;
	
	public EmployeeForm(){}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserIdToken() {
		return userIdToken;
	}

	public void setUserIdToken(String userIdToken) {
		this.userIdToken = userIdToken;
	}

}
