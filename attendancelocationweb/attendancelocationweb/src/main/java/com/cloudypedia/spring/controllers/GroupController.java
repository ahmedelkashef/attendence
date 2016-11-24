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

import com.cloudypedia.auth.SessionLoggedUser;
import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Employee;
import com.cloudypedia.domain.Group;
import com.cloudypedia.domain.Role;
import com.cloudypedia.domain.WorkLocation;
import com.cloudypedia.form.GroupForm;
import com.googlecode.objectify.Key;

@Controller
@RequestMapping(path="/group")
public class GroupController {
	
	private String TEMPLATE_PREFIX = "group/";
	
	@RequestMapping(path = { "/groupList" }, method = RequestMethod.GET)
	public ModelAndView handleGroupList(ModelAndView model, HttpSession session) {
		
		//check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		List<Group> groupList = new ArrayList<Group>();
		
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			
			
			if(role.equals(Role.Admin)){
				Key<Company> companyKey = loggedUser.getCompanyKey();
				groupList = ofy().load().type(Group.class).ancestor(companyKey).list();
				
			}else if(role.equals(Role.Manager)){
				
				Employee manager = ofy().load().key(loggedUser.getUserKey()).now();
				
				
				groupList = Group.getGroupsEntities(manager.getCompanyKey(), manager.getManagedGroups());
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
			String viewName = "group_list";
			model.setViewName(getViewName(viewName));
			model.addObject("groups", groupList);
			model.addObject("activeTab", viewName);
		}
		
		
		return model;
	}
	
	
	@RequestMapping(path = { "/edit/{groupId}" }, method = RequestMethod.GET)
	public ModelAndView handleEditGroup(ModelAndView model, HttpSession session, @PathVariable Long groupId) {
		
		//check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		Group group = null;
		
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			
			
			if(role.equals(Role.Admin) || role.equals(Role.Manager)){
				Key<Company> companyKey = loggedUser.getCompanyKey();
				
				Key<Group> groupKey = Group.getKey(companyKey, groupId);
				group = ofy().load().key(groupKey).now();
				
				if(group == null){
					thereIsError = true;
					errorName = "Edit Group";
					errorMessage = "This group is not found";
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
			Map<Object, Object> groupPage = new HashMap<Object, Object>();
			List<Employee> employees = ofy().load()
					.type(Employee.class)
					.ancestor(loggedUser.getCompanyKey())
					.list();
			
			List<Employee> members = ofy().load()
					.type(Employee.class)
					.ancestor(loggedUser.getCompanyKey())
					.filter("groups =", group.getId())
					.list();
			
			List<Employee> managers = 
					Employee.getEmployeeEntities(loggedUser.getCompanyKey(), group.getManagers());
			
			List<WorkLocation> workLocationList = 
					ofy().load().type(WorkLocation.class).ancestor(loggedUser.getCompanyKey()).list();
			
			
			WorkLocation invalidWorkLocation = null;
			invalidWorkLocation = new WorkLocation();
			invalidWorkLocation.setId((long) 0);
			invalidWorkLocation.setName("No Location");
		
			
			List<WorkLocation> workLocationListTemp = new ArrayList<WorkLocation>();
			workLocationListTemp.add(invalidWorkLocation);
			workLocationListTemp.addAll(workLocationList);
			workLocationList = workLocationListTemp;
			
			groupPage.put("employees", employees);
			groupPage.put("members", members);
			groupPage.put("managers", managers);
			groupPage.put("groupSettings", group);
			groupPage.put("workLocations", workLocationList);
			String viewName = "group_edit";
			model.setViewName(getViewName(viewName));
			model.addObject("groupPage", groupPage);
			model.addObject("activeTab", "group_list");
		}
		
		
		return model;
	}
	
	
	@RequestMapping(path = { "/create" }, method = RequestMethod.GET)
	public ModelAndView handleCreateGroup(ModelAndView model, HttpSession session) {
		
		//check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		Group group = null;
		
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			
			
			if(role.equals(Role.Admin)){
				Key<Company> companyKey = loggedUser.getCompanyKey();
				
				group = Group.createDefaulteGroup(companyKey);
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
			
			model.setViewName("redirect:/a/group/edit/" + group.getId());
		}
		
		
		return model;
	}
	
	@RequestMapping(path = "/save", consumes = "application/json", method = RequestMethod.POST)
	@ResponseBody
	public Map<Object, Object> saveGroup(HttpSession session, @RequestBody GroupForm groupForm, BindingResult result) {
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

				Group group = Group.updateGroup(groupForm, loggedUser.getCompanyKey());
				if(group == null){
					thereIsError = true;
					errorName = "Group Error";
					errorMessage = "Group is not found";
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
	
	@RequestMapping(path = { "/delete/{groupId}" }, method = RequestMethod.GET)
	public ModelAndView handleDeleteGroup(ModelAndView model, HttpSession session, @PathVariable Long groupId) {
		
		//check access rights
		SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
		
		boolean thereIsError = false;
		String errorName = null;
		String errorMessage = null;
		Group group = null;
		
		
		if(loggedUser == null){
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		}
		else{
			
			Role role = loggedUser.getRole();
			
			
			
			if(role.equals(Role.Admin)){
				Key<Company> companyKey = loggedUser.getCompanyKey();
				
				Key<Group> groupKey = Group.getKey(companyKey, groupId);
				group = ofy().load().key(groupKey).now();
				
				if(group == null){
					thereIsError = true;
					errorName = "Edit Group";
					errorMessage = "This group is not found";
				}else{
					Group.deleteGroup(groupKey);
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
			
			model.setViewName("redirect:/a/group/groupList");
			
		}
		
		
		return model;
	}
	 
	
	 private String getViewName(String view){
		 return TEMPLATE_PREFIX + view;
	 }
	

}
