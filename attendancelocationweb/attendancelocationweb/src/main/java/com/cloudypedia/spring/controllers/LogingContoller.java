package com.cloudypedia.spring.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.cloudypedia.Constants;
import com.cloudypedia.auth.SessionLoggedUser;
import com.cloudypedia.auth.VerifyResults;
import com.cloudypedia.auth.VerifyUser;
import com.cloudypedia.domain.Role;

@Controller
@RequestMapping(path={"/","/loging"})
public class LogingContoller {
	
	private String TEMPLATE_PREFIX = "loging/";
	  public static final String DEFAULT_PATH = "/a/viewData/attendanceMapView";
	  public static final String ADMIN_DEFAULT_PATH = DEFAULT_PATH;
	  public static final String MANAGER_DEFAULT_PATH = DEFAULT_PATH;
	  public static final String OWNER_DEFAULT_PATH = "/a/company/companyList";
	
	@RequestMapping(path = { "/","/loginPage" }, method = RequestMethod.GET)
	public ModelAndView login(ModelAndView model) {
		String viewName = "login";
		model.setViewName(getViewName(viewName));
		model.addObject("clientId", Constants.WEB_CLIENT_ID);
		model.addObject("activeTab", viewName);
		
		return model;
	}
	
	@RequestMapping(path = { "/login" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<Object, Object> checkIdToken(ModelAndView model, HttpSession session,
			@RequestParam String idToken) {
		Map<Object, Object> res = new HashMap<Object, Object>();
		
		VerifyResults verifyResults = VerifyUser.verifyToken(idToken);
		if(!verifyResults.isValid()){
			HashMap<Object, Object> error = new HashMap<Object, Object>();
			res.put("error", error);
			error.put("message", "Authorization Error(id token is not valid)");
		}else{
			SessionLoggedUser loggedUser = VerifyUser.checkRole(verifyResults);
			Role role = loggedUser.getRole();
			
			if(role.equals(Role.Invalid)){
				HashMap<Object, Object> error = new HashMap<Object, Object>();
				res.put("error", error);
				error.put("message", "User is not registered");
				session.setAttribute(SessionLoggedUser.SESSION_KEY, loggedUser);//TODO remove this line
			}else{
				HashMap<Object, Object> result = new HashMap<Object, Object>();
				
				
				session.setAttribute(SessionLoggedUser.SESSION_KEY, loggedUser);
				
				String nextPath = null;
				switch (role) {
				case Admin:
					nextPath = ADMIN_DEFAULT_PATH;
					break;
				case Manager:
					nextPath = MANAGER_DEFAULT_PATH;
					break;
				case Owner:
					nextPath = OWNER_DEFAULT_PATH;
					break;
				case Employee:
					break;
				case Invalid:
					break;
				

				}
				
				if(nextPath == null){
					HashMap<Object, Object> error = new HashMap<Object, Object>();
					res.put("error", error);
					error.put("message", "You do not have access rights");
				}else{
					result.put("path", nextPath);
					res.put("result", result);
				}
				
			}
			
			
		}
		
		//res.put("idToken", idToken);
		
		return res;
	}
	
	@RequestMapping(path="/logout", method = RequestMethod.POST)
	@ResponseBody
	public String logout(SessionStatus status, HttpSession session){
		session.invalidate();
		//status.setComplete();
	  
	  return "session Terminated";
	}
	
	private String getViewName(String view){
		 return TEMPLATE_PREFIX + view;
	 }
	
	
	@RequestMapping(path = { "/testlogin" }, method = RequestMethod.GET)
	@ResponseBody
	public Map<Object, Object> testLogin(HttpSession session, SessionStatus status) {
		Map<Object, Object> res = new HashMap<Object, Object>();
		
		res.put("SessionLoggedUser", session.getAttribute(SessionLoggedUser.SESSION_KEY));
		res.put("SessionStatus Iscomplete", status.isComplete());
		
		
		
		return res;
	}

}
