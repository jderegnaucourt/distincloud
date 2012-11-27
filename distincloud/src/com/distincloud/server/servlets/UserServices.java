package com.distincloud.server.servlets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author jules
 * covers B.3 REF
 * GET /users/[user_id]/storage/
 * lists the different comparisons made by a user
 */

@Path("/users/")
public class UserServices {
	
	@GET
	@Path("/")
	@Produces("text/plain")
	public String listAllUsers(@PathParam("username") String username ) {
		return "list all users";
	}
	
	@GET
	@Path("/{username}/")
	@Produces("text/plain")
	public String infosForUser(@PathParam("username") String username ) {
		return "infos about user "+username;
	}

	@GET
	@Path("/{username}/storage")
	@Produces("text/plain")
	public String getClichedMessage(@PathParam("username") String username ) {
		return "storage for "+username;
	}
} 
