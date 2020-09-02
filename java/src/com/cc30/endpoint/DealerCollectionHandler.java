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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.JSONObject;

/**
 * Servlet implementation class EndPoint
 */
@WebServlet("/client/staging/dealers/*")
public class DealerCollectionHandler extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Client client = JerseyClientBuilder.createClient();
	
	private String ClientId = "";
	private String APIKey = "";
	private String APIDomain = "https://stagingapi.coolcalc.com";
	private String LocalPathSegments = "/coolcalc/client";
	
    /**
     * @see HttpServlet#HttpServlet()
     * 
     * DealerCollectionHandler implements the entry point on your local server for all HTTP requests to .../dealers/:dealerId/...
     * The CoolCalc JavaScript library automatically calls this entry point based on events and user actions in the CoolCalc MJ8 UI.
     * Essentially this servlet forwards all requests from the browser to the CoolCalc API and all responses from the API back to the browser.
     * 
     */
    public DealerCollectionHandler() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    // coolcalcApiURL re-writes the URL as requested by the browser to the corresponding CoolCalc 
    // API URL by replacing the top level domain and the URL segments to our local entry point.
    private String coolcalcApiURL(HttpServletRequest request) {
    	
    	// To-do:
    	// Implement your own code here to check that the accountNr in the URL /dealers/accountNr/.... 
    	// corresponds to the current user/session info.
    	// This is to prevent some dishonest user from accessing someone else's project list.
    	// ...
    	// If the account nr in the REST URL does not correspond to the session user, respond with a 401 "are you trying to hack me" code.
    	// ...
    	
    	// Remove URL segments specific to our local entry point.
    	String apiPath = request.getRequestURI().replace(LocalPathSegments,"");
    	
    	// Construct the CoolCalc API URL.
    	String uri = APIDomain + apiPath;
    	if (request.getQueryString() != null) {
            uri += "?" + request.getQueryString();
    	}
    	    	
    	return uri;
    }
    
    // coolcalcAuthentication returns a Base64 encoded HTTP Basic authentication string.
    private String coolcalcAuthentication() {
    	Base64.Encoder encoder = Base64.getEncoder();
    	String normalString = ClientId + ":" + APIKey;
    	String encodedString = encoder.encodeToString( 
    	        normalString.getBytes(StandardCharsets.UTF_8) );
        return encodedString;
    }

    // MJ8Preview renders a custom "MJ8Preview" REST resource.
    // The MJ8Preview resource is a resource we create locally, not part of the CoolCalc API proper.
    // To create the MJ8Preview resource we download the HVACSystem resource from the CoolCalc API and copy some of its attributes.
    protected void MJ8Preview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	// Manually construct the URL for the HVAC system resource.
		String MJ8ReportURL = coolcalcApiURL(request);
		String myURL = MJ8ReportURL.substring(0, MJ8ReportURL.indexOf("/MJ8Report"));
		
		// Load the HVAC system resource from the CoolCalc API.
		String jsonStr = client.target(myURL)
			.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.TRUE)
	        .request(MediaType.WILDCARD)
            .header("Authorization", "Basic " + coolcalcAuthentication())
	        .get(String.class);
		
		// Parse JSON response from API.
		JSONObject APIResponse = new JSONObject(jsonStr);
		
		// Create the MJ8 preview data.
		JSONObject MJ8Preview = new JSONObject();
		MJ8Preview.put("fenestrationLoads", APIResponse.getJSONObject("HVACSystem").getJSONObject("fenestrationLoads"));
		MJ8Preview.put("heatingCoolingLoads", APIResponse.getJSONObject("HVACSystem").getJSONObject("heatingCoolingLoads"));
		MJ8Preview.put("loadCalculation", APIResponse.getJSONObject("HVACSystem").getJSONObject("loadCalculation"));
		
		// Now create a local REST resource copying only the attributes we are interested in.
		JSONObject localResource = new JSONObject();
		localResource.put("links", APIResponse.get("links"));
		localResource.put("navTrail", APIResponse.get("navTrail"));
		localResource.put("MJ8Preview", MJ8Preview);
		
		// And finally, output to the client.
    	response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(localResource.toString());
		    
    }
    
    // MJ8PreviewAlt renders a custom "MJ8PreviewAlt" REST resource.
    // The MJ8Preview resource is a resource we create locally, not part of the CoolCalc API proper.
    // To create the MJ8Preview resource we download the MJ8Project resource from the CoolCalc API and copy a few of its attributes.
    protected void MJ8PreviewAlt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	// Manually construct the URL for the MJ8Project resource.
		String MJ8ProjectURL = coolcalcApiURL(request);
		String myURL = MJ8ProjectURL.substring(0, MJ8ProjectURL.indexOf("/MJ8Reports"));
		
		// Load the MJ8Project resource from the CoolCalc API.
		String jsonStr = client.target(myURL)
			.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.TRUE)
	        .request(MediaType.WILDCARD)
            .header("Authorization", "Basic " + coolcalcAuthentication())
	        .get(String.class);
		
		// Parse JSON response from API.
		JSONObject APIResponse = new JSONObject(jsonStr);
			
		// Create a local REST resource copying only the attributes we are interested in.
		JSONObject localResource = new JSONObject();
		localResource.put("links", APIResponse.get("links"));
		localResource.put("navTrail", APIResponse.get("navTrail"));
		localResource.put("MJ8PreviewAlt", "This is just a placeholder");
		
		// And finally, output to the client.
    	response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(localResource.toString());
		    
    }
    
	/**
	 * This is the normal flow to call the CoolCalc API and forward its response to our client.
	 * Whatever the original request was from the browser to our local server is repeated to the CoolCalc API
	 * and the CoolCalc API's response is forwarded unchanged to the browser. 
	 */
    protected void coolcalcGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    // Get data from CoolCalc API.
		String myURL = coolcalcApiURL(request);
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
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * For all requests except those involving an MJ8Report resource, we delegate to function coolcalcGet.
	 * If the requests is for an MJ8Report resource, we check if the user is logged in and if not,
	 * we delegate to the MJ8Preview or MJ8PreviewAlt functions, depending on the originally requested URL.
	 * In the user interface there are multiple paths to navigate to the MJ8Report resource,
	 * the details of the "preview" resource we can render depend on the originally requested URL.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (false) {		// In production you need to check if the user is authenticated here.
			this.coolcalcGet(request, response);
		} else {
			
			// Check if the requested URL includes "MJ8Reports" or "MJ8Report" segment
			String[] URLSegments = request.getPathInfo().split("/");
			if (ArrayUtils.contains(URLSegments, "MJ8Reports")) {
    			String lastSegment = URLSegments[URLSegments.length-1];
    			if (lastSegment.equals("MJ8Reports")) {
    				this.coolcalcGet(request, response);
    			} else {
    				this.MJ8PreviewAlt(request, response);
    			} 
    		} else if (ArrayUtils.contains(URLSegments, "MJ8Report")) {
    			this.MJ8Preview(request, response);
			} else {
			    this.coolcalcGet(request, response);
    		}
			
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Payload from the browser.
        String myPayload = IOUtils.toString(request.getReader());
        
		// Post data to CoolCalc API and read the response.
		String myURL = coolcalcApiURL(request);
		Response mydata = client.target(myURL)
		        .request(MediaType.WILDCARD)
                .header("Authorization", "Basic " + coolcalcAuthentication())
		        .post(Entity.entity(myPayload, MediaType.APPLICATION_JSON), Response.class);
		
		// Output to the browser.
		response.setStatus(mydata.getStatus());
		response.setHeader("Content-Type", mydata.getHeaderString("Content-Type"));
		response.setHeader("Allow", mydata.getHeaderString("Allow"));
		response.setHeader("Location", mydata.getHeaderString("Location"));
	    response.getWriter().write(mydata.readEntity(String.class));
	    
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Payload from the browser.
        String myPayload = IOUtils.toString(request.getReader());
        
		// Post data to CoolCalc API and read the response.
		String myURL = coolcalcApiURL(request);
		Response mydata = client.target(myURL)
		        .request(MediaType.WILDCARD)
                .header("Authorization", "Basic " + coolcalcAuthentication())
		        .put(Entity.entity(myPayload, MediaType.APPLICATION_JSON), Response.class);
		
		// Output to the browser.
		response.setStatus(mydata.getStatus());
		response.setHeader("Content-Type", mydata.getHeaderString("Content-Type"));
		response.setHeader("Allow", mydata.getHeaderString("Allow"));
	    response.getWriter().write(mydata.readEntity(String.class));
	    
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Send the DELETE request to the CoolCalc API.
		String myURL = coolcalcApiURL(request);
		Response mydata = client.target(myURL)
		        .request(MediaType.WILDCARD)
                .header("Authorization", "Basic " + coolcalcAuthentication())
		        .delete();
		
		// Forward response code to the browser.
		response.setStatus(mydata.getStatus());
	}

}
