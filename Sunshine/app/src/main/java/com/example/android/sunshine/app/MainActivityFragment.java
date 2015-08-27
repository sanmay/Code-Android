package com.example.android.sunshine.app;

import java.util.*;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView= inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> weekForecast=new ArrayList<String>();
        weekForecast.add("Today-Sunny-8863");
        weekForecast.add("Tommorw-Sunny-8863");
        weekForecast.add("Weds-Sunny-8863");
        weekForecast.add("Thus-Sunny-8863");
        weekForecast.add("Fri-Sunny-8863");

        ArrayAdapter<String> mForcast=new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecaste_textview,weekForecast);
        ListView lv=(ListView)rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(mForcast);

        return rootView;
    }
}
