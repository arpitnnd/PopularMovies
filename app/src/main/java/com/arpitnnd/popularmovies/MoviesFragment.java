package com.arpitnnd.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MoviesFragment extends Fragment {

    static GridView gridView;
    static ArrayList<String> posters;
    static boolean sortByPopularity;
    static String API_KEY = "c7471dc1c8bc98ab58f7596dbb9a7888";

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_movies, container, false);

        if (getActivity() != null) {
            ArrayList<String> array = new ArrayList<>();
            ImageAdapter adapter = new ImageAdapter(getActivity(), array);
            gridView = (GridView) v.findViewById(R.id.gridView);
            gridView.setAdapter(adapter);
        }
        //listen for presses on gridView items
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
            }
        });

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();

        if (isNetworkAvailable()) {
            gridView.setVisibility(GridView.VISIBLE);
            new ImageLoadTask().execute();
        } else {
            Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "No internet access.", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while (true) {
                try {
                    posters = new ArrayList(Arrays.asList(getAPIPaths(sortByPopularity)));
                    return posters;
                } catch (Exception e) {
                    continue;
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null && getActivity() != null) {
                ImageAdapter adapter = new ImageAdapter(getActivity(), result);
                gridView.setAdapter(adapter);

            }
        }

        public String[] getAPIPaths(boolean sortby) {
            {
                while (true) {
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    String JSONResult;

                    try {
                        String urlString = null;
                        if (sortby) {
                            urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
                        } else {
                            urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + API_KEY;
                        }
                        URL url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        //Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            return null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line + "\n");
                        }
                        if (buffer.length() == 0) {
                            return null;
                        }
                        JSONResult = buffer.toString();

                        try {
                            return getPathsFromJSON(JSONResult);
                        } catch (JSONException e) {
                            return null;
                        }
                    } catch (Exception e) {
                        continue;
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                            }
                        }
                    }


                }
            }
        }

        public String[] getPathsFromJSON(String JSONStringParam) throws JSONException {

            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i] = moviePath;
            }
            return result;
        }

    }

}