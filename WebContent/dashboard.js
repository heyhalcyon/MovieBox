function handleTableResult(resultData) {
	$( ".data" ).remove();
    console.log("handleStarResult: populating table list from resultData");
    console.log(resultData)
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let tableListBodyElement = jQuery("#table-list");
    console.log(tableListBodyElement);
    tableListBodyElement.append("<code>");
    tableListBodyElement.append(JSON.stringify(resultData, null, ' '));
    tableListBodyElement.append("</code>");
    
    
}

$(function(){
	$("#addMovieSubmit").click(function(){
		var movieTitle = $('#advanceSearchForm').find('input[name="title"]').val();
		var movieYear = $('#advanceSearchForm').find('input[name="year"]').val();
		var movieDirector = $('#advanceSearchForm').find('input[name="director"]').val();
		var movieStar = $('#advanceSearchForm').find('input[name="star"]').val();
		var movieStarYear = $('#advanceSearchForm').find('input[name="syear"]').val();
		var movieGenre = $('#advanceSearchForm').find('input[name="genre"]').val();
		console.log(movieTitle);
		console.log(jQuery.param({type:"movie",title:movieTitle, year:movieYear, dir:movieDirector, genre:movieGenre, star: movieStar, syear:movieStarYear}))
		$.ajax({
			url: 'api/meta',
	     	dataType: "json", // Setting return data type
	         method: "POST", // Setting request method
	         data: jQuery.param({type:"movie",title:movieTitle, year:movieYear, dir:movieDirector, genre:movieGenre, star: movieStar, syear:movieStarYear}), // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: function (response) {
	             alert(response.status);
	         }
	     });
		
	});
	
});

$(function(){
	$("#addStarSubmit").click(function(){
		var starName = $('#addStarForm').find('input[name="name"]').val();
		var starYear = $('#addStarForm').find('input[name="year"]').val();
		console.log(jQuery.param({type:"star",name:starName, year:starYear}));
		$.ajax({
			url: 'api/meta',
	     	dataType: "json", // Setting return data type
	         method: "POST", // Setting request method
	         data: jQuery.param({type:"star",name:starName, year:starYear}), // Setting request url, which is mapped by StarsServlet in Stars.java
	         success: function (response) {
	             alert(response.status);
	         }
	     });
		
	});
	
});



jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/meta", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleTableResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});