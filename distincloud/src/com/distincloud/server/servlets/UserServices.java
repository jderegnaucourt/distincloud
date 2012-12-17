package com.distincloud.server.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


import com.distincloud.server.DistincloudEngine;
import com.distincloud.server.data.User;
import com.distincloud.server.data.WordComparisonResult;
import com.distincloud.server.tools.Output;

/**
 * @author jules
 */

@Path("/users/")
public class UserServices {

	@Context UriInfo uriInfo;
	protected DistincloudEngine _engine = DistincloudEngine.getInstance();

	@GET
	@Path("/")
	@Produces("text/plain")
	public String getUsersAsJsonArray() {
		Output out = new Output("getUsersAsJsonArray", true);
		for(User current : _engine.fetchUserList() ) {		
			JSONObject response = new JSONObject();
			try {
				response.put("username", current.getUsername());
				response.put("key", current.getKey());	
				out.addResponseAsJSON(response);
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}		
		return out.toString();
	}

	@PUT
	@Path("/{username}/")
	@Produces("application/json")
	public JSONObject requestUserCreation(@PathParam("username") String username) {
		String key = _engine.requestUserCreation(username);
		JSONObject jsonResponse = new JSONObject();
		try {
			if(key != "null") {
				jsonResponse.put("bUserCreated", true);
				jsonResponse.append("userKey", key);
			}
			else jsonResponse.put("bUserCreated", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResponse;
	}

	@GET
	@Path("/{username}/")
	@Produces("text/plain")
	public String infosForUser(@PathParam("username") String username ) {
		User current = _engine.checkExistanceOf(username);
		Output out;
		if(current == null) {
			out = new Output("infosForUser", false);
		}
		else {
			out = new Output("infosForUser", true);
			JSONObject response = new JSONObject();
			try {
				response.put("username", current.getUsername());
				response.put("key", current.getKey());	
				out.addResponseAsJSON(response);
			} catch (JSONException e) {
				e.printStackTrace();
			}				
		}
		return out.toString();
	}


	@GET
	@Path("/{username}/storage")
	@Produces("text/plain")
	public String getStorageContentList(@PathParam("username") String username , @Context HttpServletRequest request) {
		User usr = _engine.checkExistanceOf(username);
		Output out = new Output("WCR_CREATED", true);
		List<JSONObject> response = new ArrayList<JSONObject>();
		
		for(WordComparisonResult wcr : usr.getWCRList()) {
			JSONObject toadd = new JSONObject();
			try {
				toadd.put("uri", "/users/"+username+"/storage/"+wcr.getKey());
				response.add(toadd);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		out.addResponseAsJSONList(response);
		return out.toString();
	}
	
	@GET
	@Path("/{username}/storage/{key}")
	@Produces("text/plain")
	public String getWCRWithKey(@PathParam("username") String username , @PathParam("key") String key) {
		User usr = _engine.checkExistanceOf(username);
		WordComparisonResult wcr = usr.getWCR(key);
		
		Output out = new Output("WCR_CREATED", true);
		JSONObject response = new JSONObject();
		try {
			response.put("uri", "/users/"+username+"/storage/"+key);
			response.put("word1", wcr.getWord1());
			response.put("word2", wcr.getWord2());
			response.put("relatedness", wcr.getRelatedness());
			response.put("maxRelatedness", wcr.getMaxRelatedness());
			out.addResponseAsJSON(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return out.toString();
	}

	@PUT
	@Path("/users/{username}/ontologies/{onto_id}/relatedness/") 
	public String createRelatednessResult(@PathParam("username") String username ,@Context HttpServletRequest req) {
		JSONObject jso;
		String wcrKey = "null";
		try {
			jso = new JSONObject(buff(req));
			JSONArray infos = jso.getJSONArray("infos");
			JSONArray request = jso.getJSONArray("request");
			String usrKey = (String) infos.getJSONObject(0).get("usrKey");
			String word1 = (String) request.getJSONObject(0).get("word1");
			String word2 = (String) request.getJSONObject(0).get("word2");
			String maxRelatedness = (String) request.getJSONObject(0).get("maxRelatedness");
			wcrKey = _engine.proceed(_engine.checkExistanceOf(username), Integer.decode(maxRelatedness), word1, word2);

			if(wcrKey.matches("null")) return new Output("WCR_CREATED", false).toString();
			else {
				Output out = new Output("WCR_CREATED", true);
				JSONObject response = new JSONObject();
				response.put("uri", "/users/"+_engine.usernameForKey(usrKey)+"/storage/"+wcrKey);
				out.addResponseAsJSON(response);
				return out.toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new Output("WCR_CREATED", false).toString();
	}


	public String buff(HttpServletRequest req) {
		BufferedReader reader;
		try {
			reader = req.getReader();
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				sb.append(line + "\n");
				line = reader.readLine();
			}
			reader.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "null";
	}
} 
