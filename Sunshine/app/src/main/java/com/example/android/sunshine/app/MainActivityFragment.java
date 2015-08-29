package com.example.android.sunshine.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

/**
 * A placeholder fragment containing a simple view.
 *
 *
 */

public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> mForcast;

    class WeatherTask extends AsyncTask<String,Void,String[]>{

        @Override


        protected String[] doInBackground(String... param) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast




                final String baseURL="http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM="q";
                final String METRIC_PARAM="units";
                final String DAYS_PARAM="cnt";
                final String FORMAT_PARAM="mode";

                String location=param[0];
                String units=param[1];
                String days="7";
                String mode="json";

                Uri builtUri=Uri.parse(baseURL).buildUpon().appendQueryParameter(QUERY_PARAM,location).appendQueryParameter(FORMAT_PARAM,mode).appendQueryParameter(METRIC_PARAM,units).appendQueryParameter(DAYS_PARAM, days).build();
                //Log.v("Sanmay-URIString",builtUri.toString());
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                WeatherDataParser WDP=new  WeatherDataParser();
                final String[] weatherDataFromJson = WDP.getWeatherDataFromJson(forecastJsonStr, 7);
                //Log.v("Sanmay-Weather",forecastJsonStr);
                //for(int i=0;i<weatherDataFromJson.length;i++){
                 //   Log.v("Sanmay-string",weatherDataFromJson[i]);
                //}
                return weatherDataFromJson;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
                if(strings!=null) {
                    mForcast.clear();

                    for (String dayForecast : strings) {
                        mForcast.add(dayForecast);
                    }
                }
        }
    }
    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    void updateWeather(){
        SharedPreferences spf= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location=spf.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        String unit=spf.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_metric));
        Log.v("Sanmay-Unit",unit);
        WeatherTask WT = new WeatherTask();
        WT.execute(location,unit);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView= inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> weekForecast=new ArrayList<String>();

        mForcast=new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecaste_textview,weekForecast);
        ListView lv=(ListView)rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(mForcast);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(),  mForcast.getItem(position), Toast.LENGTH_SHORT).show();
                Intent details=new Intent(getActivity(),DetailActivity.class).putExtra(Intent.EXTRA_TEXT,mForcast.getItem(position));
                startActivity(details);

            }
        });





        return rootView;
    }
}
