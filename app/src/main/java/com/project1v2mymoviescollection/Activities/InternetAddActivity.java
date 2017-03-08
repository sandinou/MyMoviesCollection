package com.project1v2mymoviescollection.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project1v2mymoviescollection.Constants.And.SQL.MyMoviesSQLHelper;
import com.project1v2mymoviescollection.Functions.Functions;
import com.project1v2mymoviescollection.R;
import com.squareup.picasso.Picasso;

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
 *  This class creates a new movie sheet with data from internet API
 */
public class InternetAddActivity extends AppCompatActivity {

    private EditText title, runtime, director, writer, actors, storyLine, url;
    private TextView releaseDate;
    private ImageButton show;
    private String imdbID,genre, imageString;
    private CheckBox action, animation, adventure, comedy, drama, horror, western, thriller, sf, romance, crime, history, war, fantasy, bio;
    private int id;
    private ProgressDialog mProgressDialog;
    private Bitmap poster;
    private ImageView image;
    private MyMoviesSQLHelper myMoviesSQLHelper;
    private int currentPosition = 0, year;

    /**
     * Creation of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_movie);

        title = (EditText) findViewById(R.id.titleET);
        title.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        director = (EditText) findViewById(R.id.directorET);
        writer = (EditText) findViewById(R.id.writerET);
        actors = (EditText) findViewById(R.id.actorsET);
        storyLine = (EditText) findViewById(R.id.storyLineET);
        releaseDate = (TextView) findViewById(R.id.releaseDateTV);
        runtime = (EditText) findViewById(R.id.runtimeET);
        url = (EditText)findViewById(R.id.urlET);
        image = (ImageView)findViewById(R.id.imageView);
        show = (ImageButton) findViewById(R.id.showIB);
        action = (CheckBox) findViewById(R.id.actionCB);
        animation = (CheckBox) findViewById(R.id.animationCB);
        adventure = (CheckBox) findViewById(R.id.adventureCB);
        comedy = (CheckBox) findViewById(R.id.comedyCB);
        drama = (CheckBox) findViewById(R.id.dramaCB);
        horror = (CheckBox) findViewById(R.id.horrorCB);
        western = (CheckBox) findViewById(R.id.westernCB);
        thriller = (CheckBox) findViewById(R.id.thrillerCB);
        sf = (CheckBox) findViewById(R.id.sfCB);
        romance = (CheckBox) findViewById(R.id.romanceCB);
        crime = (CheckBox) findViewById(R.id.crimeCB);
        history = (CheckBox) findViewById(R.id.historyCB);
        war = (CheckBox) findViewById(R.id.warCB);
        fantasy = (CheckBox) findViewById(R.id.fantasyCB);
        bio = (CheckBox) findViewById(R.id.biographyCB);


        show.setVisibility(View.INVISIBLE);
        getMovieID();

        /******  Intent to MainActivity ******/
        id = getIntent().getIntExtra("_id",-1);
        /*******************/

        myMoviesSQLHelper = new MyMoviesSQLHelper(this);
    }

    /**
     *  Function who recovers the movie's API_ID from InternetSearchActivity
     */
    private void getMovieID(){
        imdbID = getIntent().getStringExtra("id");
        new DownloadMovieInfos().execute();
    }


    /**
     *  Class to download movie's information from the API
     */
    public  class DownloadMovieInfos extends AsyncTask<Object, Object, ArrayList<String>> {
        private String URL = "http://www.omdbapi.com/?i="+ imdbID+"&plot=full";


        /**
         *  A progress dialog is displayed during data loading
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(InternetAddActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Download");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            // Show progressdialog
            mProgressDialog.show();
        }

        /**
         * We retrieve all the data we need in a arraylist, using json format
         * @param params
         * @return the arraylist with all informations
         */
        @Override
        protected ArrayList<String> doInBackground(Object... params) {

            HttpURLConnection connection = null;
            BufferedReader reader;
            StringBuilder builder = new StringBuilder();

            try {
                java.net.URL url = new URL(String.format(URL));
                connection = (HttpURLConnection) url.openConnection();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
//                    return "Error from server!";
                    return null;

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();

                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }

                String jsonString =  builder.toString();
                JSONObject root = new JSONObject(jsonString);
                ArrayList<String> data = new ArrayList<>();

                /*0*/
                data.add(root.getString("Title"));
                /*1*/
                String date = root.getString("Released");
                if(!date.equals("N/A")) {
                    String[] d = date.split(" ");
                    data.add(d[0] + " "+new Functions().setMonth(d[1])+" "+d[2]);
                }
                else data.add("N/A");
                /*2*/
                String time = root.getString("Runtime");
                String[]t = time.split(" ");
                data.add(t[0]);
                /*3*/
                data.add(root.getString("Director"));
                /*4*/
                data.add(root.getString("Writer"));
                /*5*/
                data.add(root.getString("Actors"));
                /*6*/
                data.add(root.getString("Plot"));
                /*7*/
                data.add(root.getString("Poster"));
                /*8*/
                String s = root.getString("Genre");
                String[]S = s.split(",");
                data.add(new Functions().setGenre(S));

                return data;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }

        /**
         * We display all the informations retrieved in the layout
         * @param resultJSON
         */
        @Override
        protected void onPostExecute(ArrayList<String> resultJSON) {
            super.onPostExecute(resultJSON);
            if(resultJSON!= null) {
                title.setText(resultJSON.get(0).toString().toUpperCase());
                releaseDate.setText(resultJSON.get(1).toString());
                runtime.setText(resultJSON.get(2).toString());
                director.setText(resultJSON.get(3).toString());
                writer.setText(resultJSON.get(4).toString());
                actors.setText(resultJSON.get(5).toString());
                storyLine.setText(resultJSON.get(6).toString());

                /**
                 * If the movie has no poster, defines a defaut image
                 */
                if (resultJSON.get(7).toString().equals("N/A")) {
                    url.setText("");
                    image.setImageResource(R.drawable.movies_icon);
                }
                else {
                    url.setText(resultJSON.get(7).toString());
                    Picasso.with(InternetAddActivity.this).load(url.getText().toString()).into(image);
                }
                genre = resultJSON.get(8).toString();

                /**
                 *  Converts genre string on checkBox status
                 */
                new Functions().checkedGenre(genre,action,animation,adventure,comedy,drama,horror,western,thriller,romance,sf,crime,history,war,fantasy,bio);
                setTitle(title.getText().toString());
                mProgressDialog.dismiss();
            }
        }
    }

    /**
     *  Associates the menu with the layout
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        currentPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        getMenuInflater().inflate(R.menu.save_menu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    /**
     *  Defines the menu's options to be performed
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /**
             *  Save all the data on database
             */
            case R.id.saveItem:
                String[] d = releaseDate.getText().toString().split(" ");
                if (d.length==3)
                    year = Integer.parseInt(d[2]);
                else year=0;

                poster=((BitmapDrawable)image.getDrawable()).getBitmap();
                imageString = new Functions().encodeToBase64(poster,Bitmap.CompressFormat.JPEG,100);
                myMoviesSQLHelper.save(InternetAddActivity.this,id,title.getText().toString(),releaseDate.getText().toString(),year,runtime.getText().toString(),director.getText().toString(),writer.getText().toString(),genre,actors.getText().toString(),storyLine.getText().toString(),url.getText().toString(),imageString,imdbID,"0");
                break;

            /**
             *  Return to the mainActivity if the user cancel its action
             */
            case R.id.cancel:
                Intent cancel = new Intent(InternetAddActivity.this, InternetSearchActivity.class);
                cancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(cancel);
                finish();
                break;
        }
        return true;
    }
}


