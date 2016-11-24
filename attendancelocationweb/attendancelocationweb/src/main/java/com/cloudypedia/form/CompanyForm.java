package com.cloudypedia.form;

import java.util.Set;

public class CompanyForm {

	public CompanyForm(){}
	
	private long id;

	/**
	 * company domains
	 */
	private Set<String> domains;
	/**
	 * company name
	 */
	private String name;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public Set<String> getDomains() {
		return domains;
	}

	public void setDomains(Set<String> domains) {
		this.domains = domains;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
