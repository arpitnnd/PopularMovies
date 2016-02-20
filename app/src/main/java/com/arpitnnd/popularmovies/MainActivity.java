package com.arpitnnd.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static APITools api;
    static GridView gridView;
    static boolean sortByPopularity;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        ArrayList<String> array = new ArrayList<>();
        ImageAdapter adapter = new ImageAdapter(this, array);
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_popup, menu);
        if (sharedPref.getBoolean("sort_by_pop", true))
            menu.findItem(R.id.pop).setChecked(true);
        else
            menu.findItem(R.id.rat).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        SharedPreferences.Editor editor = sharedPref.edit();

        if (id == R.id.pop)
            editor.putBoolean("sort_by_pop", true);
        else if (id == R.id.rat)
            editor.putBoolean("sort_by_pop", false);

        boolean temp = sharedPref.getBoolean("sort_by_pop", true);
        editor.apply();
        if (!(temp == sharedPref.getBoolean("sort_by_pop", false))) {
            refreshPosters();
        }

        invalidateOptionsMenu();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshPosters();
    }

    public void refreshPosters() {
        api = new APITools(this);
        if (api.isNetworkAvailable()) {
            sortByPopularity = sharedPref.getBoolean("sort_by_pop", true);
            new ImageLoadTask().execute();
        } else {
            Snackbar.make(findViewById(R.id.coordinatorLayout), "No internet access.", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while (true) {
                try {
                    return api.getPosterPaths(sortByPopularity);
                } catch (Exception e) {
                    continue;
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null) {
                ImageAdapter adapter = new ImageAdapter(getApplicationContext(), result);
                gridView.setAdapter(adapter);
            }
        }
    }

}