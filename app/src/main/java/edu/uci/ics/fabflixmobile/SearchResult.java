package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResult extends ActionBarActivity {
    private int page = 1;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Bundle bundle = getIntent().getExtras();
        String response = bundle.getString("response");
        url = bundle.getString("url");
        page = bundle.getInt("page");

        ArrayList<Movie> movies = new ArrayList<>();

        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Person person = people.get(position);
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, id: %s", position, movie.getId());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                Intent goToIntent = new Intent(SearchResult.this, SingleMovieActivity.class);

                goToIntent.putExtra("id", movie.getId());
                goToIntent.putExtra("rating", movie.getRating());
                //goToIntent.putExtra("message", msg);

                startActivity(goToIntent);
            }
        });

        try {
            JSONArray jsonArray = new JSONArray(response);


            // looping through All Contacts
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                String id = c.getString("movie_id");
                String title = c.getString("movie_title");
                String year = c.getString("movie_year");
                String director = c.getString("movie_director");
                String rating = c.getString("movie_rating");
                String star = "";
                JSONArray starJsonArray = new JSONArray(c.getString("movie_stars"));
                for (int j = 0; j < starJsonArray.length(); j++){
                    star+=starJsonArray.getJSONObject(j).getString("star_name") + " ";
                }
                String genres = "";
                JSONArray genreJsonArray = new JSONArray(c.getString("movie_genres"));
                for (int j = 0; j < genreJsonArray.length(); j++){
                    genres+=genreJsonArray.getJSONObject(j).getString("genre_name") + " ";
                }
                //String star = c.getString("movie_rating");

                // Phone node is JSON Object
                //JSONObject phone = c.getJSONObject("phone");
                //String mobile = phone.getString("mobile");
                //String home = phone.getString("home");
                //String office = phone.getString("office");

                // add movie to movies list
                movies.add(new Movie(id, title, year, director, rating, star, genres));
                //Need to call this if second time creating this activity
                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            Log.d("Json parsing error: ", e.getMessage());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Json parsing error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    public void nextOnclick(View view){
        EditText mEdit;
        mEdit = (EditText)findViewById(R.id.searchBox);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // 10.0.2.2 is the host machine when running the android emulator
        page += 1;
        Log.d("url", url+page);
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url+page,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.length() != 2){

                            ArrayList<Movie> movies = new ArrayList<>();

                            MovieListViewAdapter adapter = new MovieListViewAdapter(movies, SearchResult.this);

                            ListView listView = (ListView)findViewById(R.id.list);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Person person = people.get(position);
                                    Movie movie = movies.get(position);
                                    String message = String.format("Clicked on position: %d, id: %s", position, movie.getId());
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    Intent goToIntent = new Intent(SearchResult.this, SingleMovieActivity.class);

                                    goToIntent.putExtra("id", movie.getId());
                                    goToIntent.putExtra("rating", movie.getRating());
                                    //goToIntent.putExtra("message", msg);

                                    startActivity(goToIntent);
                                }
                            });

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Person person = people.get(position);
                                    Movie movie = movies.get(position);
                                    String message = String.format("Clicked on position: %d, id: %s", position, movie.getId());
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    Intent goToIntent = new Intent(SearchResult.this, SingleMovieActivity.class);

                                    goToIntent.putExtra("id", movie.getId());
                                    goToIntent.putExtra("rating", movie.getRating());
                                    //goToIntent.putExtra("message", msg);

                                    startActivity(goToIntent);
                                }
                            });

                            try {
                                JSONArray jsonArray = new JSONArray(response);


                                // looping through All Contacts
                                // looping through All Contacts
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    String id = c.getString("movie_id");
                                    String title = c.getString("movie_title");
                                    String year = c.getString("movie_year");
                                    String director = c.getString("movie_director");
                                    String rating = c.getString("movie_rating");
                                    String star = "";
                                    JSONArray starJsonArray = new JSONArray(c.getString("movie_stars"));
                                    for (int j = 0; j < starJsonArray.length(); j++){
                                        star+=starJsonArray.getJSONObject(j).getString("star_name") + " ";
                                    }
                                    String genres = "";
                                    JSONArray genreJsonArray = new JSONArray(c.getString("movie_genres"));
                                    for (int j = 0; j < genreJsonArray.length(); j++){
                                        genres+=genreJsonArray.getJSONObject(j).getString("genre_name") + " ";
                                    }
                                    //String star = c.getString("movie_rating");

                                    // Phone node is JSON Object
                                    //JSONObject phone = c.getJSONObject("phone");
                                    //String mobile = phone.getString("mobile");
                                    //String home = phone.getString("home");
                                    //String office = phone.getString("office");

                                    // add movie to movies list
                                    movies.add(new Movie(id, title, year, director, rating, star, genres));
                                    //Need to call this if second time creating this activity
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                Log.d("Json parsing error: ", e.getMessage());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Json parsing error: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                            }

                        }else{
                            Toast.makeText(getApplicationContext(), "Already last page!", Toast.LENGTH_SHORT).show();
                        }




                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("search", error.toString());
                    }
                }
        );

        queue.add(searchRequest);
    }

    public void prevOnclick(View view){
        EditText mEdit;
        mEdit = (EditText)findViewById(R.id.searchBox);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // 10.0.2.2 is the host machine when running the android emulator

        Log.d("url", url+page);

        if(page == 1){
            Toast.makeText(getApplicationContext(), "Already first page!", Toast.LENGTH_SHORT).show();
        }else{
            page -=1;
            final StringRequest searchRequest = new StringRequest(Request.Method.GET, url+page,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            ArrayList<Movie> movies = new ArrayList<>();

                            MovieListViewAdapter adapter = new MovieListViewAdapter(movies, SearchResult.this);

                            ListView listView = (ListView)findViewById(R.id.list);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Person person = people.get(position);
                                    Movie movie = movies.get(position);
                                    String message = String.format("Clicked on position: %d, id: %s", position, movie.getId());
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    Intent goToIntent = new Intent(SearchResult.this, SingleMovieActivity.class);

                                    goToIntent.putExtra("id", movie.getId());
                                    goToIntent.putExtra("rating", movie.getRating());
                                    //goToIntent.putExtra("message", msg);

                                    startActivity(goToIntent);
                                }
                            });

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Person person = people.get(position);
                                    Movie movie = movies.get(position);
                                    String message = String.format("Clicked on position: %d, id: %s", position, movie.getId());
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    Intent goToIntent = new Intent(SearchResult.this, SingleMovieActivity.class);

                                    goToIntent.putExtra("id", movie.getId());
                                    goToIntent.putExtra("rating", movie.getRating());
                                    //goToIntent.putExtra("message", msg);

                                    startActivity(goToIntent);
                                }
                            });

                            try {
                                JSONArray jsonArray = new JSONArray(response);


                                // looping through All Contacts
                                // looping through All Contacts
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    String id = c.getString("movie_id");
                                    String title = c.getString("movie_title");
                                    String year = c.getString("movie_year");
                                    String director = c.getString("movie_director");
                                    String rating = c.getString("movie_rating");
                                    String star = "";
                                    JSONArray starJsonArray = new JSONArray(c.getString("movie_stars"));
                                    for (int j = 0; j < starJsonArray.length(); j++){
                                        star+=starJsonArray.getJSONObject(j).getString("star_name") + " ";
                                    }
                                    String genres = "";
                                    JSONArray genreJsonArray = new JSONArray(c.getString("movie_genres"));
                                    for (int j = 0; j < genreJsonArray.length(); j++){
                                        genres+=genreJsonArray.getJSONObject(j).getString("genre_name") + " ";
                                    }
                                    //String star = c.getString("movie_rating");

                                    // Phone node is JSON Object
                                    //JSONObject phone = c.getJSONObject("phone");
                                    //String mobile = phone.getString("mobile");
                                    //String home = phone.getString("home");
                                    //String office = phone.getString("office");

                                    // add movie to movies list
                                    movies.add(new Movie(id, title, year, director, rating, star, genres));
                                    //Need to call this if second time creating this activity
                                    adapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                Log.d("Json parsing error: ", e.getMessage());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Json parsing error: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            if(response.length() != 2){





                            }else{

                            }




                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("search", error.toString());
                        }
                    }
            );
            queue.add(searchRequest);
        }




    }

    public void goHome(View view) {

        Intent goToIntent = new Intent(SearchResult.this, MainActivity.class);

        startActivity(goToIntent);
    }
}
