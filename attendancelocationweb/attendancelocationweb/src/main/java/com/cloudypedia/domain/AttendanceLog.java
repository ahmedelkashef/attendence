package com.cloudypedia.domain;

import static com.cloudypedia.service.OfyService.factory;
import static com.cloudypedia.service.OfyService.ofy;

import java.util.Calendar;
import java.util.Date;

import com.cloudypedia.form.AttendanceLogForm;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import util.Utility;

/**
 * 
 * This class represent Attendance log like check in/out
 *
 */
@Entity
@Cache
public class AttendanceLog extends BaseKindCompany{
	
	public static String STATUS_IN = "IN";
	public static String STATUS_OUT = "OUT";

	@Id
	private Long id;
	
	private String user;

	/**
	 * location latitude
	 */
	private double latitude;
	/**
	 * location longitude
	 */
	private double longitude;

	/**
	 * status like IN/OUT
	 */
	@Index
	private String status;

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
	 * Attendance Date
	 */
	@Index
	private Date date;
	
	@Index
	private int dateDay;

	/**
	 * blob key of taken image
	 */
	private BlobKey image;
	
	/**
	 * location providers in mobile
	 */
	private String providers;

	public AttendanceLog() {
	}

	/**
	 * 
	 * @param attendanceLogForm
	 * @param companyKey
	 * 			company domain (needed to set parent Key)
	 * @return attendanceLog created 
	 */
	public static AttendanceLog createAttendanceLog(AttendanceLogForm attendanceLogForm, Key<Company> companyKey) {
		final Key<AttendanceLog> attendanceLogKey = factory().allocateId(companyKey, AttendanceLog.class);
		final long attendanceLogId = attendanceLogKey.getId();
		
		AttendanceLog attendanceLog = new AttendanceLog();
		
		attendanceLog.setId(attendanceLogId);
		attendanceLog.setCompanyKey(companyKey);
		
		Calendar calendar = Calendar.getInstance();
		if(attendanceLogForm.getDate() != null)
			calendar.setTimeInMillis(Long.valueOf(attendanceLogForm.getDate()));
		attendanceLog.setDate(calendar.getTime());
		attendanceLog.setEmail(attendanceLogForm.getEmail());
		attendanceLog.setUserId(attendanceLogForm.getUserId());
		attendanceLog.setLatitude(attendanceLogForm.getLatitude());
		attendanceLog.setLongitude(attendanceLogForm.getLongitude());
		attendanceLog.setStatus(attendanceLogForm.getStatus());
		attendanceLog.setUser(attendanceLogForm.getUser());
		attendanceLog.setProviders(attendanceLogForm.getProviders());
		
		if(attendanceLogForm.getImagekeyStr() != null && !attendanceLogForm.getImagekeyStr().isEmpty()){
			BlobKey imageKey = new BlobKey(attendanceLogForm.getImagekeyStr());
			attendanceLog.setImage(imageKey);
		}
		
		
		ofy().save().entity(attendanceLog).now();
		return attendanceLog;

	}
	
	public static AttendanceLog getFirstIn(Key<Company> companyKey, String userId,int dateDay){
		
		return ofy().load()
				.type(AttendanceLog.class)
				.ancestor(companyKey)
				.filter("userId =", userId)
				.filter("dateDay =", dateDay)
				.filter("status =", AttendanceLog.STATUS_IN)
				.order("date")
				.first().now();
	}
	
	public static AttendanceLog getLastIn(Key<Company> companyKey, String userId,int dateDay){
		
		return ofy().load()
				.type(AttendanceLog.class)
				.ancestor(companyKey)
				.filter("userId =", userId)
				.filter("dateDay =", dateDay)
				.filter("status =", AttendanceLog.STATUS_IN)
				.order("-date")
				.first().now();
	}
	
	public static AttendanceLog getLastOut(Key<Company> companyKey, String userId,int dateDay){
		
		return ofy().load()
				.type(AttendanceLog.class)
				.ancestor(companyKey)
				.filter("userId =", userId)
				.filter("dateDay =", dateDay)
				.filter("status =", AttendanceLog.STATUS_OUT)
				.order("-date")
				.first().now();
	}
	

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
		this.setDateDay(Utility.getDateDay(date.getTime()));
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BlobKey getImage() {
		return image;
	}

	public void setImage(BlobKey image) {
		this.image = image;
	}

	public String getImageUrl() {
		ImagesService imagesService = ImagesServiceFactory.getImagesService();

		if (this.getImage() == null)
			return null;

		ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(this.getImage());
		options.secureUrl(true);
		return imagesService.getServingUrl(options);
	}

	public String getProviders() {
		return providers;
	}

	public void setProviders(String providers) {
		this.providers = providers;
	}

	public int getDateDay() {
		return dateDay;
	}

	public void setDateDay(int dateDay) {
		this.dateDay = dateDay;
	}

	

}
