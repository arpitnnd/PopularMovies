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

import java.util.ArrayList;
import java.util.Arrays;

public class MoviesFragment extends Fragment {

    static GridView gridView;
    static ArrayList<String> posters;
    static boolean sortByPop;

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
        getActivity().setTitle("Most Popular Movies");

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
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
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

        public String[] getPathsFromAPI(boolean sort) {
            String[] array = new String[15];
            for (int i = 0; i < array.length; i++) {
                array[i] = "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
            }
            return array;

        }
    }

}