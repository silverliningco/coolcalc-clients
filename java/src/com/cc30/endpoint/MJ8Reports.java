package com.cc30.endpoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;

/**
 * Servlet implementation class MJ8Reports
 */
@WebServlet("/client/staging/MJ8Reports")
public class MJ8Reports extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Client client = JerseyClientBuilder.createClient();
	
	private String ClientId = "";
	private String APIKey = "";
	private String APIUrl = "https://stagingapi.coolcalc.com/staging/MJ8Reports/?reportId=";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MJ8Reports() {
        super();
        // TODO Auto-generated constructor stub
    }
   
    // coolcalcAuthentication returns a Base64 encoded HTTP Basic authentication string.
    private String coolcalcAuthentication() {
    	Base64.Encoder encoder = Base64.getEncoder();
    	String normalString = ClientId + ":" + APIKey;
    	String encodedString = encoder.encodeToString( 
    	        normalString.getBytes(StandardCharsets.UTF_8) );
        return encodedString;
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// The URL for the MJ8 report at the CoolCalc API.
		// MJ8 report ids are not guessable so they can be safely retrieved outside the normal UI flow.
		String myURL = APIUrl;
		myURL += request.getParameter("reportId");
		myURL += "&rev=latest";
		
		// Get data from CoolCalc API.
		Response mydata = client.target(myURL)
				.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.TRUE)
		        .request(MediaType.WILDCARD)
                .header("Authorization", "Basic " + coolcalcAuthentication())
		        .get(Response.class);
		
		// Output to the browser.
		response.setStatus(mydata.getStatus());
		response.setHeader("Content-Type", mydata.getHeaderString("Content-Type"));
		response.setHeader("Allow", mydata.getHeaderString("Allow"));
	    response.getWriter().write(mydata.readEntity(String.class));
	    
	}

}
