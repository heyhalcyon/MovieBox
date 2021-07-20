package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import java.util.ArrayList;
import android.app.Activity;
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
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        ArrayList<Movie> movies = new ArrayList<>();
        //movies.add(new Movie("tt0395642", "Loma Lynda: Episode II", "2004", "Jason Bognacki", "9.7"));
        //movies.add(new Movie("tt0424773", "Addo: The King of the Beasts", "2002", "Hugo Van Lawick", "9.5"));

        //PeopleListViewAdapter adapter = new PeopleListViewAdapter(people, this);
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

                Intent goToIntent = new Intent(MainActivity.this, SingleMovieActivity.class);

                goToIntent.putExtra("id", movie.getId());
                goToIntent.putExtra("rating", movie.getRating());
                //goToIntent.putExtra("message", msg);

                startActivity(goToIntent);
            }
        });



        RequestQueue queue = NetworkManager.sharedManager(this).queue;


        // 10.0.2.2 is the host machine when running the android emulator
        StringRequest indexRequest = new StringRequest(Request.Method.GET, "https://18.191.232.58:8443/project1/api/index",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("index.reponse", response);

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
                                //String star = c.getString("movie_rating");

                                // Phone node is JSON Object
                                //JSONObject phone = c.getJSONObject("phone");
                                //String mobile = phone.getString("mobile");
                                //String home = phone.getString("home");
                                //String office = phone.getString("office");

                                // add movie to movies list
                                movies.add(new Movie(id, title, year, director, rating));
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("username.error", error.toString());
                    }
                }
        );


        queue.add(indexRequest);
    }


    public void goToSearch(View view) {

        Intent goToIntent = new Intent(this, SearchActivity.class);
        startActivity(goToIntent);
    }
}

