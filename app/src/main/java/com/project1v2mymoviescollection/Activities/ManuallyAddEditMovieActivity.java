package com.project1v2mymoviescollection.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class ManuallyAddEditMovieActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private EditText title, runtime, director, writer, actors, storyLine, url;
    private TextView releaseDate;
    private String genre1 = "", date, genre2 = "",watched_state;
    private String imageString, toastMessage, imdbId = null;
    private Uri cameraImageUri;
    private ImageView image;
    private ImageButton show;
    private ImageButton photo;
    private int id, year, state_watched;
    private Bitmap poster;
    private ProgressDialog mProgressDialog;
    private CheckBox action, animation, adventure, comedy, drama, horror, western, thriller, sf, romance, crime, history, war, fantasy, bio;
    private MyMoviesSQLHelper myMoviesSQLHelper;
    public Cursor cursor;
    public int currentPosition = 0;


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


        id = getIntent().getIntExtra("_id", -1);
        currentPosition = getIntent().getIntExtra("position", 1);
        myMoviesSQLHelper = new MyMoviesSQLHelper(this);
        releaseDate.setOnClickListener(this);
        show.setOnClickListener(this);
//        photo.setOnClickListener(this);


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.releaseDateTV:
                Calendar cal = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                releaseDate.setText(date);
                break;
            case R.id.showIB:

                View view = v;
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
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
            /*case R.id.photoIB:
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Bitmap bitmap;
                try {
                    File photo = ConstantsFunctions.createImageFile();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
                    cameraImageUri=Uri.fromFile(photo);
                    url.setText(cameraImageUri.toString());
                   // bitmap = android.provider.MediaStore.Images.Media.getBitmap()

                    startActivityForResult(intent,REQUEST_TAKE_PHOTO);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;*/
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        new Functions().setDate(dayOfMonth,month,year,date);
        releaseDate.setText(date);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveItem:
                if (title.getText().toString().trim().equals(""))
                    Toast.makeText(this, "You can't create a movie sheet without title!", Toast.LENGTH_SHORT).show();
                else {
                    genre1 = new Functions().setGenre(action, adventure, animation, comedy, drama, horror, western, thriller, sf, romance, crime, history, war, fantasy, bio);
                    String[] d = releaseDate.getText().toString().split(" ");
                    if (d.length == 3)
                        year = Integer.parseInt(d[2]);
                    else year = 0;

                    if (image.getDrawable() != null)
                        poster = ((BitmapDrawable) image.getDrawable()).getBitmap();

                    else {
                        image.setImageResource(R.drawable.movies_icon);
                        poster = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    }

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
            // Close progressdialog

        }
    }


}
