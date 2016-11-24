package com.cloudypedia.domain;

import static com.cloudypedia.service.OfyService.factory;
import static com.cloudypedia.service.OfyService.ofy;

import java.util.Calendar;
import java.util.Date;

import com.cloudypedia.form.TrackingForm;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import util.Utility;
/**
 * 
 * This class is a kind holds tracking data
 *
 */
@Entity
@Cache
public class TrackingLog extends BaseKindCompany{
	
	@Id
	private Long id;
	
	/**
	 * user email
	 */
	private String email;
	
	/**
	 * user id
	 */
	@Index
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * location longitude
	 */
	private double longitude;
	/**
	 * location latitude
	 */
	private double latitude;
	
	/**
	 * location providers in mobile
	 */
	private String providers;
	
	/**
	 * location date
	 */
	@Index
	private Date date;
	
	@Index
	private int dateDay;

	public TrackingLog(){}
	
	public static TrackingLog createTracking(TrackingForm trackingForm, Key<Company> companyKey) {
		final Key<TrackingLog> trackingKey = factory().allocateId(companyKey, TrackingLog.class);
	    final long trackingId = trackingKey.getId();
	    
	    TrackingLog tracking = new TrackingLog();
	    
	    tracking.setId(trackingId);
	    tracking.setCompanyKey(companyKey);
	    
	    tracking.setEmail(trackingForm.getEmail());
	    tracking.setUserId(trackingForm.getUserId());
	    tracking.setLongitude(trackingForm.getLongitude());
	    tracking.setLatitude(trackingForm.getLatitude());
	    tracking.setProviders(trackingForm.getProviders());
	    
	   
	    Calendar calendar = Calendar.getInstance();
		if(trackingForm.getDate() != null)
			calendar.setTimeInMillis(Long.valueOf(trackingForm.getDate()));
		tracking.setDate(calendar.getTime());
	    
	    ofy().save().entity(tracking).now();
	    
	    return tracking;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
		this.setDateDay(Utility.getDateDay(date.getTime()));
	}

	public int getDateDay() {
		return dateDay;
	}

	public void setDateDay(int dateDay) {
		this.dateDay = dateDay;
	}
	
}

