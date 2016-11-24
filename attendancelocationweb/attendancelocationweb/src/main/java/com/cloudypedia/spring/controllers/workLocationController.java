package com.cloudypedia.spring.controllers;

import static com.cloudypedia.service.OfyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cloudypedia.Constants;
import com.cloudypedia.auth.SessionLoggedUser;
import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Role;
import com.cloudypedia.domain.WorkLocation;
import com.cloudypedia.form.WorkLocationForm;
import com.googlecode.objectify.Key;

@Controller
@RequestMapping(path="/workLocation")
public class WorkLocationController {
	
	private String TEMPLATE_PREFIX = "workLocation/";
   
    @RequestMapping(path = {"/workLocationList"}, method = RequestMethod.GET)
    public ModelAndView handleListView(ModelAndView model, HttpSession session) {
      //check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		List<WorkLocation> workLocationList = new ArrayList<WorkLocation>();
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			if(role.equals(Role.Admin) || role.equals(Role.Manager)){
				Key<Company> companyKey = loggedUser.getCompanyKey();
				workLocationList = ofy().load().type(WorkLocation.class).ancestor(companyKey).list();
			}else{
				thereIsError = true;
				errorName = "Athorization Error";
				errorMessage = "you don't have the access rights";
			}
		}
		
	if (thereIsError) {
		model.setViewName("exception");
		model.addObject("name", errorName);
		model.addObject("message", errorMessage);
	} else {
		String viewName = "work_location_list";
		model.setViewName(getViewName(viewName));
		model.addObject("activeTab", viewName);
		model.addObject("workLocations", workLocationList);
	}
	
	return model;
    }
    
    @RequestMapping(path = { "/create" }, method = RequestMethod.GET)
	public ModelAndView handleCreateWorkLocation(ModelAndView model, HttpSession session) {
		
		//check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		WorkLocation workLocation = null;
		
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			
			
			if(role.equals(Role.Admin)){
				Key<Company> companyKey = loggedUser.getCompanyKey();
				
				workLocation = WorkLocation.createDefaulteWorkLocation(companyKey);
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
			
			model.setViewName("redirect:/a/workLocation/edit/" + workLocation.getId());
		}
		
		
		return model;
	}
    
    @RequestMapping(path = "/save", consumes = "application/json", method = RequestMethod.POST)
	@ResponseBody
	public Map<Object, Object> saveWorkLocation(HttpSession session, @RequestBody WorkLocationForm workLocationForm, BindingResult result) {
		Map<Object, Object> res = new HashMap<Object, Object>();
		Map<Object, Object> error = new HashMap<Object, Object>();
		if (result.hasErrors()) {
			error.put("message", "binding Error");
			res.put("error", error);
			return res;
		}
		
		// check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);

		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;

		if (loggedUser == null) {
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		} else {

			Role role = loggedUser.getRole();

			if (role.equals(Role.Admin)) {

				WorkLocation workLocation = WorkLocation.updateWorkLocation(workLocationForm, loggedUser.getCompanyKey());
				if(workLocation == null){
					thereIsError = true;
					errorName = "Work Location Error";
					errorMessage = "Work Location is not found";
				}
			} else {
				thereIsError = true;
				errorName = "Athorization Error";
				errorMessage = "you don't have the access rights";
			}
		}

		if (thereIsError) {
			error.put("message", errorName + "\n" + errorMessage);
			res.put("error", error);
		} else {

			res.put("success", true);
		}
		return res;
	}
    
	@RequestMapping(path = { "/edit/{workLocationId}" }, method = RequestMethod.GET)
 ModelAndView handleEditWorkLocation(ModelAndView model, HttpSession session, @PathVariable Long workLocationId) {
		
		//check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		WorkLocation workLocation = null;
		
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			
			
			if(role.equals(Role.Admin)){
				Key<Company> companyKey = loggedUser.getCompanyKey();
				
				Key<WorkLocation> workLocationKey = WorkLocation.getKey(companyKey, workLocationId);
				workLocation = ofy().load().key(workLocationKey).now();
				
				if(workLocation == null){
					thereIsError = true;
					errorName = "Edit Work Location";
					errorMessage = "This Work Location is not found";
				}
				
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
			Map<Object, Object> workLocationPage = new HashMap<Object, Object>();

			workLocationPage.put("workLocation", workLocation);
			String viewName = "work_location_edit";
			model.setViewName(getViewName(viewName));
			model.addObject("workLocationPage", workLocationPage);
			model.addObject("activeTab", "work_location_list");
			model.addObject("apiKey", Constants.API_KEY);
		}
		
		
		return model;
	}
	
	@RequestMapping(path = { "/delete/{workLocatoinId}" }, method = RequestMethod.GET)
	public ModelAndView handleDeleteWorkLocation(ModelAndView model, HttpSession session, @PathVariable Long workLocatoinId) {
		
		//check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		WorkLocation workLocation = null;
		
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			
			
			if(role.equals(Role.Admin)){
				Key<Company> companyKey = loggedUser.getCompanyKey();
				
				Key<WorkLocation> workLocationKey = WorkLocation.getKey(companyKey, workLocatoinId);
				workLocation = ofy().load().key(workLocationKey).now();
				
				if(workLocation == null){
					thereIsError = true;
					errorName = "Edit Work Location";
					errorMessage = "This work location is not found";
				}else{
					WorkLocation.deleteWorkLocation(workLocationKey);
				}
				
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
			
			model.setViewName("redirect:/a/workLocation/workLocationList");
			
		}
		
		
		return model;
	}
    
	private String getViewName(String view){
		 return TEMPLATE_PREFIX + view;
	 }

}
