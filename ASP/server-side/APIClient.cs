using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;

namespace ASP.server_side
{
    public class APIClient
    {
        protected string server_authentication = string.Empty;
        protected string client_id = string.Empty;
        protected string client_key = string.Empty;

        public int response_code;
        public string response_status = string.Empty;
        public Dictionary<string, string> response_headers = new Dictionary<string, string>();

        public APIClient()

        {
            this.server_authentication = ConfigurationManager.AppSettings["server_authentication"];
            this.client_id = ConfigurationManager.AppSettings["client_id"];
            this.client_key = ConfigurationManager.AppSettings["client_key"];
        }


        public string call(string url, string query_string, string HTTP_method, string payload, Dictionary<string, string> headers)
        {
            string API_response = null;

            // URL query string.
            // These are traditional GET URL parameters, if any.
            if (query_string != "")
            {
                url += '?' + query_string;
            }

            // Create a request for the URL.
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = HTTP_method;
            
            // Add headers to request.
            foreach (KeyValuePair<string, string> header in headers)
            {
                switch (header.Key)
                {
                    case "Accept":
                        request.Accept = header.Value;
                        break;
                    case "Content-Type":
                        request.ContentType = header.Value;
                        break;
                    default:
                        // Do nothing.
                        break;
                }
            }

            // HTTP basic authentication.
            if (this.server_authentication == "HTTP_basic")
            {
                NetworkCredential networkCredential = new NetworkCredential(this.client_id, this.client_key);
                CredentialCache myCredentialCache = new CredentialCache { { new Uri(url), "Basic", networkCredential } };
                request.PreAuthenticate = true;
                request.Credentials = myCredentialCache;

            }

            // Add payload.
            if (HTTP_method == "POST" || HTTP_method == "PUT")
            {
                request.SendChunked = true;
                request.ContentLength = payload.Length;

                using (var streamWriter = new StreamWriter(request.GetRequestStream()))
                {
                    streamWriter.Write(payload);
                    streamWriter.Flush();
                }
            }


            // Get API Response.
            try
            {
                using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                {
                    // Get the stream containing content returned by the server.
                    using (Stream dataStream = response.GetResponseStream())
                    {
                        using (StreamReader reader = new StreamReader(dataStream))
                        {
                            string responseFromServer = reader.ReadToEnd();
                            API_response = responseFromServer;

                            // Add response headers.
                            for (int i = 0; i < response.Headers.Count; ++i)
                            {
                                string header = response.Headers.GetKey(i);
                                foreach (string value in response.Headers.GetValues(i))
                                {
                                    this.response_headers.Add(header, value);
                                }
                            }

                            // Add response Code.
                            this.response_code = Convert.ToInt32(response.StatusCode);
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
                        string responseFromServer = reader.ReadToEnd();
                        API_response = responseFromServer;

                        // Add response headers.
                        for (int i = 0; i < response.Headers.Count; ++i)
                        {
                            string header = response.Headers.GetKey(i);
                            foreach (string value in response.Headers.GetValues(i))
                            {
                                this.response_headers.Add(header, value);
                            }
                        }

                        // Add response Code.
                        this.response_code = (int)response.StatusCode;
                    }
                }

            }
            return API_response;
        }


        // DELETE completes an HTTP DELETE request to the API server.
        public string DELETE(string REST_url = "")
        {
            return this.call(REST_url, "", "DELETE", null, null);
        }


        // GET completes an HTTP GET request to the API server and returns its response to the client.
        public string GET(string REST_url = "", string query_string = "", Dictionary<string, string> headers = null)
        {
            return this.call(REST_url, query_string, "GET", null, headers);
        }


        // OPTIONS completes an HTTP OPTIONS request to the API server and returns its response to the client.
        public string OPTIONS(string REST_url = "")
        {
            return this.call(REST_url, "", "OPTIONS", null, null);
        }


        // POST completes an HTTP POST request to the API server and returns its response to the client.
        public string POST(string REST_url = "", string payload = null, Dictionary<string, string> headers = null)
        {
            return this.call(REST_url, "", "POST", payload, headers);
        }


        // PUT completes an HTTP PUT request to the API server and returns its response to the client.
        public string PUT(string REST_url = "", string payload = null, Dictionary<string, string> headers = null)
        {
            return this.call(REST_url, "", "PUT", payload, headers);
        }
    }
}