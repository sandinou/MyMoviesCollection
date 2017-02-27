package com.project1v2mymoviescollection.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class InternetAddActivity extends AppCompatActivity {

    private EditText title, runtime, director, writer, actors, storyLine, url;
    private TextView releaseDate;
    private ImageButton show;
    private String imdbID,genre, imageString;
    private CheckBox action, animation, adventure, comedy, drama, horror, western, thriller, sf, romance, crime, history, war, fantasy, bio;;
    private int id;
    private ProgressDialog mProgressDialog;
    private Bitmap poster;
    private ImageView image;
    private MyMoviesSQLHelper myMoviesSQLHelper;
    private int currentPosition = 0, year;


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
     *  Function who recovers the movie's ID from InternetSearchActivity
     */
    private void getMovieID(){
        imdbID = getIntent().getStringExtra("id");
        new DownloadMovieInfos().execute();
    }



    public  class DownloadMovieInfos extends AsyncTask<Object, Object, ArrayList<String>> {
        private String URL = "http://www.omdbapi.com/?i="+ imdbID;

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
                    data.add(d[0] + " " + Functions.setMonth(d[1]) + " " + d[2]);
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
                data.add(Functions.setGenre(S));

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
                if (resultJSON.get(7).toString().equals("N/A")) {
                    url.setText("");
                    image.setImageResource(R.drawable.movies_icon);
                    mProgressDialog.dismiss();
                }
                else {
                    url.setText(resultJSON.get(7).toString());
                    new DownloadImage().execute(url.getText().toString());
                }
                genre = resultJSON.get(8).toString();

                Functions.checkedGenre(genre,action,animation,adventure,comedy,drama,horror,western,thriller,romance,sf,crime,history,war,fantasy,bio);
                setTitle(title.getText().toString());
            }
        }
    }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveItem:
                String[] d = releaseDate.getText().toString().split(" ");
               // int year = Integer.parseInt(d[2]);
                if (d.length==3)
                    year = Integer.parseInt(d[2]);
                else year=0;
                myMoviesSQLHelper.save(InternetAddActivity.this,id,title.getText().toString(),releaseDate.getText().toString(),year,runtime.getText().toString(),director.getText().toString(),writer.getText().toString(),genre,actors.getText().toString(),storyLine.getText().toString(),url.getText().toString(),imageString,imdbID);
                break;

            case R.id.cancel:
                Intent cancel = new Intent(InternetAddActivity.this, InternetSearchActivity.class);
                cancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(cancel);
                finish();
                break;
        }
        return true;
    }



    /**
     * Class to download image from URL with Async Task
     */

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        public Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;

            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        public void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            if (result!=null)
                poster = result;

            else
                //poster=R.drawable.movies_icon;
                poster = BitmapFactory.decodeResource(InternetAddActivity.this.getResources(), R.drawable.movies_icon);


            image.setImageBitmap(poster);
            imageString = Functions.encodeToBase64(poster, Bitmap.CompressFormat.JPEG, 100);
            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }
}


