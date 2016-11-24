package com.cloudypedia.spring.controllers;

import static com.cloudypedia.service.OfyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cloudypedia.Constants;
import com.cloudypedia.auth.SessionLoggedUser;
import com.cloudypedia.domain.AttendanceLog;
import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Employee;
import com.cloudypedia.domain.Group;
import com.cloudypedia.domain.Role;
import com.cloudypedia.domain.TrackingLog;
import com.cloudypedia.domain.WorkShift;
import com.cloudypedia.form.AttendanceQueryByGroup;
import com.cloudypedia.form.FilterQuereyForm;
import com.cloudypedia.form.FilterQuereyForm.Filter;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy.OverQuotaException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import util.Utility;

@Controller
@RequestMapping(path="/viewData")
public class ViewDataController {
	private static final Logger LOG = Logger.getLogger(ViewDataController.class
			.getCanonicalName());
	
	private String TEMPLATE_PREFIX = "viewData/";

	

	/**
	 * just map requested path to (filter with map) template
	 * 
	 * @return template name
	 */
	@RequestMapping(path = { "/trackingHistoryView" }, method = RequestMethod.GET)
	public ModelAndView handleMapViewHistory(HttpSession session, ModelAndView model) {
		String viewName = "filter_with_map_history";
		model = checkAccessRights(model, session, viewName);
		return model;
	}
	
	/**
	 * just map requested path to (filter with map) template
	 * 
	 * @return template name
	 */
	@RequestMapping(path = { "/liveTrackingView" }, method = RequestMethod.GET)
	public ModelAndView handleLiveTrackingView(HttpSession session, ModelAndView model) {
		String viewName =  "live_tracking_map";
		model = checkAccessRights(model, session, viewName);
		return model;
	}
	
	/**
	 * just map requested path to (filter with map) template
	 * @return template name
	 */
	 @RequestMapping(path={"/attendanceMapView"} ,  method=RequestMethod.GET)
	    public ModelAndView handleMapView(HttpSession session, ModelAndView model) {
		 String viewName =  "filter_with_map";
		 model = checkAccessRights(model, session, viewName);
		return model;
	    }
	 
	/**
	 * just map requested path to (filter with list) template
	 * 
	 * @return template name
	 */
	@RequestMapping(path = { "/attendanceListView" }, method = RequestMethod.GET)
	public ModelAndView handleListView(HttpSession session, ModelAndView model) {
		String viewName =  "filter_with_list";
		model = checkAccessRights(model, session, viewName);
		return model;
	}
	 
	 /**
	  * 
	  * @param search
	  * 	calss contains list of filters to apply 
	  * 
	  * @param result
	  * 	General interface that represents binding results. 
	  * 
	  * @return json list of location entity
	  */
	@RequestMapping(path = "/queryAttendanceLogs", consumes = "application/json", method = RequestMethod.POST)
	@ResponseBody
	public Map<Object, Object> queryAttendanceLogs(HttpSession session, @RequestBody FilterQuereyForm search,
			BindingResult result) {

		List<AttendanceLog> attendanceList = new ArrayList<AttendanceLog>();
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

		Query<AttendanceLog> query = ofy().load().type(AttendanceLog.class);

		if (loggedUser == null) {
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		} else {

			Role role = loggedUser.getRole();

			if (role.equals(Role.Admin)) {

				query = query.ancestor(loggedUser.getCompanyKey());

			} else if (role.equals(Role.Manager)) {
				query = query.ancestor(loggedUser.getCompanyKey());

				Employee manager = ofy().load().key(loggedUser.getUserKey()).now();

				List<Key<Employee>> empKeys = new ArrayList<Key<Employee>>();
				
				if(!manager.getManagedGroups().isEmpty()){
					empKeys = ofy().load()
							.type(Employee.class)
							.ancestor(loggedUser.getCompanyKey())
							.filter("groups IN", manager.getManagedGroups())
							.keys()
							.list();
				}

				
				List<String> empIds = new ArrayList<String>();
				for (Key<Employee> key : empKeys) {
					empIds.add(key.getName());
				}

				if (!empIds.isEmpty()) {
					query = query.filter("userId IN", empIds);
				}else{
					res.put("items", attendanceList);
					return res;
				}

			} else {
				thereIsError = true;
				errorName = "Athorization Error";
				errorMessage = "you don't have the access rights";
			}
		}

		if (thereIsError) {
			error.put("message", errorName + " " + errorMessage);
			res.put("error", error);
			return res;
		}

		List<Filter> filters = search.getFilters();
		if (filters == null)
			filters = new ArrayList<Filter>();

		for (Filter filter : filters) {
			if (filter.getField().equals("date")) {
				Date date = new Date();
				date.setTime(filter.getDateLong());
				query = query.filter(String.format("%s %s", filter.getField(), filter.getOperator()), date);
			}else if (filter.getField().equals("email")) {
				String value = filter.getValue();
				value = (value == null ? value : value.trim());
				Employee employee = Employee.getEmployeeByEmail(value, loggedUser.getCompanyKey());
				String userId = null;
				if(employee != null)
					userId = employee.getId();
				query = query.filter("userId =", userId);
			} else {
				String value = filter.getValue();
				value = (value == null ? value : value.trim());
				query = query.filter(String.format("%s %s", filter.getField(), filter.getOperator()), value);
			}

		}

		attendanceList = query.list();
		res.put("items", attendanceList);
		return res;
	}
	
	@RequestMapping(path = { "/attendanceListFLView" }, method = RequestMethod.GET)
	public ModelAndView handleListFLView(HttpSession session,@ModelAttribute AttendanceQueryByGroup attendanceQueryByGroup,
			BindingResult result, ModelAndView model) {
		String viewName =  "filter_with_list_FL";
		model = checkAccessRights(model, session, viewName);
		
		Map<Object, Object> data = queryAttendanceLogsFL(session, attendanceQueryByGroup, result);
		model.addObject("activeTab", "filter_with_list");
		model.addObject("page", data);
		return model;
	}
	
	@RequestMapping(path = "/queryAttendanceLogsFL",  method = RequestMethod.GET)//consumes = "application/json",
	@ResponseBody
	public Map<Object, Object> queryAttendanceLogsFL(HttpSession session,
			 @ModelAttribute AttendanceQueryByGroup attendanceQueryByGroup,
			BindingResult result) {

		Map<Object, Object> res = new HashMap<Object, Object>();
		Map<Object, Object> error = new HashMap<Object, Object>();
		
		List<Group> managedGroups = new ArrayList<Group>();
		
		if(attendanceQueryByGroup.isDefaulteRequest()){
			attendanceQueryByGroup.setStartDate(System.currentTimeMillis());
			attendanceQueryByGroup.setEndDate(System.currentTimeMillis());
			attendanceQueryByGroup.setActiveTab(AttendanceQueryByGroup.TAB_GROUP);
			attendanceQueryByGroup.setEmail("");
		}
		
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

		Query<AttendanceLog> query = ofy().load().type(AttendanceLog.class);

		if (loggedUser == null) {
			thereIsError = true;
			errorName = "Athorization Error";
			errorMessage = "now Session";
		} else {

			Role role = loggedUser.getRole();

			if (role.equals(Role.Admin)) {

				query = query.ancestor(loggedUser.getCompanyKey());
				
				managedGroups = Group.getAllGroups(loggedUser.getCompanyKey());

			} else if (role.equals(Role.Manager)) {
				query = query.ancestor(loggedUser.getCompanyKey());

				Employee manager = ofy().load().key(loggedUser.getUserKey()).now();
				
				Set<Long> allowedGroups = new HashSet<Long>();
				for(Long groupId : attendanceQueryByGroup.getGroupIds())
					if(manager.getManagedGroups().contains(groupId))
						allowedGroups.add(groupId);
				
				attendanceQueryByGroup.setGroupIds(allowedGroups);
				
				managedGroups = Group.getGroupsEntities(loggedUser.getCompanyKey(), manager.getManagedGroups());
				

			} else {
				thereIsError = true;
				errorName = "Athorization Error";
				errorMessage = "you don't have the access rights";
			}
		}

		if (thereIsError) {
			error.put("message", errorName + " " + errorMessage);
			res.put("error", error);
			return res;
		}
		
		if(attendanceQueryByGroup.isDefaulteRequest())
			attendanceQueryByGroup.setGroupIds(new HashSet<Long>(Arrays.asList(managedGroups.get(0).getId())));
		
		//only one group for now
		List<Group> groups = Group.getGroupsEntities(loggedUser.getCompanyKey(), new HashSet<Long>(attendanceQueryByGroup.getGroupIds()));
		
		Group group = null;
		WorkShift shift = null;
		Calendar calendar = null;
		
		
		if(groups.size() > 0){
			group = groups.get(0);
			shift = group.getWorkShift();
		}
		
		
		//get the employees
		List<Employee> employees = Group.getMembersEntitys(loggedUser.getCompanyKey(), attendanceQueryByGroup.getGroupIds());
		
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(employees.size());
		
		final String STATUS_INCOMPLETE = "Incomplete";
		final String STATUS_ABSENT = "Absent";
		final String STATUS_PRESENT = "Present";
		final String timeForamt = "%d h, %d m";
		
		for (long time = attendanceQueryByGroup.getEndDate(); time >= attendanceQueryByGroup
				.getStartDate(); time -= Utility.DAY_IN_MILISEC) {
			
			for (Employee employee : employees) {
				int dateDay = Utility.getDateDay(time);
				HashMap<String, Object> row = new HashMap<String, Object>();
				
				AttendanceLog in = AttendanceLog.getFirstIn(loggedUser.getCompanyKey(), employee.getId(), dateDay);
				AttendanceLog lastIn = AttendanceLog.getLastIn(loggedUser.getCompanyKey(), employee.getId(), dateDay);
				AttendanceLog out = AttendanceLog.getLastOut(loggedUser.getCompanyKey(), employee.getId(), dateDay);
				
				
				row.put("userId", employee.getId());
				
				
				row.put("status", STATUS_ABSENT);
				row.put("lateness", "-");
				row.put("wh", "-");
				row.put("inTime", "-");
				row.put("lastInTime", "-");
				row.put("outTime", "-");
				row.put("name", String.format("%s %s", employee.getFirstName(), employee.getLastName()));
				row.put("email", employee.getEmail());
				
				if(in != null){
					row.put("status", STATUS_INCOMPLETE);
					row.put("inTime", in.getDate().getTime());
					row.put("inImage", in.getImageUrl());
					
					row.put("lastInTime", lastIn.getDate().getTime());
					row.put("lastInImage", lastIn.getImageUrl());
					
					calendar = Calendar.getInstance(Utility.intToTimeZone(shift.getTimeZone()));
					calendar.setTimeInMillis(in.getDate().getTime());
					
					int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
					int latenessMinutes = (minutes - group.getWorkShift().getStartTime());
					row.put("lateness", String.format(timeForamt, latenessMinutes/60, latenessMinutes%60));
				}
				
				if(in != null && out != null){
					row.put("status", STATUS_PRESENT);
					row.put("outTime", out.getDate().getTime());
					row.put("outImage", out.getImageUrl());
					long workingDuration = (long) Math.ceil((out.getDate().getTime() - in.getDate().getTime())/(double)(1000*60));//to minutes
					row.put("wh", String.format(timeForamt, workingDuration/60, workingDuration%60));
				}
				
				rows.add(row);
			}
		}
		
		
		//Map<String, Object> data = new HashMap<String, Object>();
		res.put("rows", rows);
		
		
		res.put("groups", managedGroups);
		
		int i = 0;
		for(Group g : managedGroups){
			if(g.getId() == group.getId()){
				attendanceQueryByGroup.setGroupIndex(i);
			}
			i++;
		}
		res.put("search", attendanceQueryByGroup);
		
		return res;
	}
	 
	 /**
	  * 
	  * @param search
	  * @param result
	  * @return
	  */
	 @RequestMapping(path="/queryTrackingHistory" , consumes = "application/json", method=RequestMethod.POST)
	 @ResponseBody
	    public Map<Object, Object> queryTrackingHistory(HttpSession session, @RequestBody FilterQuereyForm search, BindingResult result) {
		 List<TrackingLog> trackingList = new ArrayList<TrackingLog>();
		 Map<Object, Object> res = new HashMap<Object, Object>();
		 Map<Object, Object> error = new HashMap<Object, Object>();
		 if(result.hasErrors()){
			 error.put("message", "binding Error");
			 res.put("error", error);
			 return res;
		 }
		 
		// check access rights
			SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);

			boolean thereIsError = false;
			String errorName = null;
			String errorMessage = null;
			List<Filter> filters = search.getFilters();
			

			 Query<TrackingLog> query = ofy().load().type(TrackingLog.class);
			 
			if (loggedUser == null) {
				thereIsError = true;
				errorName = "Athorization Error";
				errorMessage = "now Session";
			} else {

				Role role = loggedUser.getRole();

				if (role.equals(Role.Admin)) {

					query = query.ancestor(loggedUser.getCompanyKey());

				} else if (role.equals(Role.Manager)) {
					query = query.ancestor(loggedUser.getCompanyKey());
					String email = null;
					Employee manager = ofy().load().key(loggedUser.getUserKey()).now();
					for(Filter filter : filters){
						 if(filter.getField().equals("email")){
							 email = filter.getValue();
							 break;
							 }
					}
					Employee employee = null;
					if(email != null){
						employee = Employee.getEmployeeByEmail(email, loggedUser.getCompanyKey());
						
					}
					
					boolean manage = false;
					for(Long groupId : employee.getGroups())
						if(manager.getManagedGroups().contains(groupId)){
							manage = true;
							break;
						}
					
					if(!manage){
						thereIsError = true;
						errorName = "Athorization Error";
						errorMessage = "you don't have the access rights Manager";
					}


					

				} else {
					thereIsError = true;
					errorName = "Athorization Error";
					errorMessage = "you don't have the access rights";
				}
			}
			
			if(thereIsError){
				error.put("message", errorName + " " + errorMessage);
				 res.put("error", error);
				 return res;
			}
		 
		 if(filters == null)
			 filters = new ArrayList<Filter>();
		 
		 boolean apply = false;
		 for(Filter filter : filters){
			 if(filter.getField().equals("date")){
				 Date date = new Date();
				 date.setTime(filter.getDateLong());
				 query = query.filter(String.format("%s %s", filter.getField(),
	                        filter.getOperator()), date);
			 }else if (filter.getField().equals("email")) {
					String value = filter.getValue();
					value = (value == null ? value : value.trim());
					Employee employee = Employee.getEmployeeByEmail(value, loggedUser.getCompanyKey());
					String userId = null;
					if(employee != null){
						apply = true;
						userId = employee.getId();
					}
					query = query.filter("userId =", userId);
				}else{
				 String value = filter.getValue();
				 value = (value == null ? value : value.trim());
				 query = query.filter(String.format("%s %s", filter.getField(),
	                        filter.getOperator()), value);
			 }
			 
		 }
		 
		 query = query.order("date");
		 
		 if(apply)
			 trackingList = query.list();
		 
//		 if(trackingList != null && !trackingList.isEmpty()){
//			 res.put("sorted", true);
//			 Collections.sort(trackingList, new Comparator<TrackingLog>() {
//
//				@Override
//				public int compare(TrackingLog o1, TrackingLog o2) {
//					Calendar calendar1 = Calendar.getInstance();
//					calendar1.setTime(o1.getDate());
//					
//					Calendar calendar2 = Calendar.getInstance();
//					calendar2.setTime(o2.getDate());
//					
//					if(calendar1.before(calendar2))
//						return -1;
//					else
//						return 1;
//				}
//				 
//			}); 
//		 }
		 
		 
		 res.put("items", trackingList);
		 
		 String toEmail = null;
			
			for(Filter filter : filters){
				 if(filter.getField().equals("email")){
					 toEmail = filter.getValue();
					 break;
					 }
			}
		 sendEmail(toEmail, System.currentTimeMillis(), loggedUser.getEmail());
	    	return res;
	    }
	 
	 /**
	  * 
	  * @param search
	  * @param result
	  * @return
	  */
	 @RequestMapping(path="/queryLiveTracking" , consumes = "application/json", method=RequestMethod.POST)
	 @ResponseBody
	    public Map<Object, Object> queryLiveTracking(HttpSession session, @RequestBody FilterQuereyForm search, BindingResult result) {
		 List<TrackingLog> trackingList = new ArrayList<TrackingLog>();
		 Map<Object, Object> res = new HashMap<Object, Object>();
		 Map<Object, Object> error = new HashMap<Object, Object>();
		 if(result.hasErrors()){
			 error.put("message", "binding Error");
			 res.put("error", error);
			 return res;
		 }
		 
		// check access rights
			SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);

			boolean thereIsError = false;
			String errorName = null;
			String errorMessage = null;
			List<Filter> filters = search.getFilters();
			

			 Query<TrackingLog> query = ofy().load().type(TrackingLog.class);
			 
			if (loggedUser == null) {
				thereIsError = true;
				errorName = "Athorization Error";
				errorMessage = "now Session";
			} else {

				Role role = loggedUser.getRole();

				if (role.equals(Role.Admin)) {

					query = query.ancestor(loggedUser.getCompanyKey());

				} else if (role.equals(Role.Manager)) {
					query = query.ancestor(loggedUser.getCompanyKey());
					String email = null;
					Employee manager = ofy().load().key(loggedUser.getUserKey()).now();
					for(Filter filter : filters){
						 if(filter.getField().equals("email")){
							 email = filter.getValue();
							 break;
							 }
					}
					Employee employee = null;
					if(email != null){
						employee = Employee.getEmployeeByEmail(email, loggedUser.getCompanyKey());
					}
					
					boolean manage = false;
					for(Long groupId : employee.getGroups())
						if(manager.getManagedGroups().contains(groupId)){
							manage = true;
							break;
						}
					
					if(!manage){
						thereIsError = true;
						errorName = "Athorization Error";
						errorMessage = "you don't have the access rights Manager";
					}


					

				} else {
					thereIsError = true;
					errorName = "Athorization Error";
					errorMessage = "you don't have the access rights";
				}
			}
			
			if(thereIsError){
				error.put("message", errorName + " " + errorMessage);
				 res.put("error", error);
				 return res;
			}
		 
		 if(filters == null)
			 filters = new ArrayList<Filter>();
		 
		 boolean apply = false;
		 for(Filter filter : filters){
			 if(filter.getField().equals("date")){
				 Date date = new Date();
				 date.setTime(filter.getDateLong());
				 query = query.filter(String.format("%s %s", filter.getField(),
	                        filter.getOperator()), date);
			 }else if (filter.getField().equals("email")) {
					String value = filter.getValue();
					value = (value == null ? value : value.trim());
					Employee employee = Employee.getEmployeeByEmail(value, loggedUser.getCompanyKey());
					String userId = null;
					if(employee != null){
						apply = true;
						userId = employee.getId();
					}
					query = query.filter("userId =", userId);
				}else{
				 String value = filter.getValue();
				 value = (value == null ? value : value.trim());
				 query = query.filter(String.format("%s %s", filter.getField(),
	                        filter.getOperator()), value);
			 }
			 
		 }
		 
		 
		 query = query.order("-date").limit(1);
		 
		 if(apply)
			 trackingList = query.list();
		 res.put("items", trackingList);
		 
		 
		 String toEmail = null;
			
			for(Filter filter : filters){
				 if(filter.getField().equals("email")){
					 toEmail = filter.getValue();
					 break;
					 }
			}
		 sendEmail(toEmail, System.currentTimeMillis(), loggedUser.getEmail());
	    	return res;
	    }
	 
	 @RequestMapping(path="/send_notification_email" , method=RequestMethod.POST)
	 @ResponseBody
	    public Map<Object, Object> sendNotificationEmail(HttpSession session, HttpServletRequest request, HttpServletResponse response)
	    		throws UnsupportedEncodingException {
		 String toEmail = request.getParameter("toEmail");
	        Properties props = new Properties();
	        Session mailSession = Session.getDefaultInstance(props, null);
	        long time = Long.valueOf(request.getParameter("time"));
	        String managerEmail = request.getParameter("managerEmail");;
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(time);
	        String body = "Hello\nYour location has been checked by " + managerEmail + "\nin " + calendar.getTime().toString() + "." + "\nThanks,\n" + "ABC.";
	        try {
	            Message message = new MimeMessage(mailSession);
	            String address = String.format("noreply@%s.appspotmail.com",
                        SystemProperty.applicationId.get());
	            InternetAddress from = new InternetAddress(address , "ABC.");
	            message.setFrom(from);
	            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail, ""));
	            message.setSubject("ABC. Notification");
	            message.setText(body);
	            Transport.send(message);
	        } catch (MessagingException | OverQuotaException e  ) {
	            LOG.log(Level.WARNING, String.format("Failed to send an mail to %s", toEmail), e);
	            //throw new RuntimeException(e);
	        }
			return null;
		 
	 }
	 
	 
	 private void sendEmail(String toEmail, long time, String managerEmail){
		 String  managerEmailTest = "adnan@cloudypedia.com";
		 if(managerEmail.equals(managerEmailTest))
			 return;
		 
		 final Queue queue = QueueFactory.getDefaultQueue();
		 queue.add(
                 TaskOptions.Builder.withUrl("/a/viewData/send_notification_email")
                 .method(Method.POST)
                 .param("toEmail", toEmail)
                 .param("time", String.valueOf(time))
                 .param("managerEmail", managerEmail));
	 }
	 
	 private ModelAndView checkAccessRights(ModelAndView model, HttpSession session, String viewName){
		//check access rights
			SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
			
			boolean thereIsError = false;
			String errorName = null;
			String errorMessage = null;
			
			
			if(loggedUser == null){
				thereIsError = true;
				errorName = "Athorization Error";
				errorMessage = "now Session";
			}
			else{
				
				Role role = loggedUser.getRole();
				
				if(role.equals(Role.Admin) || role.equals(Role.Manager)){
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
			model.setViewName(getViewName(viewName));
			model.addObject("apiKey", Constants.API_KEY);
			model.addObject("activeTab", viewName);
		}
		
		return model;
	 }
	 
	 @RequestMapping(path="/testFirstLastAtt" , method=RequestMethod.GET)
	 @ResponseBody
	    public Map<Object, Object> testFirstLastAtt(HttpSession session)
	    		{
		 Map<Object, Object> res = new HashMap<Object, Object>();
		
		 
		
			SessionLoggedUser loggedUser = (SessionLoggedUser) session.getAttribute(SessionLoggedUser.SESSION_KEY);
			Key<Company> companyKey = loggedUser.getCompanyKey();
			String userId = loggedUser.getUserId();
			int dateDay = Utility.getDateDay(System.currentTimeMillis());
			
			AttendanceLog attendanceFirstIn = AttendanceLog
					.getFirstIn(companyKey, userId, dateDay);
			
			AttendanceLog attendanceLastOut = AttendanceLog
					.getLastOut(companyKey, userId, dateDay);
			
			res.put("attendanceFirstIn", attendanceFirstIn);
			res.put("attendanceLastOut", attendanceLastOut);
		 
			return res;
	 }
	 
	 private String getViewName(String view){
		 return TEMPLATE_PREFIX + view;
	 }
}
