/**
 * Handle the data returned by CheckOutServlet
 * @param resultDataString jsonObject
 */
function handleCheckoutResult(resultDataJsonString) {
    resultDataJson = JSON.parse(resultDataJsonString);

    console.log("handle check response");
    console.log(resultDataJson);
    //console.log(resultDataJson["status"]);
    console.log("hello");
    // If login succeeds, it will redirect the user to index.html
    //if (resultDataJson["message"] ==="success") {
    if (resultDataJson["status"] =="success") {
        //window.location.replace("confirm.html");
    	console.log("success: right before jump page");
        window.location.href = 'confirm.html';
    } else {
        // If login fails, the web page will display 
        // error messages on <div> with id "login_error_message"
        console.log("show error message blah");
        console.log(resultDataJson["message"]);
        $("#checkout_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitCheckoutForm(formSubmitEvent) {
    console.log("submit checkout form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
//
//    $.post(
//        "api/checkout",
//        // Serialize the login form to the data sent by POST request
//        $("#checkout_form").serialize(),
//        (resultDataString) => handleCheckoutResult(resultDataString)   //call-back function
//    );
    $.ajax({
		type: "POST",
		url: "api/checkout?placed=true",
		data: $("#checkout_form").serialize(),
		success: function(data){
			handleCheckoutResult(data);
		}
	});
    
}

// Bind the submit action of the form to a handler function
$("#checkout_form").submit((event) => submitCheckoutForm(event));

