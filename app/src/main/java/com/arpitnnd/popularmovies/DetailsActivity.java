package com.arpitnnd.popularmovies;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

public class DetailsActivity extends AppCompatActivity {

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("position", 0);
        MovieDetails movieDetails = new MovieDetails();
        try {
            movieDetails = APITools.getMovieDetails(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.title)).setText(movieDetails.movieTitle);
        Drawable d = this.getResources().getDrawable(R.drawable.loading);
        findViewById(R.id.poster).getLayoutParams().width = getResources().getDisplayMetrics().widthPixels / 2;
        findViewById(R.id.poster).requestLayout();
        Glide.with(this).load("http://image.tmdb.org/t/p/w185/" + movieDetails.posterPath)
                .placeholder(d).into((ImageView) findViewById(R.id.poster));
        ((TextView) findViewById(R.id.date)).setText(movieDetails.releaseDate);
        ((TextView) findViewById(R.id.rating)).setText(movieDetails.voteAverage);
        ((TextView) findViewById(R.id.overview)).setText(movieDetails.plot);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*Handle action bar item clicks here. The action bar will
          automatically handle clicks on the Home/Up button, as long
          as a parent activity is specified in AndroidManifest.xml.*/
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
