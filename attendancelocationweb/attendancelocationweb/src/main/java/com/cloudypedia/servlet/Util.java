package com.cloudypedia.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudypedia.domain.Company;
import com.cloudypedia.domain.Employee;
import com.cloudypedia.domain.Role;
import com.cloudypedia.form.CompanyForm;
import com.cloudypedia.form.EmployeeForm;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.model.User;
import com.googlecode.objectify.Key;


@SuppressWarnings("serial")

public class Util extends HttpServlet  {	
	private static final Logger logger = Logger.getLogger(Util.class
			.getCanonicalName());
		private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JsonFactory JSON = new JacksonFactory();

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String func = req.getParameter("func");
		String list = req.getParameter("list");
		String Nlist = req.getParameter("Nlist");
		String domain = req.getParameter("domain");
		String code = req.getParameter("code");
		
		
		
		if (func.equals("addtoDatastore")){
			PrintWriter out = resp.getWriter();
			Company company = AddCompany( req.getSession(true).getAttribute("domains"), domain);
			if(company != null){
				Key<Company> companyKey = Key.create(Company.class, company.getId());
				addEmployee(list,Nlist,domain, req.getSession(true).getAttribute("usersList"), companyKey);
			}
			
			System.out.println("added employee");
		}
		
		
	
	}
	public static boolean  addEmployee (String list,String Nlist,String domain, Object usersListO, Key<Company> companyKey) {
		HashSet<String> admins = null ;
		if(usersListO != null){
			admins = (HashSet<String>) usersListO;
		}
		String[] UserArray = list.split(",");
		String[] NameArray = Nlist.split(",");
		String lastName = "";
		String firstName = "";
		boolean Added = false;
		for (int i =0 ; i<UserArray.length;i++)
		{
//			if(NameArray[i].split("\\w+").length>1){
//
//			       lastName = NameArray[i].substring(NameArray[i].lastIndexOf(" ")+1);
//			       firstName = NameArray[i].substring(0, NameArray[i].lastIndexOf(' '));
//			       
//			    }
//			     else{
//			       firstName = NameArray[i];
//			    }
			firstName = NameArray[i];
			logger.warning(firstName);
			Employee Employ = new Employee();
			EmployeeForm EF = new EmployeeForm();
			EF.setEmail(NameArray[i].toLowerCase().trim());
			EF.setId(UserArray[i].toLowerCase().trim());
			firstName = firstName.substring(0, firstName.indexOf('@'));
			EF.setFirstName(firstName);
			EF.setLastName(lastName);
			
			if (admins.contains(EF.getEmail())){
				EF.setRole(Role.Admin);
			} else {
				EF.setRole(Role.Employee);
			}
			
			try{
			Employee.createEmployee(EF, companyKey);
			Added = true;
			}
			catch(Exception e) {
				e.printStackTrace();
				Added = false;
			}
		}
		return Added;
	}
	
	public static Company AddCompany (Object domainsO, String companyName){
		HashSet<String> domains = null ;
		
		try{
			if(domainsO != null){
				domains = (HashSet<String>) domainsO;
				CompanyForm companyForm = new CompanyForm();
				
				companyForm.setDomains(domains);
				companyForm.setName(companyName);
				
				Company company = Company.createCompany(companyForm);
				return company;
			}
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	
	}


	
	
	
}