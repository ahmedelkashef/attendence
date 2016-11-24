package com.cloudypedia.domain;

import static com.cloudypedia.service.OfyService.factory;
import static com.cloudypedia.service.OfyService.ofy;

import java.util.Calendar;
import java.util.Date;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
/**
 * This class represent location kind 
 *
 */
@Entity
@Cache
@Deprecated
public class Location {
	@Id
	private Long id;
	
	private String name;
	private double latitude;
	private double longitude;
	@Index
	private String status;
	@Index
	private String email;
	private String imageID;
	@Index
	private Date date;
	
	private BlobKey image;
	
	public Location(){}
	
	public Location(Long id, double latitude, double longitude, String name, Date date, String status) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		this.date =  calendar.getTime();
		this.status = status;
	}


	public static Location createLocation(Date date,double latitude, double longitude, String name, String status) {
		final Key<Location> locationKey = factory().allocateId(Location.class);
	    final long locationId = locationKey.getId();
	    Location location = new Location(locationId, latitude,longitude,name,date, status);
	    //Save Conference and Profile.
	    ofy().save().entity(location).now();
		return location;

	}
	
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
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
	 * @param latitude the latitude to set
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
	 * @param longitude the longitude to set
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
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return name;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.name = user;
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


	public String getImageID() {
		return imageID;
	}


	public void setImageID(String imageID) {
		this.imageID = imageID;
	}

	public static Location createLocation(Location location) {
		final Key<Location> locationKey = factory().allocateId(Location.class);
	    final long locationId = locationKey.getId();
	    location.setId(locationId);
	    ofy().save().entity(location).now();
		return location;
		
	}

	public BlobKey getImage() {
		return image;
	}

	public void setImage(BlobKey image) {
		this.image = image;
	}
	
	public String getImageUrl() {
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		
		if(this.getImage() == null)
			return null;
		
		ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(this.getImage());
		options.secureUrl(true);
		return imagesService.getServingUrl(options);
	}

}
