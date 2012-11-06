package com.distincloud.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;


import com.distincloud.server.DistincloudEngine;
import com.distincloud.server.MockService;

/**
 * @author jules
 * covers B.3 REF
 * GET /users/[user_id]/storage/
 * lists the different comparisons made by a user
 */

public class StorageServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String query = req.getQueryString();
		String requestURI = req.getRequestURI();
		
		// DistincloudEngine de = DistincloudEngine.getInstance();
		/*
		try {
			JSONObject jsonQuery = new JSONObject(query);
			jsonQuery.get()
		} catch (JSONException e) {
			e.printStackTrace();
		}
		*/
		PrintWriter out = resp.getWriter();
		out.println(requestURI);		
		out.flush();
	}
	
}
