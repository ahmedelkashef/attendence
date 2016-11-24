package com.cloudypedia.spring.controllers;

import static com.cloudypedia.service.OfyService.ofy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudypedia.auth.SessionLoggedUser;
import com.cloudypedia.auth.VerifyResults;
import com.cloudypedia.auth.VerifyUser;
import com.cloudypedia.domain.AttendanceLog;
import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Employee;
import com.cloudypedia.domain.Group;
import com.cloudypedia.domain.TrackingLog;
import com.cloudypedia.domain.WorkLocation;
import com.cloudypedia.form.AttendanceLogForm;
import com.cloudypedia.form.TrackingForm;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * 
 * This class is mobile controller to handle mobile request
 *
 */
@RestController
@RequestMapping(path="/m")
public class MobileApiController {
	public static final Logger LOG = Logger.getLogger(MobileApiController.class.getName());
	/**
	 * 
	 * @param idToken
	 * 		id token of google account
	 * @return
	 * 		json object of group settings that the user belong to.
	 */
	@RequestMapping(path ="/getUserSetting", method=RequestMethod.POST)
	Map<Object, Object> getUserSetting(
			@RequestParam(required=false,name="idToken") String idToken
			,@RequestParam(required=false,name="userIdToken") String userIdToken){
		Map<Object, Object> res = new HashMap<>();
		Map<Object, Object> error = new HashMap<>();
	
		Employee employee = verifyUser(userIdToken, idToken);
		if(employee == null){
			res.put("error", error);
			error.put("message", "User or Domain not registered");
			return res;
		}
		Company company = ofy().load().key(employee.getCompanyKey()).now();
		
		Group group = null;
		if(employee.getGroups() != null && !employee.getGroups().isEmpty())
			group = ofy().load().key(Group.getKey(employee.getCompanyKey(), employee.getGroups().iterator().next())).now();
		
		WorkLocation workLocation = null;
		
		if(group.getWorkLocationId() != 0){
			workLocation = ofy().load()
					.key(WorkLocation.getKey(employee.getCompanyKey(), group.getWorkLocationId()))
					.now();
		}
	
		res.put("settings", group);
		
		if(workLocation != null)
			res.put("workLocation", workLocation);
		
		//generate new userIdToken
		employee.updateUserIdToken();
		res.put("userIdToken", employee.getUserIdToken());
		
		if(company.getLogo() != null)
			res.put("logoUrl", company.getLogoUrl());
		else
			res.put("logoUrl", "");
		
		return res;
		
	}
	
	
	
	/**
	 * 
	 * @param trackingForm
	 * 		contains information about tracked user
	 * @return
	 * 		JSON object with the status fails or success
	 */
	@RequestMapping(path = {"/upload/tracking"}, consumes = "application/json", method = RequestMethod.POST)
	public Map<Object, Object> handleUploadTracking(@RequestBody TrackingForm trackingForm) {
		Map<Object, Object> res = new HashMap<>();
		Map<Object, Object> error = new HashMap<>();
		
		Employee employee = verifyUser(trackingForm.getUserIdToken(), trackingForm.getIdToken());
		
		if(employee == null){
			res.put("error", error);
			error.put("message", "User or Domain not registered");
			return res;
		}
		
		
		
		trackingForm.setEmail(employee.getEmail());
		trackingForm.setUserId(employee.getId());
		TrackingLog.createTracking(trackingForm, employee.getCompanyKey());
		
		res.put("success", true);
		return res;
	}
	
	/**
	 * Handle uploaded blob for checkin image and other parameters
	 * @param request
	 * 		contains blob key
	 * @param attendanceLogForm
	 * 		contains attendance info
	 * @return
	 */
	
	/**
	 * 
	 * @param idToken
	 * 		id token of google account
	 * @return
	 * 	JSON contain blob upload url
	 */
	@RequestMapping(path = {"/upload/createUploadUrl"}, method = RequestMethod.POST)
	public Map<Object, Object> handleCreateUplocadUrl(
			@RequestParam(required=false,name="idToken") String idToken
			,@RequestParam(required=false,name="userIdToken") String userIdToken){
		Map<Object, Object> res = new HashMap<>();
		Map<Object, Object> error = new HashMap<>();
		
		Employee employee = verifyUser(userIdToken, idToken);
		
		if(employee == null){
			res.put("error", error);
			error.put("message", "User or Domain not registered");
			return res;
		}
		
		String uploadUrl = BlobstoreServiceFactory.getBlobstoreService().createUploadUrl("/a/m/upload/attendanceWithBlobImage");
		res.put("uploadUrl", uploadUrl);
		return res;
	}
	
//	@RequestMapping(path = { "/upload/attendance" }, method = RequestMethod.POST)
//	public void redirect(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String uploadUrl = BlobstoreServiceFactory.getBlobstoreService()
//				.createUploadUrl("/a/m/upload/attendanceWithBlobImage");
//		
//		URL url = new URL(uploadUrl);
//		//System.out.println("uploadUrlPAth" + url.getPath());
//		request.getRequestDispatcher(url.getPath()).forward(request, response);
//
//	}

	@RequestMapping(path = { "/upload/attendanceWithBlobImage" }, method = RequestMethod.POST)
	public Map<Object, Object> UploadLocation(HttpServletRequest request,
			@ModelAttribute AttendanceLogForm attendanceLogForm) {
		Map<Object, Object> res = new HashMap<>();
		Map<Object, Object> error = new HashMap<>();
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		LOG.warning("In /upload/attendanceWithBlobImage");

		Employee employee = verifyUser(attendanceLogForm.getUserIdToken(), attendanceLogForm.getIdToken());
		
		if (employee == null) {
			res.put("error", error);
			error.put("message", "User or Domain not registered");
			return res;
		}
		

		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);

		List<BlobKey> blobKeys = blobs.get("image");

		Map<String, List<BlobInfo>> blobInfos = blobstoreService.getBlobInfos(request);
		List<BlobInfo> infos = blobInfos.get("image");
		String imagekeyStr = null;
		if (blobKeys != null && !blobKeys.isEmpty()
				&& infos.get(0).getContentType().toLowerCase().matches("image/(jpg|jpeg|png)")) {
			imagekeyStr = blobKeys.get(0).getKeyString();
		}
		attendanceLogForm.setImagekeyStr(imagekeyStr);
		attendanceLogForm.setEmail(employee.getEmail());
		attendanceLogForm.setUserId(employee.getId());
		AttendanceLog.createAttendanceLog(attendanceLogForm, employee.getCompanyKey());

		res.put("success", true);

		return res;
	}
	
	private Employee verifyUser(String userIdToken, String idToken){
		//try to find employee by the App generated token
		Employee employee = Employee.getEmployeeByUserIdToken(userIdToken);
		
		//try to find employee by the google id token generated token
		if(employee == null && idToken != null){
			VerifyResults verifyResults = VerifyUser.verifyToken(idToken);
			if(!verifyResults.isValid()){
				return null;
			}
			
			SessionLoggedUser loggedUser = VerifyUser.checkRole(verifyResults);
			employee = null;
			
			if(loggedUser.getUserKey() != null){
				employee = ofy().load().key(loggedUser.getUserKey()).now();
			}
		}
		
		return employee;
	}
	
}
