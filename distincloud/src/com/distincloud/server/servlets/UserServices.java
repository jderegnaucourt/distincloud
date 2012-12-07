package com.distincloud.server.servlets;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



import com.distincloud.server.DistincloudEngine;
import com.distincloud.server.data.User;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * @author jules
 * covers B.3 REF
 * GET /users/[user_id]/storage/
 * lists the different comparisons made by a user
 */

@Path("/users/")
public class UserServices {

	@Context UriInfo uriInfo;
	protected DistincloudEngine _engine = DistincloudEngine.getInstance();

	@GET
	@Path("/")
	@Produces("application/json")
	public JSONObject getUsersAsJsonArray() {
		JSONArray uriArray = new JSONArray();
		for (User userEntity : _engine.fetchUserList()) {
			UriBuilder ub = uriInfo.getAbsolutePathBuilder();
			URI userUri = ub.
			path(userEntity.getUsername()).
			build();
			uriArray.put(userUri.toASCIIString());
		}
		JSONObject json = new JSONObject();
		try {
			json.put("userCount", uriArray.length());
			json.append("usersList", uriArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
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
	@Produces("application/json")
	public User infosForUser(@PathParam("username") String username ) {
		User current = _engine.checkExistanceOf(username);
		return current;
	}
	
	@GET @Produces("application/json")
	@Path("/debug/creation/{username}/")
	public User creationDebug(@PathParam("username") String username ) {
		return new User(username, "pass");
	}

	@GET
	@Path("/{username}/storage")
	@Produces("text/plain")
	public String getClichedMessage(@PathParam("username") String username , @Context HttpServletRequest request) {
		return "storage for "+username;
	}
} 
