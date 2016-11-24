package com.cloudypedia.spring.advisor;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

/**
 * Here you can put common code between controller like exceptions handling
 *
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends AbstractJsonpResponseBodyAdvice {

    public ExceptionControllerAdvice() {
        super("callback");
    }

	@ExceptionHandler(Exception.class)
	public ModelAndView exception(Exception e) {
		
		ModelAndView mav = new ModelAndView("exception");
		mav.addObject("name", e.getClass().getSimpleName());
		mav.addObject("message", e.toString());

		return mav;
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ModelAndView exception(RuntimeException e) {
		
		ModelAndView mav = new ModelAndView("exception");
		mav.addObject("name", e.getClass().getSimpleName());
		mav.addObject("message", e.getMessage());

		return mav;
	}
}
