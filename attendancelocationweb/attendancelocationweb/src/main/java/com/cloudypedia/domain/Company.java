package com.cloudypedia.domain;

import static com.cloudypedia.service.OfyService.ofy;
import static com.cloudypedia.service.OfyService.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cloudypedia.form.CompanyForm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
/**
 * This class represent a company or google domain
 *
 */
@Entity
@Cache
public class Company {
	
	@Id
	private long id;

	/**
	 * company domains
	 */
	@Index
	private Set<String> domains;
	/**
	 * company name
	 */
	private String name;
	
	/**
	 * billing plan
	 */
	private long billingPlanId;
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	@JsonIgnore
	private BlobKey logo;
	
	//private List<String> admins = new ArrayList<String>();
	
	public Company(){}
	
	
	public String getName() {
		return name;
	}
	public static Company createCompany(CompanyForm companyForm) {
		
		Company company = null;
		//check if domains already exist
		
		if(companyForm.getDomains() != null && !companyForm.getDomains().isEmpty()){
			List<Company> companys = ofy().load().type(Company.class).filter("domains IN", companyForm.getDomains()).list();
			if(!companys.isEmpty())
				company = companys.get(0);//null;
			
			if(company == null){
				company = new Company();
				Key<Company> companyKey = factory().allocateId(Company.class);
		    
				company.setId(companyKey.getId());
			}
		    
		    company.setDomains(companyForm.getDomains());
		    company.setName(companyForm.getName());
		    
		    ofy().save().entity(company).now();
		    
		    return company;
			
			
		}else{
			return null;
		}
		    
	    
	}
	
	public static Company getCompanyByDomain(String domain){
		
		return ofy().load()
			.type(Company.class)
			.filter("domains =", domain)
			.first()
			.now();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public Set<String> getDomains() {
		return (domains == null ? new HashSet<String>():domains);
	}


	public void setDomains(Set<String> domains) {
		this.domains = domains;
	}



	public long getBillingPlanId() {
		return billingPlanId;
	}


	public void setBillingPlanId(long billingPlanId) {
		this.billingPlanId = billingPlanId;
	}
	
	public String getLogoUrl() {
		if(this.getLogo() == null)
			return "/img/defaulteLogo.png";
		
		return Company.getLogoUrl(this.getLogo());
	}
	
	public static String getLogoUrl(BlobKey blobKey) {
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		
		ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
		options.secureUrl(true);
		return imagesService.getServingUrl(options);
	}
	
	public BlobKey getLogo() {
		return logo;
	}

	public void setLogo(BlobKey logo) {
		this.logo = logo;
	}


//	public List<String> getAdmins() {
//        return admins == null ? null : ImmutableList.copyOf(admins);
//    }
//
//
//	public void setAdmins(List<String> admins) {
//		this.admins = admins;
//	}
//	
//	public void addAdmin(String email) {
//		if(email != null){
//			admins.add(email);
//		}
//    }
	

}
