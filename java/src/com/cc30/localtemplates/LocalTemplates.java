package com.cc30.localtemplates;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;


/**
 * Servlet implementation class LocalTemplates
 */
@WebServlet("/LocalTemplates")
public class LocalTemplates extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LocalTemplates() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * This endpoint provides static HTML used for REST resources that we locally create,
	 * not part of the CoolCalc API proper.  We respond to the client with a JSON object 
	 * that maps resource names to the static HTML template for the resource.
	 * 
	 * For the purposes of this sample code we are hardcoding (2) HTML templates,
	 * if you have many local resources it may make more sense to read all static HTML
	 * files in a given directory on your server.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Read HTML templates for static resources/
	    String MJ8PreviewHTML = FileUtils.readFileToString(new File("C:\\Users\\Ward\\eclipse-workspace\\CC30\\WebContent\\local-assets\\static-html\\MJ8Preview.html"), "UTF-8");
	    String MJ8PreviewAltHTML = FileUtils.readFileToString(new File("C:\\Users\\Ward\\eclipse-workspace\\CC30\\WebContent\\local-assets\\static-html\\MJ8PreviewAlt.html"), "UTF-8");

	    // Create a JSON object mapping resource name to static HTML.
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("MJ8Preview", MJ8PreviewHTML);
	    map.put("MJ8PreviewAlt", MJ8PreviewAltHTML);
        JSONObject json = new JSONObject(map);
        
        // Output to client
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json.toString());
	}

}
