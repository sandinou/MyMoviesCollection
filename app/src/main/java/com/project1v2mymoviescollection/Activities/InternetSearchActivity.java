package com.project1v2mymoviescollection.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.project1v2mymoviescollection.Functions.Functions;
import com.project1v2mymoviescollection.Functions.InternetMovieAdapter;
import com.project1v2mymoviescollection.Functions.MyMovie;
import com.project1v2mymoviescollection.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 *  This class allows to search for a specific movie on the API, returns a listview
 */
public class InternetSearchActivity extends AppCompatActivity {

    private EditText searchET;
    private ImageButton searchBtn;
    private InternetMovieAdapter adapter;
    private ListView listView;
    private String t = "", message="";
    private int result=0;
    ArrayList<MyMovie> movie = new ArrayList<>();

    /**
     * Creation of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_search);

        searchET = (EditText) findViewById(R.id.searchET);
        searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        listView = (ListView)findViewById(R.id.moviesLV);

        adapter = new InternetMovieAdapter(this,R.layout.internet_my_list_item);

        /*searchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* t=searchET.getText().toString().replaceAll(" ","+");

                if (!t.trim().equals("")) {
                    //DownloadMovieTitle downloadMovieTitle = new DownloadMovieTitle();
                    //downloadMovieTitle.execute();
                }
                else
                    adapter.clear();*/
            /*}

            @Override
            public void afterTextChanged(Editable s) {
                t=searchET.getText().toString().replaceAll(" ","+");
                adapter.clear();
                DownloadMovieTitle downloadMovieTitle = new DownloadMovieTitle();
                downloadMovieTitle.execute();
                //if (t.trim().equals(""))
                  //  Toast.makeText(InternetSearchActivity.this, "Enter title !!", Toast.LENGTH_SHORT).show();
            }
        });*/


        /**
         *  Defines the action to do when clicking on the search button
         */
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = v;
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                t=searchET.getText().toString().replaceAll(" ","+");

                /**
                 *  Search movies corresponding
                 */
                if (!t.trim().equals("")) {
                    DownloadMovieTitle downloadMovieTitle = new DownloadMovieTitle();
                    downloadMovieTitle.execute();
                    listView.setAdapter(adapter);
                }
                else {
                    adapter.clear();
                    Toast.makeText(InternetSearchActivity.this, "Enter title !!", Toast.LENGTH_SHORT).show();
                }
                listView.setAdapter(adapter);
            }
        });


        /**
         *  If the user click on an element, tranfers data to the InternetAddActivity
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new Functions().addMovieFromInternet(InternetSearchActivity.this,adapter,position);
            }
        });
    }

    /**
     *  Associates the menu with the layout
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.internet_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.cancel){
            Intent cancel = new Intent(InternetSearchActivity.this,MainActivity.class);
            cancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(cancel);
            finish();
        }
        return true;
    }

    /**
     *  This class verifies if there are movies containing the title entered by the user.
     *  If this is the case, returns a list of all matching movies
     */
    private  class DownloadMovieTitle extends AsyncTask<String, Void, ArrayList<MyMovie>> {

        private String URL = "http://www.omdbapi.com/?s="+t+"&type=movie";
        private ProgressDialog mProgressDialog;

        /**
         *  A progress dialog is displayed during the search
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(InternetSearchActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Download");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();

        }

        /**
         * This function checks the internet connection and the existence of the movie sought
         * If the movie exists, returns list of corresponding movies
         * @param params
         * @return
         */
        @Override
        protected ArrayList<MyMovie> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader;
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(String.format(URL));//http://www.omdbapi.com/?s=forrest+gump&type=movie
                connection = (HttpURLConnection) url.openConnection();

                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    // return "Error from server!";
                    //return movie;
                    message = "Problem with your connection, please try later";
                    return null;
                }
                else
                    message = "Problem with your connection, please try later";


                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();

                while(line != null){
                    builder.append(line);
                    line = reader.readLine();
                }

                JSONObject root = new JSONObject(builder.toString());
               // r = root.getString("response");
                if (root.getString("Response").equals("True")) {
                    result = Integer.parseInt(root.getString("totalResults"));

                    JSONArray list = root.getJSONArray("Search");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject current = list.getJSONObject(i);
                        movie.add(new MyMovie(current.getString("Title").toUpperCase(), current.getString("Poster"), current.getString("Year"), current.getString("imdbID")));
                        message = "test: " + result + " ...";
                    }
                    return movie;
                }
                else
                    message = "This movie isn't in our database";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection!=null) {
                   // message = "Problem with your connection, please try later";

                    connection.disconnect();
                }
            }
            //return null;
            return movie;
        }

        @Override
        protected void onPostExecute(ArrayList<MyMovie> resultJSON) {
            super.onPostExecute(resultJSON);

            if (resultJSON.isEmpty())
                Toast.makeText(InternetSearchActivity.this,message, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(InternetSearchActivity.this,message, Toast.LENGTH_SHORT).show();

            adapter.clear();
            adapter.addAll(resultJSON);
            mProgressDialog.dismiss();
        }
    }
}
