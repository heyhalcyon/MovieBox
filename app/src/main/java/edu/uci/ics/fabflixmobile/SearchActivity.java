package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.view.View;
import android.widget.EditText;


public class SearchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

    }


    public void searchOnClick(View view){
        EditText mEdit;
        mEdit = (EditText)findViewById(R.id.searchBox);
        String searchTitle = mEdit.getText().toString();

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // 10.0.2.2 is the host machine when running the android emulator
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, "https://18.191.232.58:8443/project1/api/ft?search="+searchTitle+"&rpp=5&p=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.d("result:", response);
                        Intent goToIntent = new Intent(SearchActivity.this, SearchResult.class);
                        goToIntent.putExtra("url", "https://18.191.232.58:8443/project1/api/ft?search="+searchTitle+"&rpp=5&p=");
                        goToIntent.putExtra("response", response);
                        goToIntent.putExtra("page", 1);
                        startActivity(goToIntent);
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
