
/**
 * Handle the items in item list 
 * @param resultDataString jsonObject, needs to be parsed to html 
 */
function handleCartArray(resultData) {
    console.log("handleResult: populating user info from resultData");
    $( ".data" ).remove();
    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let userInfoElement = jQuery("#user_info");

    // append two html <p> created to the h3 body, which will refresh the page
    userInfoElement.append("<p class = \"data\"> User Name: " + resultData[0]["user_name"] + "</p>");

    console.log("handleResult: populating shopping cart table from resultData");

    // Populate the cart table
    // Find the empty table body by id "cart_table_body"
    let cartTableBodyElement = jQuery("#cart_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    let cartExistElement = jQuery("#cart_exist");
    let rHTML = "";
    if(resultData.length == 1){
  
    	rHTML += "<tr class = \"data\">";
    	rHTML += "<th> Your Cart is empty now</th>";
    	rHTML += "</tr>";

    	
        // Append the row created to the table body, which will refresh the page
    	cartExistElement.append("<p> Your Cart is empty right now </p>");
    }else
    {
    	cartExistElement.append("<p class = \"data\"> Below is your cart </p>");

    	
        for (let i = 1; i < resultData.length; i++) {   //resultData[0] is the jsonUserObject
        	let rowHTML = "";
        	rowHTML += "<tr class = \"data\">";
        	rowHTML += "<th> <a href= \"single-movie.html?id=" + resultData[i]["movie_id"] + "\" style=\"color: #fff;\">" + resultData[i]["item_title"] + "</a ></th>";
        	rowHTML += "<th> <input type=\"number\" id=\"quantity\" name=\"quantity\" min=\"0\" max=\"100000\" value = \""+ resultData[i]["item_quantity"]+"\"></th>"; 
        	rowHTML += "<th> <button id=\""+ resultData[i]["movie_id"]+"\" onClick=\"filterUpdateButton(this.id)\">Update</button> </th>";
        	rowHTML += "<th> <button id=\""+ resultData[i]["movie_id"]+"\" onClick=\"filterRemoveButton(this.id)\">Remove</button> </th>";
        	rowHTML += "</tr>";

        	// Append the row created to the table body, which will refresh the page
        	cartTableBodyElement.append(rowHTML);  	
        }
        let checkOutElement = jQuery("#check_out");
        let checkHTML = "";
       // <button class="ui button" type="submit">Place Order</button>
        checkHTML += "<button id=\"check_out\" onClick=\"filterCheckOutButton()\">Check Out</button>";
        
    }
}

function getQty() {
	return document.getElementById("quantity").value;
}
function filterCheckOutButton(){
	$.ajax({
     	dataType: "json", // Setting return data type
         method: "GET", // Setting request method
         url: "api/checkout", // Setting request url, which is mapped by StarsServlet in Stars.java
        //NOT FINISHED
     });
}

function filterUpdateButton(id){
	let quantity = getQty();
	$.ajax({
     	dataType: "json", // Setting return data type
         method: "GET", // Setting request method
         url: "api/cart?id=" + id+"&req=update"+"&qty="+quantity, // Setting request url, which is mapped by StarsServlet in Stars.java
         success: (resultData) => handleCartArray(resultData)
     });
}

function filterRemoveButton(id){
	let quantity = getQty();
	 $.ajax({
    	dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/cart?id=" + id+"&req=remove"+"&qty="+quantity, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleCartArray(resultData)
    });
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/cart", // Setting request url, which is mapped by CartServlet
    success: (resultData) => handleCartArray(resultData) // Setting callback function to handle data returned successfully by the CartServlet
});

// Bind the submit action of the form to a event handler function
//$("#cart").submit((event) => handleCartInfo(event));