using ASP.server_side;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;

namespace ASP.coolcalc.client.staging
{
    /// <summary>
    /// Descripción breve de mj8_reports
    /// </summary>
    public class mj8_reports : IHttpHandler
    {

        public void ProcessRequest(HttpContext context)
        {
            string result = string.Empty;
            Dictionary<string, string> my_headers = new Dictionary<string, string>();

            // Substitute the REST API's domain in the request URI so we can forward the request.
            string my_url = ConfigurationManager.AppSettings["mj8_report_url"] + context.Request.QueryString["reportId"] + "&rev=latest";

            // Instantiate APIClient library.
            APIClient API = new APIClient();

            my_headers.Add("Accept", "application/json,application/vnd.geo+json");
            result = API.GET(my_url, "", my_headers);

            // Output to client.
            // Check server response Content-Type header.
            if (context.Response.StatusCode == 200)
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