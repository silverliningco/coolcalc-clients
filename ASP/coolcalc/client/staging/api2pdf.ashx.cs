using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;

namespace ASP.coolcalc.client.staging
{
    /// <summary>
    /// Descripción breve de api2pdf
    /// </summary>
    public class api2pdf : IHttpHandler
    {

        public void ProcessRequest(HttpContext context)
        {
            // Implement your own method to Check if the user has visited an HTML page prior to requesting this resource, Because this page is open for any in internet.
            // ...

            // Read input data from POST request.
            if (context.Request.HttpMethod == "POST")
            {
                //Get content.
                var bodyStream = new StreamReader(context.Request.InputStream);
                bodyStream.BaseStream.Seek(0, SeekOrigin.Begin);
                var json_data = bodyStream.ReadToEnd();
                dynamic decoded_data = JsonConvert.DeserializeObject(json_data);

                // Check link parameter
                if (decoded_data.ContainsKey("HTMLURL"))
                {

                    // Setup request some variables. 
                    string api_url = "https://v2.api2pdf.com/chrome/pdf/url";
                    string api_key = "";

                    string payload = JsonConvert.SerializeObject(
                    new
                    {
                        url = decoded_data.HTMLURL,
                        inline = false,
                        fileName = "coolcalc-mj8-report.pdf",
                        options = new
                        {
                            delay = 4000
                        }
                    });

                    // Create a request for the URL.
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(api_url);

                    // Pass header variable in curl method
                    request.Accept = "application/json";
                    request.Headers["Authorization"] = api_key;

                    // Send json via POST.
                    request.Method = "POST";
                    request.SendChunked = true;
                    request.ContentLength = payload.Length;
                    using (var streamWriter = new StreamWriter(request.GetRequestStream()))
                    {
                        streamWriter.Write(payload);
                        streamWriter.Flush();
                    }

                    // Receive server response
                    try
                    {
                        using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                        {
                            // Get the stream containing content returned by the server.
                            using (Stream dataStream = response.GetResponseStream())
                            {
                                using (StreamReader reader = new StreamReader(dataStream))
                                {
                                    string server_output = reader.ReadToEnd();

                                    context.Response.AddHeader("Content-Type", "application/json");
                                    context.Response.Charset = "";
                                    context.Response.Write(server_output);
                                }
                            }
                        }
                    }
                    catch (System.Net.WebException ex)
                    {
                        var response = ex.Response as HttpWebResponse;
                        
                        // Get the stream containing content returned by the server.
                        using (Stream dataStream = response.GetResponseStream())
                        {
                            using (StreamReader reader = new StreamReader(dataStream))
                            {
                                string server_output = reader.ReadToEnd();

                                context.Response.AddHeader("Content-Type", "application/json");
                                context.Response.Charset = "";
                                context.Response.Write(server_output);
                                context.Response.StatusCode = (int)response.StatusCode;
                            }
                        }
                        
                    }
                }
                else
                {
                    context.Response.StatusCode = 400;
                    context.Response.Write("<h3>PDF URL is required.</h3>");

                }
            }
            else
            {
                context.Response.StatusCode = 405;
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