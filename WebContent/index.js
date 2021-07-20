/**
 * This MovieList file is following frontend and backend separation.

 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
var order = "title";
var genre = "";
var rpp = 20;
var filterType = "";
var startWith = "";
var dict = {};

$('.ui.checkbox')
.checkbox()
;

function handleStarResult(resultData) {
	$( ".data" ).remove();
    console.log("handleStarResult: populating movie list from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieListBodyElement = jQuery("#movie-list");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < rpp; i++) {
    							
        // Concatenate the html tags with resultData jsonObject
    	let rowHTML = "";
        rowHTML +="<div class=\"row data\" id=\""+ resultData[i]["movie_id"]+"\"><div class=\"three wide column\"><div class=\"ui items\"><div class=\"item\"><div class=\"image\"><i class=\"big play icon\">Thumbnail placeholder</i></div></div></div></div>"
        rowHTML +="<div class=\"seven wide column\"><div class=\"ui items\"><div class=\"item\"><div class=\"content\"><a class=\"header\" href=\"single-movie.html?id="+ resultData[i]["movie_id"] +"\">"  + resultData[i]["movie_title"]
        rowHTML +="</a><div class=\"meta\"><span> " + resultData[i]["movie_year"] + " </span>" + "<p>Director: " + resultData[i]["movie_director"] + "</p>" + 
                    "<p>" + resultData[i]["movie_rating"] + "/10.0 </p></div>" + "</div><div class=\"description\">"

        for(let j = 0; j < resultData[i]["movie_genres"].length; j++){
            rowHTML += "<p>" + resultData[i]["movie_genres"][j]["genre_name"] + "</p>";
        }  

        rowHTML +="</div></div></div></div><div class=\"three wide column\"><div class=\"extra\"><h1>Starring:</h1><p></p>"
        for(let j = 0; j < resultData[i]["movie_stars"].length; j++){
            rowHTML +="<p><a href=\"single-star.html?id=" + resultData[i]["movie_stars"][j]["star_id"] + "\">" + resultData[i]["movie_stars"][j]["star_name"] + "</p>";
        }
        rowHTML += "</div></div><div class=\"two wide column\"> <div class=\"extra\"><button class=\"ui button\" id=\""+ resultData[i]["movie_id"]+"\" onClick=\"buyButton(this.id)\">Buy</button></div></div></div>"    
       
        // Append the row created to the table body, which will refresh the page
        movieListBodyElement.append(rowHTML);
        // WTF?????I HAVE NO IDEA WHY THERE'S AN EXTRA <A> TAG?????
        //$('.two.wide a').remove()
        tempMovieId=resultData[i]["movie_id"]
        temp = $('.row.data').filter('#' + tempMovieId).children()[3]
        f = temp.children[0].children[0] 
        temp.children[0].remove()
        temp.append(f)
    }
}

function buyButton(id){
	 $.ajax({
	       dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/cart?id="+id+"&req=add&qty=1" 
	     });
	}

function handlePage(resultData, currentPage){
	$( ".pagination" ).remove();
	count = resultData[0]["count"]/20;
	let paginationElement = jQuery("#pagination");
	let rowHTML = "";
	
	if(count < 20){
		rowHTML = "<div class=\"ui pagination menu\"><a class=\"active item\">1</a></div>"
	}else if(currentPage < 6){
		rowHTML = "<div class=\"ui pagination menu\">";
		for(let i = 1; i < 6; i++){
			if(i==currentPage){
				rowHTML +="<a class=\"active item\">" + i + "</a>"
			}else{
				rowHTML +="<a class=\"item\">" + i + "</a>"
			}			
		}	
	}
	
	rowHTML = "<div class=\"ui pagination menu\">";
	if(this.filterType=="genre"){
		for(let j = 1; j < (count+1);j++){
			if(j==currentPage){
				rowHTML +="<a onclick='pageButton(\""+ filterType + "\"," +  "\"" +resultData[0]["genre"] + "\"," + j + ")' class=\"active item\">" + j + "</a>"
			}else{
				rowHTML +="<a onclick='pageButton(\""+ filterType + "\"," +  "\"" +resultData[0]["genre"] + "\"," + j + ")' class=\"item\">" + j + "</a>"
			}
		}
	}else if(this.filterType=="alphabet"){
		for(let j = 1; j < (count+1);j++){
			if(j==currentPage){
				rowHTML +="<a onclick='pageButton(\""+ filterType + "\"," +  "\"" +resultData[0]["startWith"] + "\"," + j + ")' class=\"active item\">" + j + "</a>"
			}else{
				rowHTML +="<a onclick='pageButton(\""+ filterType + "\"," +  "\"" +resultData[0]["startWith"] + "\"," + j + ")' class=\"item\">" + j + "</a>"
			}
		}
	}
	
	rowHTML +="</div>"

	paginationElement.append(rowHTML)
	//<div class="ui pagination menu">
	  //<a class="active item">
	   // 1
	  //<a>
	  //<div class="disabled item">
	  //  ...
	  //</div>
	  //<a class="item">
	   // 10
	  //</a>
	  //<a class="item">
	  //  11
	  //</a>
	  //<a class="item">
	  //  12
	  //</a>
	//</div>
	
	
}

$(function(){
	$("#filter").click(function(){
		$(".test").modal('show');
	});
	$(".test").modal({
		closable: true
	});
});

$(function(){
	$("#advanceSearchSubmit").click(function(){
		var movieTitle = $('#advanceSearchForm').find('input[name="title"]').val();
		var movieYear = $('#advanceSearchForm').find('input[name="year"]').val();
		var movieDirector = $('#advanceSearchForm').find('input[name="director"]').val();
		var movieStar = $('#advanceSearchForm').find('input[name="star"]').val();
		var order = $('#advanceSearchForm').find(":selected").text();
		var subStringMatch = $('#advanceSearchForm').find(":checkbox").is(':checked');
		console.log(movieTitle);
		var myurl = "api/advancesearch?";
		if(movieTitle!=""){
			myurl+="t="+movieTitle+"&";
		}
		if(movieYear!=""){
			myurl+="y="+movieYear+"&";
		}
		if(movieDirector!=""){
			myurl+="d="+movieDirector+"&";
		}
		if(movieStar!=""){
			myurl+="s="+movieStar+"&";
		}
		myurl+="sub="+subStringMatch+"&p=1&o="+order+"&rpp="+rpp;
		console.log(myurl);
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: myurl, // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handleStarResult(resultData)
	     });
	});
	
});


$(function(){
	$("#alphabet").click(function(){
		$(".alphabetModal").modal('show');
	});
	$(".alphabetModal").modal({
		closable: true
	});
});

function pageButton(type, key, page){
	if(type=="genre"){
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/singlesearch?g="+key+"&p="+page+"&o="+order+"&rpp="+rpp, // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handleStarResult(resultData)
	     });
		
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/singlesearch?g="+key+"&c=True"+"&p="+page+"&o="+order+"&rpp="+rpp, // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handlePage(resultData, page)
	     });
	}else if(type=="alphabet"){
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/singlesearch?sw="+key+"&p="+page+"&o="+order+"&rpp="+rpp, // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handleStarResult(resultData)
	     });
		
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/singlesearch?sw="+key+"&c=True"+"&p="+page+"&o="+order+"&rpp="+rpp, // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handlePage(resultData, page)
	     });
	}
	
	

}


function orderButton(order){
	$(".mini.modal").modal('hide');
	this.order = order;
	if(this.filterType=="genre"){
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/singlesearch?g="+genre+"&p=1&o="+order+"&rpp="+rpp, // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handleStarResult(resultData)
	     });
		
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/singlesearch?g="+genre+"&c=True", // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handlePage(resultData, 1)
	     });
	}else if(this.filterType=="alphabet"){
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/singlesearch?sw="+this.startWith+"&p=1&o="+order+"&rpp="+rpp, // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handleStarResult(resultData)
	     });
		
		$.ajax({
	     	dataType: "json", // Setting return data type
	         method: "GET", // Setting request method
	         url: "api/singlesearch?sw="+this.startWith+"&c=True", // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: (resultData) => handlePage(resultData, 1)
	     });
	}
	
}


function filterButton(id){
	this.filterType="genre";
	$(".test").modal('hide');
	$.ajax({
     	dataType: "json", // Setting return data type
         method: "GET", // Setting request method
         url: "api/singlesearch?g="+id+"&c=True&p=1&rpp=20&o=title", // Setting request url, which is mapped by StarsServlet in Stars.java
         success: (resultData) => handlePage(resultData, 1)
     });
	
	 $.ajax({
     	dataType: "json", // Setting return data type
         method: "GET", // Setting request method
         url: "api/singlesearch?g="+id+"&p=1"+"&o="+order+"&rpp="+rpp, // Setting request url, which is mapped by StarsServlet in Stars.java
         success: (resultData) => handleStarResult(resultData)
     });
	 genre = id;
}

function alphabetButton(id){
	this.filterType="alphabet";
	this.startWith=id;
	$(".alphabetModal").modal('hide');
	$.ajax({
     	dataType: "json", // Setting return data type
         method: "GET", // Setting request method
         url: "api/singlesearch?sw="+id+"&c=True&p=1&rpp=20&o=title", // Setting request url, which is mapped by StarsServlet in Stars.java
         success: (resultData) => handlePage(resultData, 1)
     });
	
	 $.ajax({
     	dataType: "json", // Setting return data type
         method: "GET", // Setting request method
         url: "api/singlesearch?sw="+id+"&p=1"+"&o="+order+"&rpp="+rpp, // Setting request url, which is mapped by StarsServlet in Stars.java
         success: (resultData) => handleStarResult(resultData)
     });
	 genre = id;
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/index", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});


/*
 * This function is called by the library when it needs to lookup a query.
 * 
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")

	// TODO: if you want to check past query results first, you can do it here
	if(query in dict){
		console.log("in handlelookup, query is in cache")
		handleLookupAjaxSuccess(dict[query], query, doneCallback) 
	}else{
	
		console.log("in handlelookup, query is not in cache, sending AJAX request to backend Java Servlet")
	
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": "api/ac?query=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log("lookup suggestion list: ",jsonData)
	
	// TODO: if you want to cache the result into a global variable you can do it here
	dict[query]=data;
	
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion	
	console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
//	$.ajax({
//     	 dataType: "json", // Setting return data type
//         method: "GET", // Setting request method
//         url: "api/single-movie?id=" + suggestion["data"]["movieID"],
//     });
	window.location.replace( "single-movie.html?id=" + suggestion["data"]["movieID"]);

}

//$('#autocomplete').on('change', function() {
//    var text = $('#autocomplete').val();
//    var minlength = 3;   // TODO: add other parameters, such as minimum characters
//    if(text.length >= minlength){
//       
$('#autocomplete').autocomplete({
    		// documentation of the lookup function can be found under the "Custom lookup function" section
    	    lookup: function (query, doneCallback) {
    	    		handleLookup(query, doneCallback)
    	    },
    	    onSelect: function(suggestion) {
    	    		handleSelectSuggestion(suggestion)
    	    },
    	    // set delay time
    	    deferRequestBy: 300,
    	    minChars: 3,
    	    
});
//    }
//});

/*
 * do normal full text search if no suggestion is selected 
 */
function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// you should do normal search here

	jQuery.ajax({
		"method": "GET",
		// generate the request url from the query.
		// escape the query string to avoid errors caused by special characters 
		"url": "api/ft?search="+ escape(query),
		"success": function(data) {
			handleStarResult(data)
		},
		"error": function(errorData) {
			console.log("ft search ajax error")
			console.log(errorData)
		}
	})	
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
	console.log("enter pressed");
	// keyCode 13 is the enter key
	// if you have a "search" button, you may want to bind the onClick event as well of that button
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val())
	}
})

$('#searchbutton').click(function(){
	console.log("search button clicked");
	handleNormalSearch($('#autocomplete').val())
})
