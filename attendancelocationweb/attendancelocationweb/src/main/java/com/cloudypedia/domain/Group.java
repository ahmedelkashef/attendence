package com.cloudypedia.domain;

import static com.cloudypedia.service.OfyService.factory;
import static com.cloudypedia.service.OfyService.ofy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cloudypedia.form.GroupForm;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * 
 * This class represent group definition and its settings
 *
 */
@Entity
@Cache
public class Group extends BaseKindCompany{
	
	@Id
	private long id;
	
	private HashSet<String> managers;
	private String name;
	private boolean trackingEnabled;
	/**
	 * interval in seconds
	 */
	private long trackingInterval;
	private boolean needImage;
	private boolean attendanceEnabled;
	private boolean notificationsEnabled;
	private boolean trackingAllTime;
	private boolean trackingDuringShiftOnly;
	private boolean attendanceInWorkLocationOnly;
	private boolean autoLocationAttendance;
	private boolean useUserLocation;
	private boolean useWifi;
	
	private long workLocationId;
	
	private WorkShift workShift;
	
	public Group(){}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isTrackingEnabled() {
		return trackingEnabled;
	}
	public void setTrackingEnabled(boolean trackingEnabled) {
		this.trackingEnabled = trackingEnabled;
	}
	public long getTrackingInterval() {
		return trackingInterval;
	}
	public void setTrackingInterval(long trackingInterval) {
		this.trackingInterval = trackingInterval;
	}
	public boolean isNeedImage() {
		return needImage;
	}
	public void setNeedImage(boolean needImage) {
		this.needImage = needImage;
	}

	public boolean isAttendanceEnabled() {
		return attendanceEnabled;
	}

	public void setAttendanceEnabled(boolean attendanceEnabled) {
		this.attendanceEnabled = attendanceEnabled;
	}

	public HashSet<String> getManagers() {
		return managers == null ? new HashSet<String>() : managers;
	}

	public void setManagers(HashSet<String> managers) {
		this.managers = managers;
	}
	
	public void addManager(String newManager) {
		if(newManager != null){
			if(this.managers == null)
				this.managers = new HashSet<String>();
			this.managers.add(newManager);
		}
    }
	
	
	public static Key<Group> getKey(Key<Company> companyKey, Long id){
		return Key.create(companyKey, Group.class, id);
	}
	
	public static List<Group> getGroupsEntities(Key<Company> companyKey, HashSet<Long> setId) {
		List<Key<Group>> keysList = new ArrayList<Key<Group>>();
		for(Long id : setId){
			keysList.add(Group.getKey(companyKey, id));
		}
        return new ArrayList<Group>(ofy().load().keys(keysList).values());
    }
	
	public static List<Group> getAllGroups(Key<Company> companyKey) {
		return ofy().load()
				.type(Group.class)
				.ancestor(companyKey)
				.list();
    }
	
	public static Group createDefaulteGroup(Key<Company> companyKey){
		final Key<Group> groupKey = factory().allocateId(companyKey, Group.class);
	    final long groupId = groupKey.getId();
	    
	    Group group = new Group();
	    
	    group.setId(groupId);
	    group.setCompanyKey(companyKey);
	    
	    group.setName("Untitled group");
	    group.setTrackingInterval(60);
	    group.setAttendanceEnabled(true);
	    group.setNeedImage(true);
	    group.setNotificationsEnabled(true);
	    group.setWorkLocationId(0);
	    
	    ofy().save().entity(group).now();
	    
	    return group;
	}

	public static Group updateGroup(GroupForm groupForm, Key<Company> companyKey) {
		final Key<Group> groupKey = Group.getKey(companyKey, groupForm.getId());
	    Group group = ofy().load().key(groupKey).now();
	    
	    if(group == null)
	    	return null;
	    
	    
	    group.setName(groupForm.getName());
	    group.setAttendanceEnabled(groupForm.isAttendanceEnabled());
	    group.setNeedImage(groupForm.isNeedImage());
	    group.setTrackingEnabled(groupForm.isTrackingEnabled());
	    group.setNotificationsEnabled(groupForm.isNotificationsEnabled());
	    group.setTrackingAllTime(groupForm.isTrackingAllTime());
	    group.setTrackingDuringShiftOnly(groupForm.isTrackingDuringShiftOnly());
	    group.setTrackingInterval(groupForm.getTrackingInterval());
	    group.setWorkShift(groupForm.getWorkShift());
	    group.setAttendanceInWorkLocationOnly(groupForm.isAttendanceInWorkLocationOnly());
	    group.setAutoLocationAttendance(groupForm.isAutoLocationAttendance());
	    group.setUseUserLocation(groupForm.isUseUserLocation());
	    group.setUseWifi(groupForm.isUseWifi());
	    
	    //update workLocation
	    
	    	//remove deleted location
	    Long oldWorkLocationId = group.getWorkLocationId();
	    if(oldWorkLocationId != groupForm.getWorkLocationId() && oldWorkLocationId != 0/*no location*/){
	    	WorkLocation oldWorkLocation = ofy().load()
	    			.key(WorkLocation.getKey(companyKey, oldWorkLocationId)).now();
	    	oldWorkLocation.removeGroupId(group.getId());
	    	ofy().save().entity(oldWorkLocation).now();
	    }
	    
	    	//add added location
	    if(groupForm.getWorkLocationId() != 0/*no location*/){
	    	WorkLocation newWorkLocation = ofy().load()
	    			.key(WorkLocation.getKey(companyKey, groupForm.getWorkLocationId())).now();
	    	newWorkLocation.addGroupId(group.getId());
	    	ofy().save().entity(newWorkLocation).now();
	    }
	    group.setWorkLocationId(groupForm.getWorkLocationId());
	    
	    //update managers
	    
	    	//remove deleted managers
	    for(String id : group.getManagers()){
	    	if(!groupForm.getManagers().contains(id)){
	    		Employee manager = Employee.getEmployeeById(id, companyKey);
	    		if(manager != null){
	    			manager.removeManagedGroup(group.getId());
	    			ofy().save().entity(manager).now();
	    		}
	    		
	    	}
	    }
	    	//add added managers
	    for(String id : groupForm.getManagers()){
	    	if(!group.getManagers().contains(id)){
	    		Employee manager = Employee.getEmployeeById(id, companyKey);
	    		if(manager != null){
	    			manager.addManagedGroup(group.getId());
	    			ofy().save().entity(manager).now();
	    		}
	    		
	    	}
	    }
	    group.setManagers(groupForm.getManagers());
	    
	    
	    //update members
	    List<Employee> members = ofy().load()
				.type(Employee.class)
				.ancestor(companyKey)
				.filter("groups =", group.getId())
				.list();
	    
	    	//remove deleted members
	    for(Employee member : members){
	    	if(!groupForm.getMembers().contains(member.getId())){
	    		member.removeGroup(group.getId());
	    		ofy().save().entity(member).now();
	    		
	    	}
	    }
	    	//add added members
	    for(String newMember : groupForm.getMembers()){
	    	boolean realyNew = true;
	    	for(Employee member : members){
	    		if(member.getId().equals(newMember)){
	    			realyNew = false;
	    			break;
	    		}
	    	}
	    	
	    	if(realyNew){
	    		Employee memberE = Employee.getEmployeeById(newMember, companyKey);
		    	memberE.addGroup(group.getId());
		    	ofy().save().entity(memberE).now();
	    	}
	    	
	    }
	    
	    //finally save group
	    ofy().save().entity(group).now();
	    
	    return group;
		
	}


	public static void deleteGroup(Key<Group> groupKey) {
		if(groupKey == null)
			return;
		
		Group group = ofy().load().key(groupKey).now();
		
		if(group == null)
			return;
		
		GroupForm groupForm = new GroupForm();
		
		groupForm.setManagers(new HashSet<String>());
		groupForm.setMembers(new HashSet<String>());
		groupForm.setWorkLocationId(0);//no location
		
		groupForm.setId(group.getId());
		
		
		Group updatedGroup = Group.updateGroup(groupForm , group.getCompanyKey());
		if(updatedGroup != null)
			ofy().delete().key(groupKey).now();
		
	}

	public static List<Key<Employee>> getMembersKeys(Key<Company> companyKey, Set<Long> groupIds){
		
		List<Key<Employee>> empKeys = new ArrayList<Key<Employee>>();
		
		if(!groupIds.isEmpty()){
			empKeys = ofy().load()
					.type(Employee.class)
					.ancestor(companyKey)
					.filter("groups IN", groupIds)
					.keys()
					.list();
		}
		return empKeys;
		
	}
	
	public static List<String> getMembersIds(Key<Company> companyKey, Set<Long> groupIds){
		
		List<Key<Employee>> empKeys = Group.getMembersKeys(companyKey, groupIds);
		
		List<String> empIds = new ArrayList<String>();
		for (Key<Employee> key : empKeys) {
			empIds.add(key.getName());
		}
		return empIds;
		
	}
	
	public static List<Employee> getMembersEntitys(Key<Company> companyKey, Set<Long> groupIds){

		List<Employee> emps = new ArrayList<Employee>();
		
		if(!groupIds.isEmpty()){
			emps = ofy().load()
					.type(Employee.class)
					.ancestor(companyKey)
					.filter("groups IN", groupIds)
					.list();
		}
		return emps;
		
	}
	
	public long getWorkLocationId() {
		return workLocationId;
	}

	public void setWorkLocationId(long workLocationId) {
		this.workLocationId = workLocationId;
	}

	public WorkShift getWorkShift() {
		if(workShift == null){
			WorkShift shift = new WorkShift();
			
			shift.setStartTime(9*60);
			shift.setEndTime(17*60);
			shift.setTimeZone(2);
			return shift;
		}
			
		return workShift;
	}

	public void setWorkShift(WorkShift workShift) {
		this.workShift = workShift;
	}

	public boolean isNotificationsEnabled() {
		return notificationsEnabled;
	}

	public void setNotificationsEnabled(boolean notificationsEnabled) {
		this.notificationsEnabled = notificationsEnabled;
	}

	public boolean isTrackingDuringShiftOnly() {
		return trackingDuringShiftOnly;
	}

	public void setTrackingDuringShiftOnly(boolean trackingDuringShiftOnly) {
		this.trackingDuringShiftOnly = trackingDuringShiftOnly;
	}

	public boolean isTrackingAllTime() {
		return trackingAllTime;
	}

	public void setTrackingAllTime(boolean trackingAllTime) {
		this.trackingAllTime = trackingAllTime;
	}

	public boolean isAttendanceInWorkLocationOnly() {
		return attendanceInWorkLocationOnly;
	}

	public void setAttendanceInWorkLocationOnly(boolean attendanceInWorkLocationOnly) {
		this.attendanceInWorkLocationOnly = attendanceInWorkLocationOnly;
	}

	public boolean isAutoLocationAttendance() {
		return autoLocationAttendance;
	}

	public void setAutoLocationAttendance(boolean autoLocationAttendance) {
		this.autoLocationAttendance = autoLocationAttendance;
	}

	public boolean isUseUserLocation() {
		return useUserLocation;
	}

	public void setUseUserLocation(boolean useUserLocation) {
		this.useUserLocation = useUserLocation;
	}

	public boolean isUseWifi() {
		return useWifi;
	}

	public void setUseWifi(boolean useWifi) {
		this.useWifi = useWifi;
	}

	

	
}
