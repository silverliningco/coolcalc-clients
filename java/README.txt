This is a sample Java server-side library for the CoolCalc MJ8 application.  We are not a Java shop, do not use this code "as is" but evaluate if changes are required to meet your production criteria.  Suggestions for improvement and pull requests are welcome.

Configure for your own installation:

1. Enter your API credentials in (2) Java servlets DealerCollectionHandler.java and MJ8Report.java (src/com/cc30/endpoint)

2. Edit local JavaScript configs in libmj8.js and fun-factory.js (WebContent/javascript/mj8/v-bootstrap/staging).

3. Implement the session-user endpoint SessionUser.java.  The particulars of the implementation will be determined by the session/user authentication mechanisms your application uses. (src/com/cc30/sessionuser).

This library is designed as if the MJ8 application is open to the web but authentication is required to download MJ8 reports.  If an unauthenticated user attempts to download an MJ8 report they will be shown a locally generated MJ8 report "preview" resource.  This implementation requires additional customizations:

4. Implement checking whether the user is authenticated or not in DealerCollectionHandler.java line 187 (src/com/cc30/endpoint)

5. Verify static HTML for the MJ8 preview resources meets your needs (WebContent/local-assets/static-html).  The HTML can be customized as required, customizing this HTML should not affect functionality. If you move the static HTML files you will also have to edit the path to the local HTML templates in src/com/cc30/localtemplates.

6. Implement your actual login flow either by adding links to a login page in the static HTML for the MJ8 preview (WebContent/local-assets/static-html) or implementing event handlers in myFunFactory.addlHandlers (WebContent/javascript/mj8/v-bootstrap/staging).

In production you must verify that the dealer id (customer account nr) in the REST URL corresponds to the actual session user.  See the comments in function coolcalcApiURL in file DealerCollectionHandler.java (src/com/cc30/endpoint).

Customizing the library for PDF and email send functionality:

The local JavaScript file fun-factory.js (WebContent/javascript/mj8/v-bootstrap/staging) contains handler functions for PDF rendering and email send functionality.  These functions are injected to the CoolCalc JavaScript library when the user interface is initialized.  You can edit the local functions any way you see fit as long as you do not change the function signatures.  In their standard implementation, the PDF and email handlers make a POST request to a local endpoint on your server with a given HTML URL (the MJ8 report the user wants to download or email) and email in the payload.  Your local endpoint is responsible for generating the PDF or sending the email.  These endpoints are not implemented in this sample library as most clients will different APIs or third party libraries for this functionality (for information only, the coolcalc.com site uses api2pdf.com for PDF generation)


