using ASP.server_side;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Web;

namespace ASP.coolcalc.client.staging
{
    /// <summary>
    /// This is the main "forwarding" script that forwards requests from the client (browser)
    /// to the REST API for the /dealers/dealerId/... entry point.
    /// It is our responsability to verify that the dealerId URL segment provided corresponds to actual session data.
    /// Here we only send/receive JSON or GeoJSON, no other content types are accepted.
    /// </summary>
    
    public class dealers : IHttpHandler
    {

        public void ProcessRequest(HttpContext context)
        {

            string result = string.Empty;
            Dictionary<string, string> my_headers = new Dictionary<string, string>();

            // Substitute the REST API's domain in the request URI so we can forward the request.
            string my_path = context.Request.ServerVariables["REQUEST_URI"].Replace(ConfigurationManager.AppSettings["path_to_client"], "");
            string my_url = "https://" + ConfigurationManager.AppSettings["rest_api_server"] + my_path;

            // To-do:
            // Implement your own code here to check that the accountNr in the URL /dealers/accountNr/.... 
            // corresponds to the current user/session info.
            // This is to prevent some dishonest user from accessing someone else's project list.
            // ...
            // If the account nr in the REST URL does not correspond to the session user, respond with a 401 "are you trying to hack me" code.
            // ...

            // Instantiate APIClient library.
            APIClient API = new APIClient();
           
            //Get content as string
            var bodyStream = new StreamReader(context.Request.InputStream);
            bodyStream.BaseStream.Seek(0, SeekOrigin.Begin);
            var json_data = bodyStream.ReadToEnd();

            switch (context.Request.HttpMethod)
            {
                case "OPTIONS":
                    result = API.OPTIONS(my_url);
                    break;
                case "GET":
                    my_headers.Add("Accept", "application/json,application/vnd.geo+json");
                    result = API.GET(my_url, "", my_headers);
                    break;
                case "POST":
                    my_headers.Add("Content-Type", "application/json");
                    my_headers.Add("Accept", "application/json,application/vnd.geo+json");
                    result = API.POST(my_url, json_data, my_headers);
                    break;
                case "PUT":
                    my_headers.Add("Content-Type", "application/json");
                    my_headers.Add("Accept", "application/json,application/vnd.geo+json");
                    result = API.PUT(my_url, json_data, my_headers);
                    break;
                case "DELETE":
                    result = API.DELETE(my_url);
                    break;
            }

            // Set HTTP response code.
            context.Response.StatusCode = API.response_code;

            // For POST requests, forward HTTP "Location" header if sent by REST API to the client.
            // HTTP "Location" header can be used by the client to download a newly created resource after a POST request (add to collection).
            if (context.Request.HttpMethod == "POST" && API.response_headers.ContainsKey("Location"))
            {
                context.Response.AddHeader("Location", API.response_headers["Location"]);
            }

            // Forward HTTP "Allow" header to the client.
            // HTTP "Allow" header is used by the client to render form controls (save, delete, etc) to a resource.
            if (API.response_headers.ContainsKey("Allow"))
            {
                context.Response.AddHeader("Allow", API.response_headers["Allow"]);
            }

            // Output to client.
            // Check server response Content-Type header.
            if (context.Response.StatusCode != 204)
            {
                if (API.response_headers["Content-Type"] == "application/json" || API.response_headers["Content-Type"] == "application/vnd.geo+json")
                {
                    context.Response.AddHeader("Content-Type", API.response_headers["Content-Type"]);
                    context.Response.Charset = "";
                    context.Response.Write(result);
                }
                else
                {
                    context.Response.StatusCode = 500;
                    context.Response.Write("<h3>Well this isn\'t supposed to happen now, is it?</h3>");
                }

            }
        }

        public bool IsReusable
        {
            get
            {
                return false;
            }
        }
    }
}