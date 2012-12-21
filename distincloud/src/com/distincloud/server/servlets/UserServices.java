package com.distincloud.server.servlets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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


	public UserServices() {
	}

	@GET
	@Path("/")
	@Produces("text/plain")
	public String getUsersAsJsonArray() {
		Output out = new Output("getUsersAsJsonArray", true);
		for(User current : _engine.fetchUserList() ) {		
			JSONObject response = new JSONObject();
			try {
				response.put("username", current.getUsername());
				response.put("userKey", current.getKey());	
				response.put("storageSize", current.getWCRList().size());	
				out.addResponseAsJSON(response);
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}		
		return out.toString();
	}

	@POST
	@Path("/debug/")
	@Consumes("text/plain")
	public String printJSON(String queryContent, @Context HttpHeaders hh) {	
		try {
			JSONObject j = new JSONObject(queryContent);
			System.out.println(j.get("data"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
		System.out.println(headerParams.getFirst("usrKey"));
		return queryContent;
	}

	@GET
	@Path("/{username}/")
	@Produces("text/plain")
	public String infosForUser( @PathParam("username") String username , @Context HttpHeaders hh ) {
		MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
		User current = _engine.checkExistanceOf(username);
		Output out;
		if( current != null && current.getKey().matches(headerParams.getFirst("usrKey")) ) {
			out = new Output("USER_INFO", true);
			JSONObject response = new JSONObject();
			try {
				response.put("username", current.getUsername());
				response.put("userKey", current.getKey());
				response.put("storage", "/resources/users/"+username+"/storage/");
				out.addResponseAsJSON(response);
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}
		else out = new Output("USER_INFO", false);			
		return out.toString();
	}

	@PUT
	@Path("/{username}/")
	@Produces("text/plain")
	public String requestUserCreation(@PathParam("username") String username) {
		String userCreatedNickname = _engine.requestUserCreation(username);
		if( !( userCreatedNickname.matches("null")) ) {		
			Output out = new Output("USER_CREATION", true);
			try {
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("username", userCreatedNickname);
				jsonResponse.put("userKey", _engine.fetchUserWithUSername(userCreatedNickname).getKey());
				jsonResponse.put("storage", "/resources/users/"+userCreatedNickname+"/storage/");
				out.addResponseAsJSON(jsonResponse);
				return out.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return new Output("USER_CREATION", false).toString();
	}

	@DELETE
	@Path("/{username}/")
	@Produces("text/plain")
	public String requestUserDeletion(@PathParam("username") String username,  @Context HttpHeaders hh ) {
		
		User userToDelete = _engine.checkExistanceOf(username);
		MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
		
		if( !( userToDelete == null) && userToDelete.getKey().matches(headerParams.getFirst("usrKey")) ) {		
			Output out = new Output("USER_DELETION", true);
			_engine.deleteUser(userToDelete);
			return out.toString();
		}
		return new Output("USER_DELETION", false).toString();
	}


	@GET
	@Path("/{username}/storage")
	@Produces("text/plain")
	public String getStorageContentList( @PathParam("username") String username , @Context HttpHeaders hh ) {
		MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
		User current = _engine.checkExistanceOf(username);
		Output out;
		if( current != null && current.getKey().matches(headerParams.getFirst("usrKey")) ) {
			out = new Output("STORAGE_DISPLAY", true);
			List<JSONObject> response = new ArrayList<JSONObject>();
			for(WordComparisonResult wcr : current.getWCRList()) {
				JSONObject toadd = new JSONObject();
				try {
					toadd.put("uri", "/users/"+username+"/storage/"+wcr.getKey());
					response.add(toadd);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			out.addResponseAsJSONList(response);
		}
		else out = new Output("STORAGE_DISPLAY", false);		
		return out.toString();
	}

	@GET
	@Path("/{username}/storage/{key}")
	@Produces("text/plain")
	public String getWCRWithKey( @PathParam("username") String username , @PathParam("key") String key , @Context HttpHeaders hh ) {

		MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
		User current = _engine.checkExistanceOf(username);
		WordComparisonResult wcr = null;
		Output out;

		if( current != null ) wcr = current.getWCR(key);	
		if( current != null && current.getKey().matches(headerParams.getFirst("usrKey")) && wcr != null) {
			out = new Output("WCR_DISPLAY", true);
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
		}
		else out = new Output("WCR_DISPLAY", false);
		return out.toString();
	}

	@PUT
	@Consumes("text/plain")
	@Path("/{username}/ontologies/{onto_id}/relatedness/") 
	public String createRelatednessResult(@PathParam("username") String username , @Context HttpHeaders hh , String queryContent) {

		MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
		User current = _engine.checkExistanceOf(username);
		Output out;
		JSONObject jso;
		String wcrKey = "null";

		if( current != null && current.getKey().matches(headerParams.getFirst("usrKey")) ) {
			System.out.println("1. Matched user");
			try {
				jso = new JSONObject(queryContent);
				JSONArray request = jso.getJSONArray("request");
				String word1 = (String) request.getJSONObject(0).get("word1");
				String word2 = (String) request.getJSONObject(0).get("word2");
				int maxRelatedness =  (Integer) request.getJSONObject(0).get("maxRelatedness");
				wcrKey = _engine.proceed(_engine.checkExistanceOf(username), maxRelatedness, word1, word2);
				System.out.println("2. _engine.proceed performed");
				if(wcrKey.matches("null")) {
					System.out.println("3. wcrKey.matches(null)");
					return new Output("WCR_CREATED", false).toString();
				}
				else {
					System.out.println("4. creation Ok");
					out = new Output("WCR_CREATED", true);
					JSONObject response = new JSONObject();
					response.put("uri", "/users/"+username+"/storage/"+wcrKey);
					out.addResponseAsJSON(response);
					return out.toString();
				}
			} catch (JSONException e) {
				System.out.println("5. JSON parsing error");
				e.printStackTrace();
			}
		}
		return new Output("WCR_CREATED", false).toString();
	}
} 
