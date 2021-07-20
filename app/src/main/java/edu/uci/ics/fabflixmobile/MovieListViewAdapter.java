package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie>{

    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.layout_listview_row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_listview_row, parent, false);

        Movie singleMovie = movies.get(position);

        TextView titleView = (TextView)view.findViewById(R.id.title);
        TextView subtitleView = (TextView)view.findViewById(R.id.subtitle);
        TextView yearView = (TextView)view.findViewById(R.id.year);
        TextView ratingView = (TextView)view.findViewById(R.id.rating);
        TextView starView = (TextView)view.findViewById(R.id.star);
        TextView genreView = (TextView)view.findViewById(R.id.genre);

        titleView.setText(singleMovie.getTitle());
        subtitleView.setText(singleMovie.getDirector());
        yearView.setText(singleMovie.getYear());
        ratingView.setText(singleMovie.getRating());
        starView.setText(singleMovie.getStars());
        genreView.setText(singleMovie.getGenres());


        return view;
    }
}


