package com.cloudypedia.spring.controllers;

import static com.cloudypedia.service.OfyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cloudypedia.auth.SessionLoggedUser;
import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Role;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@Controller
@RequestMapping(path="/company")
public class CompanyController {

	private String TEMPLATE_PREFIX = "company/";
	
	@RequestMapping(path = { "/companyList" }, method = RequestMethod.GET)
	public ModelAndView handleGroupList(ModelAndView model, HttpSession session) {
		
		//check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		List<Company> companyList = new ArrayList<Company>();
		
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			
			
			if(role.equals(Role.Owner)){
				companyList = ofy().load().type(Company.class).filter("domains !=", "").list();
				
			}else{
				thereIsError = true;
				errorName = "Athorization Error";
				errorMessage = "you don't have the access rights";
			}
		}
			
			if(thereIsError){
				model.setViewName("exception");
				model.addObject("name", errorName);
				model.addObject("message", errorMessage);
			}else{
				String viewName = "company_list";
				model.setViewName(getViewName(viewName));
				model.addObject("companies", companyList);
				model.addObject("activeTab", viewName);
			}
			
	
		
			return model;
		}
	
	@RequestMapping(path = {"/upload/createUploadUrl"}, method = RequestMethod.POST)
	@ResponseBody
	public Map<Object, Object> handleCreateUplocadUrl(HttpSession session) {
		Map<Object, Object> res = new HashMap<>();
		String uploadUrl = "";

		// check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.
				getAttribute(SessionLoggedUser.SESSION_KEY);

		if (loggedUser == null) {
		} else {

			Role role = loggedUser.getRole();

			if (role.equals(Role.Admin)) {
				uploadUrl = BlobstoreServiceFactory.getBlobstoreService()
						.createUploadUrl("/a/company/upload/logo");

			}
		}

		res.put("uploadUrl", uploadUrl);
		return res;
	}
	
	@RequestMapping(path = { "/upload/logo" }, method = RequestMethod.POST)
	public String UploadLocation(HttpServletRequest request, HttpSession session) {
		Map<Object, Object> res = new HashMap<>();
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

		// check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);

		res.put("success", false);
		if (loggedUser == null) {
		} else {

			Role role = loggedUser.getRole();

			if (role.equals(Role.Admin)) {

				Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);

				List<BlobKey> blobKeys = blobs.get("companyLogo");

				Map<String, List<BlobInfo>> blobInfos = blobstoreService.getBlobInfos(request);
				List<BlobInfo> infos = blobInfos.get("companyLogo");
				if (blobKeys != null && !blobKeys.isEmpty()
						&& infos.get(0).getContentType().toLowerCase().matches("image/(jpg|jpeg|png)")) {
					
					loggedUser.setLogoUrl(Company.getLogoUrl(blobKeys.get(0)));
					session.setAttribute(SessionLoggedUser.SESSION_KEY, loggedUser);
					
					Company company = ofy().load().key(loggedUser.getCompanyKey()).now();
					
					if(company.getLogo() != null)
						blobstoreService.delete(company.getLogo());
					company.setLogo(blobKeys.get(0));
					ofy().save().entity(company).now();
					
					res.put("success", true);
				}

			}
		}
		
		

		return "redirect:/a/viewData/attendanceMapView";
	}
	
	private String getViewName(String view){
		 return TEMPLATE_PREFIX + view;
	 }
	
}
