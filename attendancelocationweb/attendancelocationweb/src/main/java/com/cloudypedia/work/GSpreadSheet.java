package com.cloudypedia.work;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.mortbay.log.Log;

import com.cloudypedia.domain.Location;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GSpreadSheet {

																																			// google
																																				// spreadsheet
																																				// URI

	/** Application name. */
	private static final String APPLICATION_NAME = "Location Attendance";

	/**
	 * Global instance of the scopes required by this quickstart
	 */
	private static final List<String> SCOPES = new ArrayList<String>() {
		{
			add("https://spreadsheets.google.com/feeds");
			add("https://docs.google.com/feeds");
			add("https://spreadsheets.google.com/feeds/worksheets/key/private/full");
			//add("https://www.googleapis.com/auth/spreadsheets");
			
		}
	};

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method authenticate with google and return Sheet service.
	 * @returnSheet service.
	 * @since 2016-05-25
	 */
	public static Sheets authenticate() throws GeneralSecurityException, IOException {

		// GoogleCredential credential =
		// GoogleCredential.getApplicationDefault();
		// if (credential.createScopedRequired()) {
		// System.out.println("need scopes");
		// credential = credential.createScoped(SCOPES);
		// }

		GoogleCredential credential = new GoogleCredential.Builder().setTransport(new NetHttpTransport())
				.setJsonFactory(new JacksonFactory()).setServiceAccountScopes(SCOPES)
				.setServiceAccountId("sheet-963@locationattendance.iam.gserviceaccount.com")
				.setServiceAccountUser("adnan@cloudy-apps-test.com")
				.setServiceAccountPrivateKeyFromP12File(new File("WEB-INF/key/LocationAttendance-f8388a34217d.p12"))
				.build();

		Sheets service = new Sheets.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
				.setApplicationName(APPLICATION_NAME).build();
		// Sheets service = new Sheets(APPLICATION_NAME);Sheets.Builder
		// // service.setProtocolVersion(Sheets.Versions.V3);
		// service.setOAuth2Credentials(credential);
		// service.setConnectTimeout(0);
		


		
		return service;

	}
	
	/**
	 * This method write data to google spreadsheet 
	 * 
	 * 
	 * @param date </br>
	 *            Date of location.
	 * @param latitude </br>
	 *            location latitude.
	 *@param longitude </br>
	 *				location longitude.
	 *@param name </br>
	 *				device name
	 * @return nothing.
	 * @since 2016-05-25
	 */
	public static void writeToSheet(Date date, double latitude, double longitude, String name, String status) {
		
				try {
					// Build a new authorized API client service.
					Sheets service = authenticate();
					
					// https://docs.google.com/spreadsheets/d/1-waBJz8xQS3gf5UGPR_ynoIefoSTCmko0Xo_PAhcitY/edit
					String spreadsheetId = "1-waBJz8xQS3gf5UGPR_ynoIefoSTCmko0Xo_PAhcitY";
				
				
				
			
				ValueRange response = service.spreadsheets().values().get(spreadsheetId, "A:A").execute();
				int numberOfRows = response.getValues().size();
				
				ValueRange valueRange = new ValueRange();
				
				List<List<Object>> values = new ArrayList<List<Object>>();
				List<Object> row = new ArrayList<Object>();
			
				SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timeZone = "GMT+2";

				
				
				row.add(isoFormat.format(convertToTimezone(timeZone,date)));
				row.add(String.valueOf(latitude));
				row.add(String.valueOf(longitude));
				row.add(name);
				row.add(timeZone);
				row.add(status);
				values.add(row);
				
				//valueRange.setRange("sheet1!A2:B2");
				valueRange.setValues(values );
				//valueRange.setMajorDimension("ROWS");

				UpdateValuesResponse updateResponse = service.spreadsheets()
				.values()
				.update(spreadsheetId, "A"+ (numberOfRows + 1) /*+ ":B"*/, valueRange)
				.setValueInputOption("USER_ENTERED")//RAW
				.execute();
				
				System.out.println("TotalUpdatedCells: " + updateResponse.getUpdatedCells());
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	/**
	 * This method convert from time zone to another
	 * 
	 * 
	 * @param timeZone </br>
	 *            the ID for a TimeZone, either an abbreviation such as "PST", a
	 *            full name such as "America/Los_Angeles", or a custom ID such
	 *            as "GMT-8:00". Note that the support of abbreviations is for
	 *            JDK 1.1.x compatibility only and full names should be used..
	 * @param date </br>
	 *            date to convert
	 * @return the date after converting.
	 * @exception ParseException
	 * @since 2016-05-25
	 * @see ParseException
	 *  @author adnan
	 */

	public static Date convertToTimezone(String timeZone, Date date){
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
		// To TimeZone timeZone
		SimpleDateFormat sdfNew = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
		TimeZone tzInNew = TimeZone.getTimeZone(timeZone);
		sdfNew.setTimeZone(tzInNew);
		
		String sDateInNew = sdfNew.format(date); // Convert to String first
		Date dateInNew = null;
		try {
			dateInNew = formatter.parse(sDateInNew);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dateInNew;

	}

	public static void writeToSheet() {
		// Build a new authorized API client service.
		try {
			Sheets service = authenticate();

		// Prints the names and majors of students in a sample spreadsheet:
		// https://docs.google.com/spreadsheets/d/1-waBJz8xQS3gf5UGPR_ynoIefoSTCmko0Xo_PAhcitY/edit
		String spreadsheetId = "1-waBJz8xQS3gf5UGPR_ynoIefoSTCmko0Xo_PAhcitY";
//		String range = "A2:B";
//		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
//		List<List<Object>> values = response.getValues();
//		if (values == null || values.size() == 0) {
//			System.out.println("No data found.");
//		} else {
//			System.out.println("Name, Major");
//			for (List row : values) {
//				// Print columns A and E, which correspond to indices 0 and 4.
//				System.out.printf("%s %s\n", row.get(0),row.get(1));
//			}
//		}
		
		
		
		Integer rowCounts = service.spreadsheets().get(spreadsheetId).execute().getSheets().get(0)
				.getProperties().getGridProperties().getRowCount();
		
		List<GridData> grides = service.spreadsheets().get(spreadsheetId).execute().getSheets().get(0).getData();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, "A:A").execute();
		System.out.println("rowCounts " + rowCounts +"\n");
		System.out.println("rowCounts2 " + response.getRange() +"\n");
		System.out.println("rowCounts2 " + response.getValues().size() +"\n");
//		System.out.println("grides :" + grides.size() +"\n");
//		System.out.println("grides :" + grides.get(0).size() +"\n");
		BatchUpdateValuesRequest batchRequest = new BatchUpdateValuesRequest();
		batchRequest.setValueInputOption("RAW");
		List<ValueRange> updateValueRangeList = new ArrayList<ValueRange>();
		ValueRange valueRange = new ValueRange();
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		List<Object> row = new ArrayList<Object>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
		row.add(isoFormat.format(calendar.getTime()));
		row.add(String.valueOf(45));
		row.add(String.valueOf(45.7));
		row.add("ad");
		values.add(row);
		
		//valueRange.setRange("sheet1!A2:B2");
		valueRange.setValues(values );
		//valueRange.setMajorDimension("ROWS");
		batchRequest.setData(updateValueRangeList);

		UpdateValuesResponse updateResponse = service.spreadsheets().
		values().update(spreadsheetId, "A"+ (response.getValues().size() + 1)/* + ":B"*/, valueRange)
		.setValueInputOption("USER_ENTERED")//RAW
		.execute();
		
		System.out.println("TotalUpdatedCells: " + updateResponse.getUpdatedCells());
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This method write data to google spreadsheet 
	 * 
	 * 
	 * @param location </br>
	 * @return nothing.
	 * @since 2016-06-06
	 */
	public static void writeToSheet(Location location) {

		try {
			// Build a new authorized API client service.
			Sheets service = authenticate();
			
			// https://docs.google.com/spreadsheets/d/1-waBJz8xQS3gf5UGPR_ynoIefoSTCmko0Xo_PAhcitY/edit
			String spreadsheetId = "1-waBJz8xQS3gf5UGPR_ynoIefoSTCmko0Xo_PAhcitY";
		
		
		
	
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, "A:A").execute();
		int numberOfRows = response.getValues().size();
		
		ValueRange valueRange = new ValueRange();
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		List<Object> row = new ArrayList<Object>();
	
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeZone = "GMT+2";

		
		
		row.add(isoFormat.format(convertToTimezone(timeZone,location.getDate())));
		row.add(String.valueOf(location.getLatitude()));
		row.add(String.valueOf(location.getLongitude()));
		row.add(location.getUser());
		row.add(timeZone);
		row.add(location.getStatus());
		row.add(location.getEmail());
		row.add(location.getImageUrl());
		values.add(row);
		
		//valueRange.setRange("sheet1!A2:B2");
		valueRange.setValues(values );
		//valueRange.setMajorDimension("ROWS");

		UpdateValuesResponse updateResponse = service.spreadsheets()
		.values()
		.update(spreadsheetId, "A"+ (numberOfRows + 1) /*+ ":B"*/, valueRange)
		.setValueInputOption("USER_ENTERED")//RAW
		.execute();
		
		Log.warn("TotalUpdatedCells: " + updateResponse.getUpdatedCells());
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
