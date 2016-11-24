package com.cloudypedia.service;

import com.cloudypedia.domain.AttendanceLog;
import com.cloudypedia.domain.BillingPlan;
import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Employee;
import com.cloudypedia.domain.Group;
import com.cloudypedia.domain.TrackingLog;
import com.cloudypedia.domain.WorkLocation;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Custom Objectify Service that this application should use.
 */
public class OfyService {
	/**
	 * This static block ensure the entity registration.
	 */
	static {
		factory().register(TrackingLog.class);
		factory().register(AttendanceLog.class);
		factory().register(BillingPlan.class);
		factory().register(Company.class);
		factory().register(Employee.class);
		factory().register(Group.class);
		factory().register(WorkLocation.class);
		
	}


	/**
	 * Use this static method for getting the Objectify service object in order
	 * to make sure the above static block is executed before using Objectify.
	 * 
	 * @return Objectify service object.
	 */
	public static Objectify ofy() {
		return ObjectifyService.ofy();
	}

	/**
	 * Use this static method for getting the Objectify service factory.
	 * 
	 * @return ObjectifyFactory.
	 */
	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
}
