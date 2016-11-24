package com.cloudypedia.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;

/**
 * Hold common attributes and functionalities of kids that its parent is Company 
 *
 */

public abstract class BaseKindCompany {
	
	public BaseKindCompany() {
	}
	
	/**
     * Holds Company key as the parent.
     */
    @Parent
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @JsonIgnore
    private Key<Company> companyKey;
    
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Key<Company> getCompanyKey() {
		return companyKey;
	}

	public void setCompanyKey(Key<Company> companyKey) {
		this.companyKey = companyKey;
	}
}
