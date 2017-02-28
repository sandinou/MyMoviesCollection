package com.project1v2mymoviescollection.Functions;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.util.Base64;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project1v2mymoviescollection.Activities.InternetAddActivity;
import com.project1v2mymoviescollection.Activities.InternetSearchActivity;
import com.project1v2mymoviescollection.Activities.ManuallyAddEditMovieActivity;
import com.project1v2mymoviescollection.Constants.And.SQL.DBConstants;
import com.project1v2mymoviescollection.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by SandraMac on 22/02/2017.
 *
 * This class regroups functions to save the data in the database
 */

public class Functions {

    /**
     * Function to share movie's informations for ViewMovieActivity
     * @param context
     * @param cursor

     */
    public static void share(Context context, Cursor cursor){

       cursor.moveToFirst();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, MovieInformations(cursor));
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }



    /**
     * Function to set the checkboxes state from ManuallyAddEditMovieActivity or from InternetAddActivity
     * @param genre
     * @param action
     * @param animation
     * @param adventure
     * @param comedy
     * @param drama
     * @param horror
     * @param western
     * @param thriller
     * @param romance
     * @param sf
     * @param crime
     * @param history
     * @param war
     * @param fantasy
     * @param bio
     */
    public static void checkedGenre(String genre, CheckBox action, CheckBox animation, CheckBox adventure, CheckBox comedy, CheckBox drama, CheckBox horror,
                                    CheckBox western, CheckBox thriller, CheckBox romance, CheckBox sf, CheckBox crime, CheckBox history, CheckBox war, CheckBox fantasy, CheckBox bio){
        if (genre != null) {
            if (genre.contains("Action"))
                action.setChecked(true);
            if (genre.contains("Animation"))
                animation.setChecked(true);
            if (genre.contains("Adventure"))
                adventure.setChecked(true);
            if (genre.contains("Comedy"))
                comedy.setChecked(true);
            if (genre.contains("Drama"))
                drama.setChecked(true);
            if (genre.contains("Horror"))
                horror.setChecked(true);
            if (genre.contains("Western"))
                western.setChecked(true);
            if (genre.contains("Thriller"))
                thriller.setChecked(true);
            if (genre.contains("Science-Fiction"))
                sf.setChecked(true);
            if (genre.contains("Romance"))
                romance.setChecked(true);
            if (genre.contains("Crime"))
                crime.setChecked(true);
            if (genre.contains("History"))
                history.setChecked(true);
            if (genre.contains("War"))
                war.setChecked(true);
            if (genre.contains("Fantasy"))
                fantasy.setChecked(true);
            if (genre.contains("Biography"))
                bio.setChecked(true);
        }
    }


    /**
     * Function to save the title Checked Boxes in string from ManuallyAddEditMovieActivity
     * @param action
     * @param adventure
     * @param animation
     * @param comedy
     * @param drama
     * @param horror
     * @param western
     * @param thriller
     * @param sf
     * @param romance
     * @param crime
     * @param history
     * @param war
     * @param fantasy
     * @param bio
     * @return
     */
    public static String setGenre(CheckBox action, CheckBox adventure, CheckBox animation, CheckBox comedy, CheckBox drama, CheckBox horror, CheckBox western, CheckBox thriller, CheckBox sf,
                                  CheckBox romance, CheckBox crime, CheckBox history, CheckBox war, CheckBox fantasy, CheckBox bio) {

        ArrayList<String> categories= new ArrayList<>();
        String genre="";

        if (action.isChecked())
            categories.add(action.getText().toString());
        if (adventure.isChecked())
            categories.add(adventure.getText().toString());
        if (animation.isChecked())
            categories.add(animation.getText().toString());
        if (comedy.isChecked())
            categories.add(comedy.getText().toString());
        if (drama.isChecked())
            categories.add(drama.getText().toString());
        if (horror.isChecked())
            categories.add(horror.getText().toString());
        if (western.isChecked())
            categories.add(western.getText().toString());
        if (thriller.isChecked())
            categories.add(thriller.getText().toString());
        if (sf.isChecked())
            categories.add(sf.getText().toString());
        if (romance.isChecked())
            categories.add(romance.getText().toString());
        if (crime.isChecked())
            categories.add(crime.getText().toString());
        if (history.isChecked())
            categories.add(history.getText().toString());
        if (war.isChecked())
            categories.add(war.getText().toString());
        if (fantasy.isChecked())
            categories.add(fantasy.getText().toString());
        if (bio.isChecked())
            categories.add(bio.getText().toString());

        for (int i=0;i<categories.size()-1;i++)
            genre+=categories.get(i).toString()+", ";
        if (!categories.isEmpty())
            genre+=categories.get(categories.size()-1).toString();

        return genre;
    }

    /**
     * Function to transform movie's runtime in minutes into hours for ViewActivity
     * @param num
     * @return string time
     */
    public static String minutesInHours(String num){

        String time="";
        int mins,h,hours;
        if(!num.equals("")) {
            int t = Integer.valueOf(num);
            mins = t % 60;
            h = t - mins;
            hours = h / 60;
            time = hours + "h" + mins + " min";
        }
        return time;
    }


    /**
     * Function to set the month in the releaseDate for InternetAddActivity
     * @param month
     * @return new month
     */
    public static String setMonth(String month){
        switch (month){
            case "Jan":
                month="January";
                break;
            case "Feb":
                month="February";
                break;
            case "Mar":
                month="March";
                break;
            case "Apr":
                month="April";
                break;
            case "Jun":
                month="June";
                break;
            case "Jul":
                month="July";
                break;
            case "Aug":
                month="August";
                break;
            case "Sep":
                month="September";
                break;
            case "Oct":
                month="October";
                break;
            case "Nov":
                month="November";
                break;
            case "Dec":
                month="December";
                break;
        }
        return month;
    }


    /**
     * Function to set the movie's genres in string for InternetAddActivity
     * @param s
     * @return string genre
     */
    public static String setGenre(String[] s){
        String genre="";
        for (int i=0;i<s.length;i++){
            if (s[i].contains("Sci-Fi"))
                s[i]="Science-Fiction";
        }
        for (int i=0;i<s.length-1;i++)
            genre+=s[i]+", ";
        genre+=s[s.length-1];
        return genre;
    }

    /**
     * Function to transform Bitmap into String
     * @param image
     * @param compressFormat
     * @param quality
     * @return string
     */
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Function to transform String into Bitmap
     * @param input
     * @return bitmap
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    /**
     * Function to share movie's informations for ViewActivity
     * @param cursor
     * @return info
     */
    public static String MovieInformations(Cursor cursor){
        String id = cursor.getString(cursor.getColumnIndex(DBConstants.IMDB_ID_MOVIE_COLUMN));
        String URL;
        if (id!=null)
            URL = "http://www.imdb.com/title/"+id+"/?ref_=fn_al_tt_1";
        else{
            URL="TITLE:\t"+cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN))
                    +"\nRelease date:\t"+cursor.getString(cursor.getColumnIndex(DBConstants.RELEASE_DATE_COLUMN))
                    +"\nRuntime:\t"+ Functions.minutesInHours(cursor.getString(cursor.getColumnIndex(DBConstants.RUNTIME_COLUMN)))
                    +"\nDirector:\t"+cursor.getString(cursor.getColumnIndex(DBConstants.DIRECTOR_COLUMN))
                    +"\nWriter:\t"+cursor.getString(cursor.getColumnIndex(DBConstants.WRITER_COLUMN))
                    +"\nActors:\t"+cursor.getString(cursor.getColumnIndex(DBConstants.ACTORS_COLUMN))
                    +"\nGenre:\t"+cursor.getString(cursor.getColumnIndex(DBConstants.GENRE_COLUMN))
                    +"\nStory:\t"+cursor.getString(cursor.getColumnIndex(DBConstants.STORY_COLUMN))
                    +"\nUrl image:\t"+cursor.getString(cursor.getColumnIndex(DBConstants.URL_COLUMN));
        }
        return URL;
    }


    public static String setDate(int dayOfMonth,int month, int year, String date){
        String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String mon=MONTHS[month];
        date = dayOfMonth+" "+mon+" "+year;

        return date;
    }

    public static void URLEmpty(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        //the default toast view group is a relativelayout
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(18);
        toast.show();
    }

    public static void addMovieFromInternet(Context context, InternetMovieAdapter adapter, int position){
        Intent edit = new Intent(context,InternetAddActivity.class);
        edit.putExtra("id",adapter.getItem(position).getId());
        edit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(edit);
    }








   /* public static File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFile = "JPEG_"+timeStamp+"_";
        File photo = new File(Environment.getExternalStorageDirectory(),imageFile+".jpg");

        return photo;
    }*/


}
