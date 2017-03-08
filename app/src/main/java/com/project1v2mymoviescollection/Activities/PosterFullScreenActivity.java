package com.project1v2mymoviescollection.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.project1v2mymoviescollection.Constants.And.SQL.DBConstants;
import com.project1v2mymoviescollection.Functions.Functions;
import com.project1v2mymoviescollection.R;

/**
 *  This class allows to watch a picture on full screen
 */
public class PosterFullScreenActivity extends AppCompatActivity {

    private ImageView fullPoster;

    /**
     *  Creation of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_full_screen);
        getSupportActionBar().hide();

        fullPoster = (ImageView)findViewById(R.id.fullPoster);

        Intent intent = getIntent();
        String imageString = intent.getStringExtra(DBConstants.POSTER_COLUMN);
        Bitmap bitmap = new Functions().decodeBase64(imageString);
        fullPoster.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        finish();
    }
}
