// ----------------------------------------------------------------------------------------------------------------------------------------------
//
// myFunFactory implements client-specific functions to be consumed by the CoolCalc JavaScript library.
//
// The CoolCalc JavaScript library requires that the following methods are defined and return client-specific functions:
//
//   * CSRFHandler: Returns an anonymous function where you can implement CSRF protection for ajax requests.
//       Returned function signature: function(xhr) { ... }
//       Param xhr represents any XMLHttpRequest used by the CoolCalc JavaScript library to communicate with your local server-side library.
//
//   * PDFHandler: Returns an anonymous function which opens or downloads a PDF document based on a given HTML URL.
//       Returned function signature: function(someURL, backToCoolCalc) { ... }
//       When your local functionality is complete you must call backToCoolCalc() to return control back to the main CoolCalc library.
//
//   * emailHandler: Returns an anonymous function which emails a PDF for a given HTML URL to a given email address.
//       Returned function signature: function(someURL, email, backToCoolCalc) { ... }
//       When your local functionality is complete you must call backToCoolCalc() to return control back to the main CoolCalc library.
//
//   * startupCallback: Returns an anonymous function which you can use to add custom functionality to the Khipu-JS instance.
//       Returned function signature: function(restUI) { ... }
//
// If your server returns any custom REST resources or you want to override any of the "render" callbacks in the main CoolCalc library,
// you can provide custom "render" handlers as a map (or JavaScript object) of resource name to the corresponding render handler. 
// myFunFactory.renderHandlers() should return the map/object containing your custom render handlers.
// In the default implementation this.addlHandlers represents your custom handlers.
//
//   Render handler function signature: function(resourceBodyDiv, responseHeaders, someResource, resourceName, options, restUI) { ... }
//
// ----------------------------------------------------------------------------------------------------------------------------------------------
var myFunFactory = {

    // The URL we use to convert HTML to PDF.
    PDFURL: "/coolcalc/client/staging/api2pdf.php",

    // The URL we use to email a PDF to a user.
    emailURL: "/coolcalc/client/staging/send-report.php",

    // The URL for the static HTML templates for our local resources,
    // ie. the REST resources specific to this client only (not from the CoolCalc API)
//    localResourceTemplateURL: "/coolcalc/local-templates/mj8/staging/index.php",
    localResourceTemplateURL: "",

    // addlHandlers represents our local/custom "render" handlers we want to inject into the CoolCalc library.
    addlHandlers: {}

};


// CSRFHandler returns a function that is called by the Khipu-JS library to implement CSRF protection.
myFunFactory.CSRFHandler = function() {
    return function(xhr) {
        // This is just boilerplate code.  Implement retrieving the token value and actual header name as required for your application.
        myTokenValue = "";
        xhr.setRequestHeader("X-CSRF-Token", myTokenValue);
    }
}


// PDFHandler returns a function that opens or downloads a PDF document based on an HTML URL.
myFunFactory.PDFHandler = function() {

    var myObj = this;

    return function(someURL, backToCoolCalc) {

        var http  = new XMLHttpRequest();
        http.open("POST", myObj.PDFURL, true);

        http.onreadystatechange = function() {
            if (http.readyState == 4) {
                if (http.status == 200) {
                    let myData = JSON.parse(http.responseText);
                    let pdfURL = myData.FileUrl;
                    if (pdfURL) {
                        // Call backToCoolCalc to transfer control back to the CoolCalc JavaScript library.
                        backToCoolCalc();
                        // Download the actual PDF.
                        window.location = pdfURL;
                    } else {
                        alert("Sorry, we were unable to generate the requested PDF document.");
                        // Call backToCoolCalc to transfer control back to the CoolCalc JavaScript library.
                        backToCoolCalc();
                    }
                } else {
                    alert("Sorry, we were unable to generate the requested PDF document.");
                    // Call backToCoolCalc to transfer control back to the CoolCalc JavaScript library.
                    backToCoolCalc();
                }
            }
        }

        var payload = { HTMLURL: someURL };
        http.send(JSON.stringify(payload));
    }
}


// emailHandler returns a function that sends an email with PDF attachment representing a given HTML URL to a given email address.
myFunFactory.emailHandler = function() {

    var myObj = this;

    return function(someURL, email, backToCoolCalc) {

        var http  = new XMLHttpRequest();
        http.open("POST", myObj.emailURL, true);

        http.onreadystatechange = function() {
            if (http.readyState == 4) {
                if (http.status == 200) {
                    alert("The MJ8 report has been emailed to " + email);
                } else {
                    alert("Sorry, we were unable to email the MJ8 report.");
                }
                backToCoolCalc();
            }
        }

        var payload = { email: email, HTMLURL: someURL };
        http.send(JSON.stringify(payload));
    }
}


// startupCallback returns a local callback that will be executed after the Khipu-JS instance is created.
myFunFactory.startupCallback = function() {
    var myObj = this;
    return function(restUI) {
        // We load some static HTML templates for the local "MJ8 preview" resources.
        restUI.loadTemplates("resources", myObj.localResourceTemplateURL);
    }
}


// renderHandlers returns this.addlHandlers.
// This is used to inject custom "render" handlers above and beyond the standard handlers in the CoolCalc library.
myFunFactory.renderHandlers = function() {
    return this.addlHandlers;
}


// MJ8Preview is a custom render handler for the MJ8 report "preview".
// The "MJ8Preview" REST resource is a custom resource we create locally for the coolcalc.com site only,
// it is used when an MJ8 report that has not yet been paid for is requested from the HVAC system interface.
myFunFactory.addlHandlers.MJ8Preview = function(resourceBodyDiv, responseHeaders, someResource, resourceName, options, restUI) {

    // Load calculation visualizations.
    coolcalc.visualizeLC(someResource, "MJ8Preview", resourceBodyDiv);

    // Click handler to download the full report.
    var myViewMoreButton = document.getElementById("btn-view-report"); 
    var myLinkCopy = restUI.selfLink(someResource);
    if (myLinkCopy.href.indexOf("?") == -1) {
        myLinkCopy.href += "?";
    }
    myLinkCopy.href += "&deduct_credit=1";
    myViewMoreButton.onclick = restUI.readHandler(myLinkCopy, null);
}


// MJ8PreviewAlt is a custom render handler for the alternate (empty) MJ8 report "preview".
// The "MJ8PreviewAlt" REST resource is a custom resource we create locally for the coolcalc.com site only,
// it is used when a report that has not yet been paid for is requested directly from the project list.
myFunFactory.addlHandlers.MJ8PreviewAlt = function(resourceBodyDiv, responseHeaders, someResource, resourceName, options, restUI) {

    // Click handler to download the full report
    var myViewMoreButton = document.getElementById("btn-view-report"); 
    var myLinkCopy = restUI.selfLink(someResource);
    if (myLinkCopy.href.indexOf("?") == -1) {
        myLinkCopy.href += "?";
    }
    myLinkCopy.href += "&deduct_credit=1";
    myViewMoreButton.onclick = restUI.readHandler(myLinkCopy, null);
}


// MJ8PreviewNoCredits is a custom render handler for the MJ8PreviewNoCredits resource.
// The "MJ8PreviewNoCredits" REST resource is a custom resource we create locally for the coolcalc.com site only,
// it is used when an MJ8 report is requested by a user who has a zero credit balance.
myFunFactory.addlHandlers.MJ8PreviewNoCredits = function(resourceBodyDiv, responseHeaders, someResource, resourceName, options, restUI) {
    // Load calculation visualizations.
    coolcalc.visualizeLC(someResource, "MJ8PreviewNoCredits", resourceBodyDiv);
}


