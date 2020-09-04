// ----------------------------------------------------------------------------------------------------------------------------------------------
//
// libMJ8 is a small local library to initialize the CoolCalc MJ8 application.
// In most cases you should not make any changes here other than setting the correct values of the following first-level attributes:
//     * sessionUserURL
//     * APIDomain => normally "stagingapi.coolcalc.com" for staging, "api3.coolcalc.com" for production.
//     * localDomain => typically "my-local-host" for development, "www.my-domain.com" for production.
//     * MJ8AjaxURL
//     * MJ8ReportURL
//
// ----------------------------------------------------------------------------------------------------------------------------------------------
var libMJ8 = {

    // The URL of the local script that returns the user info for the current session.
    // The output of this script should be something like { "dealerReference": "123-xyz", "userReference": "345-abc", "isAdmin": false }
    // If isAdmin is true, the user will be able to view projects for all individual end users in their organization.
 //   sessionUserURL: "/coolcalc/user/staging/session-user.php",
    sessionUserURL: "/coolcalc/user/session-user.ashx",

    // URL to the Khipu-JS JSON configs.
    khipuConfigURL: "https://cdn.coolcalc.com/config/mj8/v-bootstrap/staging/khipu-js.json",

    // The domain names for the REST API and our local server, and the path to the local API endpoint.
    // The CoolCalc JavaScript library needs these to re-write the domain in ajax requests.
    APIDomain: "stagingapi.coolcalc.com",
    //localDomain: "stagingui.coolcalc.com",
    //localDomain: "localhost:443",

    // The entry point for our local server-side library.
    APIClientEndpoint: "/coolcalc/client",

    // The URL to the ajax script that downloads MJ8 report JSON from the CoolCalc API.
    // This is normally different from the main CoolCalc client endpoint because it should be accessible without user login.
    MJ8AjaxURL: "/coolcalc/client/staging/mj8-reports.ashx",

    // The URL for the static HTML page where you render HTML MJ8 reports.
    MJ8ReportURL: "/coolcalc/ui/mj8/v-bootstrap/staging/MJ8Report.html",

    // Map trace config URL.
    mapTraceConfigURL: "https://cdn.coolcalc.com/config/map-trace/staging/map-trace.json",    

};


// loadSessionUser loads the current session/user info and then executes the given callback. 
libMJ8.loadSessionUser = function(callback) {

    var http  = new XMLHttpRequest();
    http.open("GET", this.sessionUserURL, true);

    http.onreadystatechange = function() {
        if (http.readyState == 4) {
            if (http.status == 200) {
                var myUserInfo = JSON.parse(http.responseText);
                if (callback) {
                    callback(myUserInfo);
                }
            } else {
                alert("Sorry, unable to start CoolCalc session.");
            }
        }
    }

    http.send();
}


// landingPage initializes the single page REST UI on any resource specified by URL query param "hateoasLink" or
// the default MJ8 project list if no "hateoasLink" query param exists in the window URL.
libMJ8.landingPage = function(funFactory) {

    var myObj = this;

    // Most of the "this" first level properties get passed to the Khipu-JS constructor.
    var myConfig = {
        khipuConfigURL: this.khipuConfigURL,
        APIDomain: this.APIDomain,
        localDomain: this.localDomain,
        APIClientEndpoint: this.APIClientEndpoint,
        MJ8AjaxURL: this.MJ8AjaxURL,
        MJ8ReportURL: this.MJ8ReportURL,
        mapTraceConfigURL: this.mapTraceConfigURL,    
        SVGIconURL: this.SVGIconURL
    }

    // Load user info for the current session, then initialize the single page REST UI on the desired landing page.
    function userInfoCallback(myUserInfo) {
        var myLandingResourceLink = coolcalc.hateoasLinkParam();
        if (myLandingResourceLink) {
            coolcalc.MJ8.loadLandingResource(myConfig, myUserInfo, funFactory, funFactory.startupCallback(), myLandingResourceLink);
        } else {
            coolcalc.MJ8.projectList(myConfig, myUserInfo, funFactory, funFactory.startupCallback());
        }
    }
    this.loadSessionUser(userInfoCallback);
}


// projectList initializes the single page REST UI on the user's MJ8 project list.
// This function differs from function landingPage only in that we always load the project list, never any other resource.
libMJ8.projectList = function(funFactory) {

    var myObj = this;

    // Most of the "this" first level properties get passed to the Khipu-JS constructor.
    var myConfig = {
        khipuConfigURL: this.khipuConfigURL,
        APIDomain: this.APIDomain,
        localDomain: this.localDomain,
        APIClientEndpoint: this.APIClientEndpoint,
        MJ8AjaxURL: this.MJ8AjaxURL,
        MJ8ReportURL: this.MJ8ReportURL,
        mapTraceConfigURL: this.mapTraceConfigURL,    
        SVGIconURL: this.SVGIconURL
    }

    // Load user info for the current session, then initialize the single page REST UI on the user's project list.
    function userInfoCallback(myUserInfo) {
        coolcalc.MJ8.projectList(myConfig, myUserInfo, funFactory, funFactory.startupCallback());
    }
    this.loadSessionUser(userInfoCallback);
}


// MJ8Report loads and renders the MJ8 report corresponding to URL query param "report_id".
// The static HTML to render the report must be present in the DOM before calling this function.
libMJ8.MJ8Report = function() {

    // Initialize a Khipu instance, for no reason other than to use its HTML rendering functions.
    var restUI = new khipu({ DOMElementId: "mj8-report", CSS: {hiddenContent: "d-none"} }, null);

    // URL to download the MJ8 report JSON.
    var urlParams = coolcalc.getJsonFromUrl(location.href);
    var myURL = this.MJ8AjaxURL + "?reportId=" + encodeURIComponent(urlParams["report_id"]);

    // Load the MJ8 report and render HTML when server responds.
    var http  = new XMLHttpRequest();
    http.open("GET", myURL, true);
    http.onreadystatechange = function() {
        if (http.readyState == 4) {
            var myReport = JSON.parse(http.responseText);
            coolcalc.renderMJ8Report(document.getElementById("mj8-report"), "", myReport, "MJ8Report", {}, restUI);
        }
    }
    http.send();

}


