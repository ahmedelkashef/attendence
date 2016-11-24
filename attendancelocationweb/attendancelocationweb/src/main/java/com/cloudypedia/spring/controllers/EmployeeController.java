package com.cloudypedia.spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path="/employee")
public class EmployeeController {
	
	private String TEMPLATE_PREFIX = "employee/";
	
	private String getViewName(String view){
		 return TEMPLATE_PREFIX + view;
	 }

}
