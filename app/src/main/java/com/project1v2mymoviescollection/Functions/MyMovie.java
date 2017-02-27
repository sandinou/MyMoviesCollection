package com.project1v2mymoviescollection.Functions;

/**
 * Created by SandraMac on 17/02/2017.
 *
 * This class create a movie object for the InternetSearchActivity from the API
 */

public class MyMovie {

    private String title,poster,year, id, genre;

    /**
     * Constructor
     * @param title
     * @param poster
     * @param year
     * @param id
     */
    public MyMovie(String title, String poster, String year, String id) {
        this.title = title;
        this.poster = poster;
        this.id = id;
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public MyMovie(String title, String poster, String year, String id, String genre) {

        this.title = title;
        this.poster = poster;
        this.year = year;
        this.id = id;
        this.genre = genre;
    }

    /**
     * Return the movie title, got from the API
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Return the movie's imdbID, got from the API
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Return the movie's poster url, got from the API
     * @return poster
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Return the movie's year, got from the API
     * @return year
     */
    public String getYear() {
        return year;
    }
}
