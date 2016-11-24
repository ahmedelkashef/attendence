package com.cloudypedia.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudypedia.domain.Location;
import com.cloudypedia.work.GSpreadSheet;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

/**
 * Servlet implementation class UploadLocation
 * 
 * This servlet used to create blob upload url
 * 
 * upload location data
 */
public class UploadLocation extends HttpServlet {
	public static final Logger LOG = Logger.getLogger(UploadLocation.class.getName());

	private static final long serialVersionUID = 1L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadLocation() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		String temp = "";
		Enumeration params = request.getParameterNames();
		while(params.hasMoreElements()){
			String p =  params.nextElement().toString();
			temp += p + "=" + request.getParameter(p)+" ,";
		}
			
		LOG.warning("params:\n" + temp);
		
		temp = "";
		Enumeration headers = request.getHeaderNames();
		while(headers.hasMoreElements())
			temp += headers.nextElement().toString() + ",";
		LOG.warning("headers:\n" + temp);
		
		temp = "";
		LOG.warning("in Servlet");
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
		
		LOG.warning("getUploads kes:");
		
		for(String k : blobs.keySet())
			temp += k + ",";
		LOG.warning(temp);
		
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
        List<BlobKey> blobKeys = blobs.get("image");

        
        if (blobKeys == null || blobKeys.isEmpty()) {
        	LOG.warning("blobKeys: " + blobKeys);
        } else {
    		Map<String, List<BlobInfo>> blobInfos = blobstoreService.getBlobInfos(request);
    		List<BlobInfo> infos = blobInfos.get("image");
    		if(infos.get(0).getContentType().toLowerCase().matches("image/(jpg|jpeg|png)")){
    			
    			Location location = new Location();
    			
    			Date date = new Date();
				date.setTime(Long.parseLong(request.getParameter("date")));
				location.setDate(date);
				location.setLatitude(Double.parseDouble(request.getParameter("latitude")));
				location.setLongitude(Double.parseDouble(request.getParameter("longitude")));
				location.setStatus(request.getParameter("status"));
				location.setUser(request.getParameter("user"));
				location.setEmail(request.getParameter("email"));
				location.setImageID(request.getParameter("imageID"));
				location.setImage(blobKeys.get(0));
				
				GSpreadSheet.writeToSheet(location);
    		    Location.createLocation(location);
    		    out.println("url: " + location.getImageUrl());
    			
            	
    		}else{
    			LOG.warning("content type: " + infos.get(0).getContentType().toLowerCase());
    		}
        }

	}

}
