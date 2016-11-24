package com.cloudypedia.domain;

import static com.cloudypedia.service.OfyService.factory;
import static com.cloudypedia.service.OfyService.ofy;

import java.util.HashSet;
import java.util.Set;

import com.cloudypedia.form.WorkLocationForm;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
/**
 * 
 * this class represent a kind that hold work location or branches of an company
 *
 */
@Entity
@Cache
public class WorkLocation extends BaseKindCompany {
	
	@Id
	private Long id;
	
	private String name;
	private double longitude;
	private double latitude;
	private int areaRaduis = 100;// in meters 
	private String wifiIDs;
	private Set<Long> groupIds;
	
	public WorkLocation(){}
	
//	public static WorkLocation createWorkLocation(WorkLocationForm workLocationForm, Key<Company> companyKey) {
//		final Key<WorkLocation> workLocationKey = factory().allocateId(companyKey, WorkLocation.class);
//	    final long workLocationId = workLocationKey.getId();
//	    
//	    WorkLocation workLocation = new WorkLocation();
//	    
//	    workLocation.setId(workLocationId);
//	    workLocation.setCompanyKey(companyKey);
//	    workLocation.setName(workLocationForm.getName());
//	    workLocation.setLongitude(workLocationForm.getLongitude());
//	    workLocation.setLatitude(workLocationForm.getLatitude());
//	    workLocation.setAreaRaduis(workLocationForm.getAreaRaduis());
//	    workLocation.setWifiIDs(workLocationForm.getWifiIDs());
//	    
//	    ofy().save().entity(workLocation).now();
//	    
//	    return workLocation;
//	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getAreaRaduis() {
		return areaRaduis;
	}

	public void setAreaRaduis(int areaRaduis) {
		this.areaRaduis = areaRaduis;
	}

	public String getWifiIDs() {
		return wifiIDs == null ? "" : wifiIDs;
	}

	public void setWifiIDs(String wifiIDs) {
		this.wifiIDs = wifiIDs;
	}

	public static Key<WorkLocation> getKey(Key<Company> companyKey, Long workLocationId) {
		return Key.create(companyKey, WorkLocation.class, workLocationId);
	}
	
	public static WorkLocation createDefaulteWorkLocation(Key<Company> companyKey){
		final Key<WorkLocation> workLocationKey = factory().allocateId(companyKey, WorkLocation.class);
	    final long workLocationId = workLocationKey.getId();
	    
	    WorkLocation workLocation = new WorkLocation();
	    
	    workLocation.setId(workLocationId);
	    workLocation.setCompanyKey(companyKey);
	    
	    workLocation.setName("Untitled Work Location");
	    workLocation.setAreaRaduis(100);
	    workLocation.setWifiIDs("");
	    
	    ofy().save().entity(workLocation).now();
	    
	    return workLocation;
	}

	public static WorkLocation updateWorkLocation(WorkLocationForm workLocationForm, Key<Company> companyKey) {
		final Key<WorkLocation> workLocationKey = WorkLocation.getKey(companyKey, workLocationForm.getId());
		WorkLocation workLocation = ofy().load().key(workLocationKey).now();
	    
	    if(workLocation == null)
	    	return null;
	    
	    workLocation.setAreaRaduis(workLocationForm.getAreaRaduis());
	    workLocation.setLongitude(workLocationForm.getLongitude());
	    workLocation.setLatitude(workLocationForm.getLatitude());
	    workLocation.setName(workLocationForm.getName());
	    workLocation.setWifiIDs(workLocationForm.getWifiIDs());
	    workLocation.setGroupIds(workLocationForm.getGroupIds());
	    
	    ofy().save().entity(workLocation).now();
	    
		return workLocation;
	}
	
	public static boolean deleteWorkLocation(Key<WorkLocation> workLocationKey) {
		WorkLocation workLocation = ofy().load().key(workLocationKey).now();
		
		if(workLocation == null)
			return false;
		
		if(workLocation.getGroupIds() != null && !workLocation.getGroupIds().isEmpty())
			return false;
		
		ofy().delete().key(workLocationKey).now();
		
		return true;
	}

	public Set<Long> getGroupIds() {
		return groupIds == null ?new HashSet<Long>(): groupIds;
	}

	public void setGroupIds(Set<Long> groupIds) {
		this.groupIds = groupIds;
	}
	
	public void addGroupId(Long id){
		if(groupIds == null)
			groupIds = new HashSet<Long>();
		
		groupIds.add(id);
	}
	
	public void removeGroupId(Long id){
		if(groupIds == null)
			groupIds = new HashSet<Long>();
		
		groupIds.remove(id);
	}

}
