package com.turbo_license.rest;

import java.io.StringWriter;
import java.sql.ResultSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;






@Path("ProductServiceResource")
public class ProductServiceResource {
	static Logger logger = Logger.getLogger(ProductServiceResource.class);

	@GET
    @Produces(MediaType.TEXT_PLAIN)
	//@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
    public String getMessage() {
		String jsonStr = "";
		jsonStr = "{\"lng\": \"1\", \"lat\": \"2\", \"email\": \"test@foo.com\", \"store_name\": \"test store\", \"product1\": \"tp1\", \"product2\": \"tp2\", \"product3\": \"\", \"product4\": \"\", \"product5\": \"\"}" ;
		logger.info("input para is " + jsonStr);
		JSONObject jInput = null;
		JSONObject jOutput = new JSONObject();
		JSONObject err = null;
		JSONArray errArr = new JSONArray();
		String lng = "", lat = "", email = "", storeName = "", product1 = "", product2 = "", product3 = "", product4 = "", product5 = "";
		String result = "";
		

		String sql = "";
		
		
		try {
			jInput = new JSONObject(jsonStr);
			lng = jInput.getString("lng");
			lat = jInput.getString("lat");
			email = jInput.getString("email");
			storeName = jInput.getString("store_name");
			product1 = jInput.getString("product1");
			product2 = jInput.getString("product2");
			product3 = jInput.getString("product3");
			product4 = jInput.getString("product4");
			product5 = jInput.getString("product5");
			if (lng == null || "".equals(lng.trim())) {
				err = new JSONObject();
				err.put("error", "lng must have a value");
				errArr.put(err);
			}
			if (lat == null || "".equals(lat.trim())) {
				err = new JSONObject();
				err.put("error", "lat must have a value");
				errArr.put(err);
			}
			if (email == null || "".equals(email.trim())) {
				err = new JSONObject();
				err.put("error", "email must have a value");
				errArr.put(err);
			}
			if (storeName == null || "".equals(storeName.trim())) {
				err = new JSONObject();
				err.put("error", "store name must have a value");
				errArr.put(err);
			}
			if (errArr.length() > 0) {
				jOutput.put("errors", errArr);
				jOutput.put("success", "N");
			} 
			
			sql = "insert into store_product values(nextval('store_product_id_seq')::regclass, '" +
					lng + "', '" +
					lat + "', '" +
					email + "', '" +
					storeName + "', '" +
					product1 + "', '" +
					product2 + "', '" +
					product3 + "', '" +
					product4 + "', '" +
					product5 + "')";
			jOutput.put("success", "Y");
			
			
			
			result = jOutput.toString();
		} catch (Exception e) {
			logger.error("json error: " + e.getMessage(), e);
			result = "{\"success\":\"N\", \"errors\":[{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}]}";
		}
		logger.info("input got " + jInput.toString());
		logger.info("output is " + result);
		

		return result;
		//return "Rest ever Sleeps";
	}
}
