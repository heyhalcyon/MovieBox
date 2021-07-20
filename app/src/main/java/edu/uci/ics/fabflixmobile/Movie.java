package edu.uci.ics.fabflixmobile;

public class Movie {

    private String id;
    private String title;
    private String year;
    private String rating;
    private String director;
    private String stars;
    private String genres;

    public Movie(String id, String title, String year, String director, String rating){
        this.id = id;
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.director = director;
    }

    public Movie(String id, String title, String year, String director, String rating, String stars, String genres){
        this.id = id;
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.director = director;
        this.stars = stars;
        this.genres = genres;
    }

    public String getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getRating() {
        return rating;
    }

    public String getDirector() {
        return director;
    }

    public String getStars() {
        return stars;
    }

    public String getGenres(){
        return genres;
    }
}
