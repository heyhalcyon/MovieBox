function handleResult(resultData) {
    console.log("handleResult: populating user info from resultData");

    let userInfoElement = jQuery("#user_info");

    userInfoElement.append("<p>User Name: " + resultData[0]["user_name"] + "</p>");

    console.log("handleResult: populating shopping cart table from resultData");

    // Populate the cart table
    // Find the empty table body by id "cart_table_body"
    let cartTableBodyElement = jQuery("#confirm_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 1; i < resultData.length; i++) {   //resultData[0] is the jsonUserObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>"+resultData[i]["sale_id"]+"</th>"; 
        rowHTML += "<th> <a href=\"single-movie.html?id=" + resultData[i]["movie_id"] + "\">" + resultData[i]["item_title"] + "</a></th>";
        rowHTML += "<th>"+resultData[i]["item_quantity"]+"</th>"; 
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        cartTableBodyElement.append(rowHTML);
    }
    
}


jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "POST",// Setting request method
    url: "api/confirm", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
