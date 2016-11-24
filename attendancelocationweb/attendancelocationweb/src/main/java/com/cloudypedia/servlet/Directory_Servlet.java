package com.cloudypedia.servlet;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;


public class Directory_Servlet extends HttpServlet{
	private static final Logger logger = Logger
			.getLogger(Directory_Servlet.class.getName());
	public static Directory authenticate(String code , String CurrURL) throws IOException {
		
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		GoogleAuthorizationCodeTokenRequest authorizationTokenRequest = new GoogleAuthorizationCodeTokenRequest(
				transport, jsonFactory,
				"584666349039-hapvscpag4083e3vnai97a1on2m60cjm.apps.googleusercontent.com",
				"tRa6jgi8TzKDtiugOm18NMtA",
				code, CurrURL);
		GoogleTokenResponse tokenResponse = null;
		try {
			tokenResponse = authorizationTokenRequest.execute();
		} catch (Exception e) {
			e.printStackTrace();
			HttpServletRequest request = null;
			HttpServletResponse response = null;
			request.getSession(false).setAttribute("error","Sorry, Authentication Failed.");
			response.sendRedirect("/error.jsp");
			
		}
		GoogleCredential gc = new GoogleCredential();
		gc.setAccessToken(tokenResponse.getAccessToken());
		System.out.println("Authenticated");
		Directory dir = new Directory.Builder(transport, jsonFactory,
				gc).setApplicationName("cloudypedia-abc").build();
		logger.warning("directory is finally done");
		return dir;
	}
	
}
