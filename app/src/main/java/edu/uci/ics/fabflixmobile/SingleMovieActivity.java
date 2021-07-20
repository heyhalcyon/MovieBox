package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

public class SingleMovieActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        Bundle bundle = getIntent().getExtras();
        //Toast.makeText(this, "Page for: " + bundle.get("id") + ".", Toast.LENGTH_LONG).show();
        String movieId = bundle.get("id").toString();
        String movieRating = bundle.get("rating").toString();


        ArrayList<Person> stars = new ArrayList<>();
        PeopleListViewAdapter adapter = new PeopleListViewAdapter(stars, this);
        ListView listView = (ListView)findViewById(R.id.starList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // 10.0.2.2 is the host machine when running the android emulator
        final StringRequest movieInfoRequest = new StringRequest(Request.Method.GET, "https://18.191.232.58:8443/project1/api/single-movie?id="+movieId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("index.reponse", response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            JSONObject movieInfo = jsonArray.getJSONObject(0);

                            String id = movieInfo.getString("movie_id");
                            String title = movieInfo.getString("movie_title");
                            String year = movieInfo.getString("movie_year");
                            String director = movieInfo.getString("movie_director");
                            //String rating = movieInfo.getString("movie_rating");
                            JSONArray movieGenres = movieInfo.getJSONArray("movie_genre");
                            String genres = "";
                            for (int i = 0; i < movieGenres.length(); i++) {

                                genres += movieGenres.getJSONObject(i).getString("movie_genre") + " ";
                            }

                            // set fields
                            ((TextView) findViewById(R.id.movieTitle)).setText(title);
                            ((TextView) findViewById(R.id.movieRating)).setText(movieRating);
                            ((TextView) findViewById(R.id.movieGenre)).setText(genres);
                            ((TextView) findViewById(R.id.movieYear)).setText(year);
                            ((TextView) findViewById(R.id.movieDirector)).setText(director);


                            JSONArray movieStars = movieInfo.getJSONArray("movie_stars");
                            for (int i = 0; i < movieStars.length(); i++) {

                                stars.add(new Person(movieStars.getJSONObject(i).getString("star_name"), movieStars.getJSONObject(i).getString("star_birth_year")));
                            }
                            adapter.notifyDataSetChanged();








                        } catch (final JSONException e) {
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("username.error", error.toString());
                    }
                }
        );



        queue.add(movieInfoRequest);
    }

    public void goHome(View view) {

        Intent goToIntent = new Intent(SingleMovieActivity.this, MainActivity.class);

        startActivity(goToIntent);
    }
}
