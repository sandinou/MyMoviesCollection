package com.project1v2mymoviescollection.Functions;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project1v2mymoviescollection.Constants.And.SQL.DBConstants;
import com.project1v2mymoviescollection.Constants.And.SQL.MyMoviesSQLHelper;
import com.project1v2mymoviescollection.R;

/**
 * Created by SandraMac on 24/02/2017.
 */

public class MyMovieAdapter extends CursorAdapter {

    private TextView title;
    private TextView genre;
    private TextView year;
    private ImageView poster;
    private ImageButton watchedView;
    private boolean watch = false;
    private int watched,id;
    private MyMoviesSQLHelper myMoviesSQLHelper;


    public MyMovieAdapter(Context context, Cursor c) {
        super(context, c);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_movies_list,null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        title = (TextView)view.findViewById(R.id.titleTV);
        year = (TextView) view.findViewById(R.id.yearTV);
        genre = (TextView) view.findViewById(R.id.genreTV);
        poster = (ImageView)view.findViewById(R.id.posterIV);
        watchedView = (ImageButton) view.findViewById(R.id.imageButton2);
        //id = getIntent().getIntExtra("_id", -1);
      //  id=cursor.getPosition();


        String y = cursor.getString(cursor.getColumnIndex(DBConstants.RELEASE_DATE_COLUMN));
        String[] Year = y.split(" ");
        String image = cursor.getString(cursor.getColumnIndex(DBConstants.POSTER_COLUMN));

        if (cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)).contains("-")){
            String[] name = cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)).split("-");
            title.setText(name[0]);
        }
        else
            title.setText(cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)));

        genre.setText(cursor.getString(cursor.getColumnIndex(DBConstants.GENRE_COLUMN)));
        year.setText(Year[Year.length-1]);

        if (image!=null){
            Bitmap bitmap = Functions.decodeBase64(image);
            poster.setImageBitmap(bitmap);
        }
        watched=cursor.getInt(cursor.getColumnIndex(DBConstants.WATCHED_COLUMN));
        if (watched==0)
            watchedView.setImageResource(R.drawable.not_watched_black);
        else
            watchedView.setImageResource(R.drawable.watched_black);





    }


}
