This is a sample ASP server-side library for the CoolCalc MJ8 application.  We are not an ASP shop, do not use this code "as is" but evaluate if changes are required to meet your production criteria.  Suggestions for improvement and pull requests are welcome.

Basic configuration for your own installation:

1. Enter your API credentials in Web.config and verify Rewrite rules (/Web.config).

2. Enter a valid Google maps key in static HTML file restui.html (coolcalc/ui/mj8/v-bootstrap/staging/).

3. Edit local JavaScript configs in libmj8.js and fun-factory.js (coolcalc/javascript/mj8/v-bootstrap/staging).

4. Implement the session-user endpoint session-user.ashx.  The particulars of the implementation will be determined by the session/user authentication mechanisms your application uses. (coolcalc/user).
 
Additional customizations for production servers:

5. In production you must verify that the dealer id (customer account nr) in the REST URL corresponds to the actual session user to prevent dishonest users from downloading someone else's project list.  See the comments in line 31 in file dealers.ashx (coolcalc/client/staging/).  Actual implementation will vary based on your specific user accounts/session management.

Customizing the library for PDF and email send functionality:

6. The local JavaScript file fun-factory.js (coolcalc/javascript/mj8/v-bootstrap/staging/) contains handler functions for PDF rendering and email send functionality.  These functions are injected to the CoolCalc JavaScript library when the user interface is initialized.  You can edit the local functions any way you see fit as long as you do not change the function signatures.  In their standard implementation the PDF and email handlers make a POST request to a local endpoint on your server with a given HTML URL (the MJ8 report the user wants to download or email) and email in the payload.  Your local endpoint is responsible for generating the PDF or sending the email.  In this library the email endpoint is not implemented as most clients will use a different API or third party email sending library.  A sample PDF endpoint is implemented in coolcalc/client/staging/api2pdf.ashx using the api2pdf.com API.  If you want to use api2pdf.com you only need to add your api_key in line 34 of said file.
