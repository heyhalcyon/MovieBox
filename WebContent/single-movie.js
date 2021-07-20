function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");
    
    let movieGenres = "<p>";
    for(j=0; j<resultData[0]["movie_genre"].length;j++){
    	movieGenres += resultData[0]["movie_genre"][j]["movie_genre"]+"     ";
    }
    movieGenres += "</p>" // + "<p>" + resultData[0]["movie_genre"] + "</p>"
    console.log(movieGenres);
    
    
    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Name: " + resultData[0]["movie_title"] + "<button class=\"ui button\" id=\""+ resultData[0]["movie_id"]+"\" onClick=\"buyButton(this.id)\">Buy</button></p>"
    		+ movieGenres
    		+ "<p>Year: " + resultData[0]["movie_year"]+"</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#star_table_body");
    console.log(resultData[0]["movie_stars"]);
    

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData[0]["movie_stars"].length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th> <a href=\"single-star.html?id=" + resultData[0]["movie_stars"][i]["star_id"] + "\">" + resultData[0]["movie_stars"][i]["star_name"] + "</a></th>";
        rowHTML += "<th>" + resultData[0]["movie_stars"][i]["star_birth_year"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


function buyButton(id){
	$.ajax({
     	dataType: "json", // Setting return data type
         method: "GET", // Setting request method
         url: "api/cart?id="+id+"&req=add&qty=1", // Setting request url, which is mapped by StarsServlet in Stars.java
         success: (resultData) => handleStarResult(resultData)
     });
}


// Get id from URL
let movieId = getParameterByName('id');
console.log(movieId);
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});