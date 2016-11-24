package com.cloudypedia.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import com.cloudypedia.Constants;
import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Employee;
import com.cloudypedia.domain.Role;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.googlecode.objectify.Key;

import util.Utility;

public class VerifyUser{
	//private static final Logger log = Logger.getLogger(VerifyUser.class.getName());
	public VerifyUser(){}
	
	static public VerifyResults verifyToken(String idTokenString) {
		
		VerifyResults results = new VerifyResults();
		/** Google public keys manager. */
		GoogleIdTokenVerifier verifier1 = new GoogleIdTokenVerifier.Builder(Utils.getDefaultTransport(), Utils.getDefaultJsonFactory())
				.setAudience(Arrays.asList(Constants.WEB_CLIENT_ID))
				// If you retrieved the token on Android using the Play Services
				// 8.3 API or newer, set
				// the issuer to "https://accounts.google.com". Otherwise, set
				// the issuer to
				// "accounts.google.com". If you need to verify tokens from
				// multiple sources, build
				// a GoogleIdTokenVerifier for each issuer and try them both.
				.setIssuer("https://accounts.google.com").build();
		
		GoogleIdTokenVerifier verifier2 = new GoogleIdTokenVerifier.Builder(Utils.getDefaultTransport(), Utils.getDefaultJsonFactory())
				.setAudience(Arrays.asList(Constants.WEB_CLIENT_ID))
				// If you retrieved the token on Android using the Play Services
				// 8.3 API or newer, set
				// the issuer to "https://accounts.google.com". Otherwise, set
				// the issuer to
				// "accounts.google.com". If you need to verify tokens from
				// multiple sources, build
				// a GoogleIdTokenVerifier for each issuer and try them both.
				.setIssuer("accounts.google.com").build();


		GoogleIdToken idToken = null;
		try {
			idToken = verifier1.verify(idTokenString);
			if(idToken == null)
				idToken = verifier2.verify(idTokenString);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		if (idToken != null) {
			results.setValid(true);
			Payload payload = idToken.getPayload();

			// Print user identifier
			String userId = payload.getSubject();
			if(userId != null)
				results.setUserId(userId);

			// Get profile information from payload
			String email = payload.getEmail();
			if(email != null)
				results.setEmail(email.toLowerCase());
			
			results.setHostedDomain(Utility.getDomainFromMail(email));
			
//			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			results.setPictureUrl(pictureUrl);
//			String locale = (String) payload.get("locale");
//			String familyName = (String) payload.get("family_name");
//			String givenName = (String) payload.get("given_name");

			// Use or store profile information
			// ...

		} else {
			results.setValid(false);
		}
		return results;
	}	
	
	
	public static SessionLoggedUser checkRole(VerifyResults verifyResults){
		String email = verifyResults.getEmail();
		SessionLoggedUser loggedUser = new SessionLoggedUser();
		
		loggedUser.setEmail(email);
		loggedUser.setHostedDomain(verifyResults.getHostedDomain());
		loggedUser.setUserId(verifyResults.getUserId());
		loggedUser.setPictureUrl(verifyResults.getPictureUrl());
		
		//check if owner
		for(String e : Constants.PROJECT_OWNERS){
			if(e.equals(email)){
				loggedUser.setRole(Role.Owner);
				return loggedUser;
			}
		}
		
		
		//get user company
		Company company = Company.getCompanyByDomain(loggedUser.getHostedDomain());
		
		
		if(company == null){
			loggedUser.setRole(Role.Invalid);
			return loggedUser;
		}
		
		Key<Company> companyKey = Key.create(Company.class, company.getId());
		
		loggedUser.setCompanyKey(companyKey);
		loggedUser.setLogoUrl(company.getLogoUrl());
		
//		// check if Admin
//		for(String adminEmail : company.getAdmins()){
//			if(adminEmail.equals(email)){
//				loggedUser.setRole(Role.Admin);
//				return loggedUser;
//			}
//		}
		
//		//check if Manager
//		Manager manager = ofy().load().type(Manager.class)
//			.ancestor(companyKey)
//			.filter("managerEmail =", email).limit(1).first().now();
		
		Employee employee = Employee.getEmployeeById(loggedUser.getUserId(), companyKey);
		
		if(employee != null){
			loggedUser.setRole(employee.getRole());
			Key<Employee> emplyeeKey = Key.create(companyKey, Employee.class, loggedUser.getUserId());
			loggedUser.setUserKey(emplyeeKey);
			return loggedUser;
		}
		
		
			
		
		
		loggedUser.setRole(Role.Invalid);
		return loggedUser;
		
		
		
	}
}