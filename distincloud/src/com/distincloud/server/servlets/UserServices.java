package com.distincloud.server.servlets;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


import com.distincloud.server.DistincloudEngine;
import com.distincloud.server.data.User;
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
	public String getClichedMessage(@PathParam("username") String username , @Context HttpServletRequest request) {
		return "storage for "+username;
	}
} 
