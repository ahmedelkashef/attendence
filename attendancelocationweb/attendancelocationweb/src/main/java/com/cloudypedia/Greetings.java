package com.cloudypedia;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.inject.Named;

import com.cloudypedia.domain.Location;
import com.cloudypedia.form.Message;
import com.cloudypedia.work.GSpreadSheet;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;

/**
 * Defines v1 of a helloworld API, which provides simple "greeting" methods.
 */
@Api(
    name = "helloworld",
    version = "v1"
)
public class Greetings {
	public static final Logger LOG = Logger.getLogger(Greetings.class.getName());

  public static ArrayList<HelloGreeting> greetings = new ArrayList<HelloGreeting>();

  static {
    greetings.add(new HelloGreeting("hello world!"));
    greetings.add(new HelloGreeting("goodbye world!"));
  }

  public HelloGreeting getGreeting(@Named("id") Integer id) throws NotFoundException {
    try {
      return greetings.get(id);
    } catch (IndexOutOfBoundsException e) {
      throw new NotFoundException("Greeting not found with an index: " + id);
    }
  }

  public ArrayList<HelloGreeting> listGreeting() {
    return greetings;
  }

  @ApiMethod(name = "greetings.multiply", httpMethod = "post")
  public HelloGreeting insertGreeting(@Named("times") Integer times, HelloGreeting greeting) {
    HelloGreeting response = new HelloGreeting();
    StringBuilder responseBuilder = new StringBuilder();
    for (int i = 0; i < times; i++) {
      responseBuilder.append(greeting.getMessage());
    }
    response.setMessage(responseBuilder.toString());
    return response;
  }

  @ApiMethod(name = "greetings.authed", path = "hellogreeting/authed")
  public HelloGreeting authedGreeting(User user) {
    HelloGreeting response = new HelloGreeting("hello " + user.getEmail());
    return response;
  }
  
  /**
	 * This API write data to google spreadsheet 
	 * 
	 * 
	 * @param latitude </br>
	 *            location latitude.
	 *@param longitude </br>
	 *				location longitude.
	 *@param name </br>
	 *				device name
	 * @return nothing.
	 * @since 2016-05-25
	 */
  @ApiMethod(name = "addLocation", path = "location/addLocation", httpMethod = "get")
  public void addLocation(final User user, @Named("latitude")double latitude, @Named("longitude")double longitude,
		  @Named("name") String name, @Nullable @Named("status") String status) {
	  
	  if(user != null){
		  LOG.warning("User: " + user.getEmail());
	  }
	  else
		  LOG.warning("User: " + user);
	  
	  Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
	GSpreadSheet.writeToSheet(calendar.getTime(), latitude, longitude, name, status);
    Location.createLocation(calendar.getTime(),latitude, longitude, name, status);
    
  }
  
  @ApiMethod(name = "testSheet", path = "location/sheet", httpMethod = "get")
  public void testSheet() {
    GSpreadSheet.writeToSheet();
    
  }
  
  @ApiMethod(name = "addLocationV2", path = "location/addLocationV2", httpMethod = "post")
  public Location addLocationV2(Location location) {
		
	GSpreadSheet.writeToSheet(location);
    Location.createLocation(location);
    
    return location;
    
  }
  
  @ApiMethod(name = "createUploadUrl", path = "upload/createUploadUrl", httpMethod = "post")
  public Message createUploadUrl() {
		
	String uploadUrl = BlobstoreServiceFactory.getBlobstoreService().createUploadUrl("/UploadLocation");
    
    return new Message(uploadUrl);
    
  }
  
  
}
