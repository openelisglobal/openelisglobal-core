package us.mn.state.health.lims.datasubmission;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import us.mn.state.health.lims.common.util.validator.GenericValidator;
import us.mn.state.health.lims.datasubmission.valueholder.DataIndicator;
import us.mn.state.health.lims.datasubmission.valueholder.DataValue;
import us.mn.state.health.lims.siteinformation.dao.SiteInformationDAO;
import us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDAOImpl;

public class DataSubmitter {
	
	public static boolean sendDataIndicator(DataIndicator indicator) throws IOException, ParseException {
		for (String tableName : indicator.getDataValueTableNames()) {
			Map<String, String> values = new HashMap<String, String>();
			addKeyValuesForTable(tableName, values, indicator);
			String id = checkForIdOnVLDash(tableName, values);
			
			for (DataValue value : indicator.getDataValuesByTable(tableName)) {
				values.put(value.getForeignColumnName(), value.getValue());
			}
			String result;
			JSONObject jsonResult;
			if (!GenericValidator.isBlankOrNull(id)) {
				result = sendJSONPut(tableName, id, values);
				jsonResult = (JSONObject) (new JSONParser()).parse(result);
			} else {
				result = sendJSONPost(tableName, values);
				jsonResult = (JSONObject) (new JSONParser()).parse(result);
				id = (String) ((JSONObject) jsonResult.get("message")).get("id");
				for (DataValue value : indicator.getDataValuesByTable(tableName)) {
					value.setForeignId(id);
				}
			}
			if ("0".equals(jsonResult.get("success"))) {
				return false;
			}
		}
		
		return true;
	}
	
	//add values to provide enough information to identify an entry without knowing the foreign id value
	//TO DO verify that all entries work as a uniquely identifying foreign key 
	private static void addKeyValuesForTable(String tableName, Map<String, String> values, DataIndicator indicator) {
		switch (tableName) {
		case "facilitys":
			//TO DO add correct facility code
			values.put("facilitycode", "1");
			break;
		case "vl_national_summary":
			values.put("month", Integer.toString(indicator.getMonth() + 1));
			values.put("year", Integer.toString(indicator.getYear()));
			break;
		case "vl_site_summary":
			values.put("month", Integer.toString(indicator.getMonth() + 1));
			values.put("year", Integer.toString(indicator.getYear()));
			//temporary
			values.put("facility", "1");
			break;
		case "vl_site_suppression":
			//temporary
			values.put("facility", "1");
			break;
		default:
			break;
		}
	}
	
	//returns the id of an entry that matches the specified column-value pairs
	public static String checkForIdOnVLDash(String table, Map<String, String> columnValues) throws ClientProtocolException, IOException, ParseException {
		if (columnValues.isEmpty()) {
			return null;
		}
		String result = sendGet(table, columnValues);
		JSONObject jsonResult = (JSONObject) (new JSONParser()).parse(result);
		JSONArray message = (JSONArray) jsonResult.get("message");
		if (message.size() == 0) {
			return null;
		} else {
			JSONObject obj = (JSONObject) message.get(0);
			if (obj.containsKey("id")) {
				return (String) obj.get("id");
			} else if (obj.containsKey("ID")) {
				return (String) obj.get("ID");
			} 
			return null;
		}
	}
	
	//get a resource based on its column-value pairs.
	public static String sendGet(String table, Map<String, String> columnValues) throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		StringBuilder url = new StringBuilder();
		url.append(getBaseURL());
		url.append(table);
		url.append("?");
		String prefix = "";
		for (String keyName : columnValues.keySet()) {
			url.append(prefix);
			url.append(keyName);
			url.append("=");
			url.append(columnValues.get(keyName));
			prefix = "&";
		}
		HttpGet request = new HttpGet(url.toString());
		request.setHeader("Accept", "application/json");
		
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}
		
		String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		System.out.println(body);
		return body;
	}
	
	//get a resource based on its column-value pairs.
	public static String sendGet(String table, String id) throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		StringBuilder url = new StringBuilder();
		url.append(getBaseURL());
		url.append(table);
		url.append("/");
		url.append(id);
		HttpGet request = new HttpGet(url.toString());
		request.setHeader("Accept", "application/json");
		
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}
		
		String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		System.out.println(body);
		return body;
	}
	
	//used for talking to VL-DASHBOARD api to update an old entry
	public static String sendJSONPut(String table, String foreignKey, Map<String, String> values) throws IOException { 
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut request = new HttpPut(getBaseURL() + "/" + table + "/" + foreignKey);
		StringEntity entity = new StringEntity(createJSONString(values));
		entity.setContentType("application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}
		
		String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		System.out.println(body);
		return body;
	}

	//used for talking to VL-DASHBOARD api to insert a new entry
	public static String sendJSONPost(String table, Map<String, String> values) throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(getBaseURL() + "/" + table);
		StringEntity entity = new StringEntity(createJSONString(values));
		entity.setContentType("application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}
		
		String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		System.out.println(body);
		return body;
	}

	private static String createJSONString(Map<String, String> values) {
		JSONObject json = new JSONObject();
		json.putAll(values);
		
		// TODO Auto-generated method stub
		return json.toString();
	}
	
	private static String getBaseURL() {
		SiteInformationDAO siteInfoDAO = new SiteInformationDAOImpl();
		String url = siteInfoDAO.getSiteInformationByName("Data Sub URL").getValue();
		return url;
	} 

}
