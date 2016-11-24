package com.cloudypedia.domain;

import static com.cloudypedia.service.OfyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.cloudypedia.form.EmployeeForm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * 
 * This class represent employee kind 
 *
 */
@Entity
@Cache
public class Employee extends BaseKindCompany{
	
	@Id
	private String id;

	@Index
	private String email;
	
	@Index
	private Role role;
	
	private String firstName;
	private String lastName;
	
	//groups for employee
	@Index
	private HashSet<Long> groups;
	
	//groups that the manager manage
	private HashSet<Long> managedGroups;
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @JsonIgnore
	private String userIdToken;
	
	private static char delimiter = '|';

	public Employee(){}
	
	public static Employee createEmployee(EmployeeForm employeeForm, Key<Company> companyKey) {
	    
		Employee employee = getEmployeeById(employeeForm.getId(), companyKey);
		
		if(employee == null){
			employee = new Employee();
			employee.setId(employeeForm.getId());
			employee.setCompanyKey(companyKey);
			
		}
	    
	    
	    employee.setEmail(employeeForm.getEmail());
	    employee.setFirstName(employeeForm.getFirstName());
	    employee.setLastName(employeeForm.getLastName());
	    employee.setRole(Employee.getHigherRole(employee.getRole(), employeeForm.getRole()));
	    
	    
	    ofy().save().entity(employee).now();
	    
	    return employee;
	}
	
	public static Role getHigherRole(Role r1, Role r2){
		if(r1 == null)
			return r2;
		
		if(r2 == null)
			return r1;
		
		Map<Role, Integer> roleDegree = new HashMap<Role, Integer>();
		
		int minDegree = 0;
		roleDegree.put(Role.Invalid, minDegree++);
		roleDegree.put(Role.Employee, minDegree++);
		roleDegree.put(Role.Manager, minDegree++);
		roleDegree.put(Role.Admin, minDegree++);
		
		return (roleDegree.get(r1) > roleDegree.get(r2)? r1 : r2);
		
		
		
	}
	
	public static String generateUserIdToken(long companyId, String employeeId){
		Random random = new Random();
		
		String userIdToken = String.format("%d%c%d%c%s"
				, random.nextInt()
				, delimiter
				, companyId
				, delimiter
				, employeeId);
		
		return userIdToken;
	}
	
	public static Employee getEmployeeByEmail(String email, Key<Company> companyKey){
		
		return ofy().load()
			.type(Employee.class)
			.ancestor(companyKey)
			.filter("email =", email).limit(1).first().now();
	}
	
	public static Employee getEmployeeByUserIdToken(String userIdToken){
		if(userIdToken == null)
			return null;
		
		try{
			int delimiterPos1 = userIdToken.indexOf(delimiter);
			int delimiterPos2 = userIdToken.indexOf(delimiter, delimiterPos1 + 1);
			
			String random = userIdToken.substring(0, delimiterPos1);
			long companyId = Long.valueOf(userIdToken.substring(delimiterPos1+1, delimiterPos2));
			String employeeId = userIdToken.substring(delimiterPos2+1);
			
			
			Key<Company> companyKey = Key.create(Company.class, companyId);
			Key<Employee> employeeKey = Employee.getKey(companyKey, employeeId);
			Employee employee = ofy().load().key(employeeKey).now();
			
			if(employee != null && employee.getUserIdToken() != null){
				int eDelimiterPos1 = userIdToken.indexOf(delimiter);
				String eRandom = employee.getUserIdToken().substring(0, eDelimiterPos1);
				if(eRandom.equals(random))//check if the is not old
					return employee;
				else
					return null;
			
			}else
				return null;
			
		}catch(Exception e){
			return null;	
		}
		
		
		
	}
	
	public static Employee getEmployeeById(String id,  Key<Company> companyKey){
		
		Key<Employee> employeeKey = Key.create(companyKey,Employee.class, id);
		
		return ofy().load().key(employeeKey).now();
	}
	
	
	public static Key<Employee> getKey(Key<Company> companyKey, String id){
		return Key.create(companyKey, Employee.class, id);
	}
	
	public static ArrayList<Employee> getEmployeeEntities(Key<Company> companyKey, HashSet<String> setId) {
		List<Key<Employee>> keysList = new ArrayList<Key<Employee>>();
		for(String id : setId){
			keysList.add(Employee.getKey(companyKey, id));
		}
        return new ArrayList<>(ofy().load().keys(keysList).values());
    }
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public HashSet<Long> getManagedGroups() {
		return managedGroups == null ? new HashSet<Long>() : managedGroups;
	}

	public void setManagedGroups(HashSet<Long> managedGroups) {
		this.managedGroups = managedGroups;
		if(managedGroups != null && !managedGroups.isEmpty()
				&& (this.role == null || this.role != Role.Admin)){
			 this.role = Role.Manager;
		}
		
		if((managedGroups == null || managedGroups.isEmpty())
				&& (this.role == null || this.role != Role.Admin))
			this.role = Role.Employee;
	}
	
	public void addManagedGroup(Long newGroup) {
		if(newGroup != null){
			if(this.managedGroups == null)
				this.managedGroups = new HashSet<Long>();
			managedGroups.add(newGroup);
			if(this.role == null || this.role != Role.Admin){
				 this.role = Role.Manager;
			}
		}
    }
	
	public void removeManagedGroup(Long newGroup) {
		if(newGroup != null){
			if(this.managedGroups == null)
				this.managedGroups = new HashSet<Long>();
			managedGroups.remove(newGroup);
			if(managedGroups.isEmpty() && this.role == Role.Manager){
				 this.role = Role.Employee;
			}
		}
    }
	
	public HashSet<Long> getGroups() {
		return groups == null ? new HashSet<Long>() : groups;
		
	}

	public void setGroups(HashSet<Long> groups) {
		this.groups = groups;
	}
	
	public void addGroup(Long newGroup) {
		if(newGroup != null){
			if(this.groups == null)
				this.groups = new HashSet<Long>();
			groups.add(newGroup);
		}
    }

	public void removeGroup(Long newGroup) {
		if(newGroup != null){
			if(this.groups == null)
				this.groups = new HashSet<Long>();
			groups.remove(newGroup);
		}
    }
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @JsonIgnore
	public void updateUserIdToken() {
		this.userIdToken = Employee.generateUserIdToken(this.getCompanyKey().getId(), id);
		ofy().save().entity(this).now();
		
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserIdToken() {
		return userIdToken;
	}

	public void setUserIdToken(String userIdToken) {
		this.userIdToken = userIdToken;
	}

}
