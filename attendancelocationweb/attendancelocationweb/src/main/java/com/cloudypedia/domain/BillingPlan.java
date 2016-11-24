package com.cloudypedia.domain;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class BillingPlan {
	
	@Id
	private long id;
	
	public BillingPlan(){}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	

}
