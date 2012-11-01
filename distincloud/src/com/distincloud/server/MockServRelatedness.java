package com.distincloud.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockServRelatedness extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String word1 = req.getParameter("word1");
		String word2 = req.getParameter("word2");
		MockService _ms  = MockService.getInstance();
		String response;
		if (word1 != null && word2 != null && req.getParameter("maxRel") != null) response = String.valueOf(_ms.calculateRelatedness(Integer.decode(req.getParameter("maxRel")), word1, word2));
		else response = "syntaxe error, need parameters : word1, word2, maxRel";
		out.println(response);		
		out.flush();
	}
	
}
