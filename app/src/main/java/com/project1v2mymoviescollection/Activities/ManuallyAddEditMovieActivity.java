package com.project1v2mymoviescollection.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project1v2mymoviescollection.Constants.And.SQL.DBConstants;
import com.project1v2mymoviescollection.Constants.And.SQL.MyMoviesSQLHelper;
import com.project1v2mymoviescollection.Functions.CheckConnection;
import com.project1v2mymoviescollection.Functions.Functions;
import com.project1v2mymoviescollection.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.jar.Manifest;

/**
 *  This class creates a new movie sheet with the data entered by the user
 */
public class ManuallyAddEditMovieActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1, RESULT_LOAD_IMG = 2;
    private EditText title, runtime, director, writer, actors, storyLine, url;
    private TextView releaseDate;
    private String genre1 = "", date="", genre2 = "",watched_state;
    private String imageString, toastMessage, imdbId = null;
    private ImageView image;
    private ImageButton show;
    private int id, year;
    private Bitmap poster;
    private CheckBox action, animation, adventure, comedy, drama, horror, western, thriller, sf, romance, crime, history, war, fantasy, bio;
    private MyMoviesSQLHelper myMoviesSQLHelper;
    public Cursor cursor;
    public int currentPosition = 0;


    /**
     * Creates the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_movie);

        title = (EditText) findViewById(R.id.titleET);
        releaseDate = (TextView) findViewById(R.id.releaseDateTV);
        runtime = (EditText) findViewById(R.id.runtimeET);
        director = (EditText) findViewById(R.id.directorET);
        director.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        writer = (EditText) findViewById(R.id.writerET);
        writer.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        actors = (EditText) findViewById(R.id.actorsET);
        actors.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        storyLine = (EditText) findViewById(R.id.storyLineET);
        storyLine.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        url = (EditText) findViewById(R.id.urlET);
        image = (ImageView) findViewById(R.id.imageView);
        show = (ImageButton) findViewById(R.id.showIB);
        //  photo = (ImageButton)findViewById(R.id.photoIB);
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

        registerForContextMenu(show);

        id = getIntent().getIntExtra("_id", -1);
        currentPosition = getIntent().getIntExtra("position", 1);
        myMoviesSQLHelper = new MyMoviesSQLHelper(this);
        releaseDate.setOnClickListener(this);
        show.setOnClickListener(this);

        cursor = myMoviesSQLHelper.getReadableDatabase().query(DBConstants.TABLE_NAME, null, DBConstants.ID_COLUMN + "=?", new String[]{" " + id}, null, null, null);
        cursor.moveToPosition(currentPosition);

        if (id != -1) {
            myMoviesSQLHelper.editMovie(cursor, title, releaseDate, runtime, director, writer, genre2, actors, storyLine, url, imageString, action, animation, adventure, comedy, drama, horror, western, thriller, romance, sf, crime, history, war, fantasy, bio, image);
            watched_state=cursor.getString(cursor.getColumnIndex(DBConstants.WATCHED_COLUMN));
        }
        else
            watched_state="0";

        setTitle(title.getText().toString());
        
    }

    /**
     *  Defines onClick actions
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * Puts a date picker to set the release date of the movie
             */
            case R.id.releaseDateTV:
                Calendar cal = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            /**
             * Shows differents option to get the movie poster
             */
            case R.id.showIB:
                openContextMenu(show);
                break;
        }
    }

    /**
     *  Defines the date from the date picker
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        releaseDate.setText(new Functions().setDate(dayOfMonth,month,year,date));

    }

    /**
     * Defines the menu's options to be performed
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /**
             * Save the new movie sheet in database
             */
            case R.id.saveItem:
                if (title.getText().toString().trim().equals(""))
                    Toast.makeText(this, "You can't create a movie sheet without title!", Toast.LENGTH_SHORT).show();
                else {
                    genre1 = new Functions().setGenre(action, adventure, animation, comedy, drama, horror, western, thriller, sf, romance, crime, history, war, fantasy, bio);
                    String[] d = releaseDate.getText().toString().split(" ");
                    if (d.length == 3)
                        year = Integer.parseInt(d[2]);
                    else year = 0;

                    if (image.getDrawable() == null)
                        image.setImageResource(R.drawable.movies_icon);

                    poster = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    imageString = new Functions().encodeToBase64(poster,Bitmap.CompressFormat.JPEG,100);

                    myMoviesSQLHelper.save(ManuallyAddEditMovieActivity.this, id, title.getText().toString(), releaseDate.getText().toString(), year, runtime.getText().toString(), director.getText().toString(), writer.getText().toString(), genre1, actors.getText().toString(), storyLine.getText().toString(), url.getText().toString(), imageString, imdbId,watched_state);
                }
                break;
            case R.id.cancel:
                Intent cancel = new Intent(ManuallyAddEditMovieActivity.this, MainActivity.class);
                startActivity(cancel);
                break;
        }
        return true;
    }


    /**
     * Associates the menu with the layout
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    /**
     * Associates the context menu with the layout
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        if (v.getId()==R.id.showIB)
            getMenuInflater().inflate(R.menu.poster_menu, menu);
    }

    /**
     * Defines the context menu's options to be performed
     * @param item
     * @return super.onContextItemSelected(item)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /**
             * Takes photo with the camera
             */
            case R.id.camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                break;
            /**
             * Download picture from internet url
             */
            case R.id.url:
                CheckConnection checkConnection = new CheckConnection(ManuallyAddEditMovieActivity.this);
                if (!checkConnection.isNetworkAvailable())
                    Toast.makeText(ManuallyAddEditMovieActivity.this, "Please connect to internet !", Toast.LENGTH_SHORT).show();
                if (!url.getText().toString().trim().equals(""))
                    new checkUrl().execute(url.getText().toString());
                else {
                    toastMessage = "URL empty !!";
                    new Functions().URLEmpty(ManuallyAddEditMovieActivity.this,toastMessage);
                }
               break;
            /**
             * Picks a picture from the device pictures gallery
             */
            case R.id.gallery:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode== RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    image.setImageBitmap(imageBitmap);
                }
                break;
            case RESULT_LOAD_IMG:
                if (resultCode == RESULT_OK && null != data) {
                    // Get the Image from data
                    Uri selectedImage = data.getData();
                    Picasso.with(this).load(selectedImage).into(image);
                } else
                    Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                break;
        }
    }


    /**
     * Checks if the entered url is valid/exist
     */
    public class checkUrl extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        public Boolean doInBackground(String... URL) {
            String imageURL = URL[0];
            try {
                InputStream input = new URL(imageURL).openStream();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result)
                Picasso.with(ManuallyAddEditMovieActivity.this).load(url.getText().toString()).into(image);
            else {
                toastMessage = "URL not valid !!";
                new Functions().URLEmpty(ManuallyAddEditMovieActivity.this,toastMessage);
            }
        }
    }
}
