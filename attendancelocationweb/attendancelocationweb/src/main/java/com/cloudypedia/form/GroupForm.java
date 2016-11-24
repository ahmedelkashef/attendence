package com.cloudypedia.form;

import java.util.HashSet;

import com.cloudypedia.domain.WorkShift;

public class GroupForm {
	
	public GroupForm(){}
	
	private long id;
	
	private HashSet<String> managers;
	private HashSet<String> members;
	
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
	private long workLocationId;
	private boolean useUserLocation;
	private boolean useWifi;
	private WorkShift workShift;
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the managers
	 */
	public HashSet<String> getManagers() {
		return managers;
	}
	/**
	 * @param managers the managers to set
	 */
	public void setManagers(HashSet<String> managers) {
		this.managers = managers;
	}
	/**
	 * @return the members
	 */
	public HashSet<String> getMembers() {
		return members;
	}
	/**
	 * @param members the members to set
	 */
	public void setMembers(HashSet<String> members) {
		this.members = members;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the trackingEnabled
	 */
	public boolean isTrackingEnabled() {
		return trackingEnabled;
	}
	/**
	 * @param trackingEnabled the trackingEnabled to set
	 */
	public void setTrackingEnabled(boolean trackingEnabled) {
		this.trackingEnabled = trackingEnabled;
	}
	/**
	 * @return the trackingInterval
	 */
	public long getTrackingInterval() {
		return trackingInterval;
	}
	/**
	 * @param trackingInterval the trackingInterval to set
	 */
	public void setTrackingInterval(long trackingInterval) {
		this.trackingInterval = trackingInterval;
	}
	/**
	 * @return the needImage
	 */
	public boolean isNeedImage() {
		return needImage;
	}
	/**
	 * @param needImage the needImage to set
	 */
	public void setNeedImage(boolean needImage) {
		this.needImage = needImage;
	}
	/**
	 * @return the attendanceEnabled
	 */
	public boolean isAttendanceEnabled() {
		return attendanceEnabled;
	}
	/**
	 * @param attendanceEnabled the attendanceEnabled to set
	 */
	public void setAttendanceEnabled(boolean attendanceEnabled) {
		this.attendanceEnabled = attendanceEnabled;
	}
	public long getWorkLocationId() {
		return workLocationId;
	}
	public void setWorkLocationId(long workLocationId) {
		this.workLocationId = workLocationId;
	}
	public WorkShift getWorkShift() {
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
	public boolean isTrackingAllTime() {
		return trackingAllTime;
	}
	public void setTrackingAllTime(boolean trackingAllTime) {
		this.trackingAllTime = trackingAllTime;
	}
	public boolean isTrackingDuringShiftOnly() {
		return trackingDuringShiftOnly;
	}
	public void setTrackingDuringShiftOnly(boolean trackingDuringShiftOnly) {
		this.trackingDuringShiftOnly = trackingDuringShiftOnly;
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
