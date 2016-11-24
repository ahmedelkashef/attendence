package com.cloudypedia.spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/** This class handle http requests depending on the path requested 
 * **/
@Controller
public class HomeController {
	@RequestMapping(path = { "/jsp1" }, method = RequestMethod.GET)
	public ModelAndView handleMapViewHistory( ModelAndView model) {
		String viewName = "jsp/jspTest";
		model.addObject("msg", "hiii AlLLLLLLLLLLLLLLLLl");
		model.setViewName(viewName);
		return model;
	}

}
