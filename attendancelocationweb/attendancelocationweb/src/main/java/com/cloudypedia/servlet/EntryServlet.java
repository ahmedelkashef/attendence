package com.cloudypedia.servlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.services.admin.directory.Directory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EntryServlet extends HttpServlet {
	private static final Logger logger = Logger
			.getLogger(EntryServlet.class.getName());
	@Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException 
	{
		doGet(request, response);
    }
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
         //   PrintWriter out = response.getWriter();
        	
        String domain = request.getParameter("domain");
        logger.warning("domain:" + domain);
        String currURL = "";
        	if (request.getServerName().equalsIgnoreCase("localhost"))
        		currURL = request.getScheme() + "://" + request.getServerName()
        				+ ":8888";//+request.getRequestURI();
        	else
        		currURL = request.getScheme() + "://" + request.getServerName();// +request.getRequestURI();
        	String Email = "";
	        String redirect_url= currURL+"/welcome.jsp";
	        System.out.println(redirect_url);
	        logger.warning("url:" + redirect_url);
	        try {
	    	GoogleAuthorizationCodeRequestUrl authUrl = new GoogleAuthorizationCodeRequestUrl(
                "584666349039-hapvscpag4083e3vnai97a1on2m60cjm.apps.googleusercontent.com", redirect_url,
                Arrays.asList("https://www.googleapis.com/auth/admin.directory.user.readonly",
                				"https://www.googleapis.com/auth/admin.directory.domain.readonly"));
    			authUrl.setState(domain);
		        authUrl.setAccessType("online");
		        String authorizationURL=authUrl.build();
		        request.getSession(true).setAttribute("Domain", domain);
		        request.getSession(true).setAttribute("isLoggedIn", "true");
		        logger.warning("authenticated");
		        response.sendRedirect(authorizationURL);
	        }
	        catch(Exception e){
				request.getSession(false).setAttribute("error","Sorry, Authentication Failed.");
				response.sendRedirect("/error.jsp");

	        }
		   
    }

    

}
