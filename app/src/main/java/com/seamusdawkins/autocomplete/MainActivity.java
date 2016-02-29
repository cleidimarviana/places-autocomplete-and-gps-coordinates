/*
*    The MIT License (MIT)
*
*   Copyright (c) 2016 Cleidimar Viana
*
*   Permission is hereby granted, free of charge, to any person obtaining a copy
*   of this software and associated documentation files (the "Software"), to deal
*   in the Software without restriction, including without limitation the rights
*   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*   copies of the Software, and to permit persons to whom the Software is
*   furnished to do so, subject to the following conditions:
*   The above copyright notice and this permission notice shall be included in all
*   copies or substantial portions of the Software.
*   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*   SOFTWARE.
*/
package com.seamusdawkins.autocomplete;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    public static final int REQUEST_PERMISSION = 221;

    private static String TAG = MainActivity.class.getSimpleName();

    /* These variables are meant to floatint action button */
    private FloatingActionButton floatButton;

    /* These variables are meant to autocomplete */
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    public static ArrayList resultListReference;


    private Activity activity;
    private GoogleMap map;

    private boolean error;
    private String urlPost;
    private Double latitude, longitude;
    private String description;

    private AutoCompleteTextView autoCompView;


    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        initUI();
        initListener();

    }

    /**
     * This method maping xml interface with java.
     */
    public void initUI() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        floatButton = (FloatingActionButton) findViewById(R.id.float_button);
    }

    /**
     * This method implementing a listener
     */
    public void initListener() {

        mapFragment.getMapAsync(this);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item_autocomplete));
        autoCompView.setOnItemClickListener(this);

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/cleidimarviana/places-autocomplete-and-gps-coordinates";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION);
                return;
            }
        } else {
            //Do Your Stuff
        }
        map.setMyLocationEnabled(true);

        this.map = map;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Add your function here which open camera
                } else {

                }
                return;
            }
        }
    }

    /**
     * This method will results show the autocomplete addresses
     *
     * @param input
     * @return list
     */
    public ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        resultListReference = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + activity.getString(R.string.google_server_key));
            //sb.append("&components=country:br");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.wtf(LOG_TAG, "Error processing Places API URL: " + e);
            return resultList;
        } catch (IOException e) {
            Log.wtf(LOG_TAG, "Error connecting to Places API: " + e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            resultListReference = new ArrayList();
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println(predsJsonArray.getJSONObject(i).getString("reference"));
                System.out.println("--------------------------------------------------------");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                resultListReference.add(predsJsonArray.getJSONObject(i).getString("reference"));
            }
        } catch (JSONException e) {
            Log.wtf(LOG_TAG, "Cannot process JSON results: " + e);
        }

        return resultList;
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        hideKeyboard();

        description = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();

        String reference = (String) resultListReference.get(position);
        String urlLocate = "https://maps.googleapis.com/maps/api/place/details/json?key=" + getString(R.string.google_server_key) + "&reference=" + reference;
        AsyncTaskNewsParseJson myTask = new AsyncTaskNewsParseJson();
        myTask.execute(urlLocate);

        autoCompView.setText("");

    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }


    public class AsyncTaskNewsParseJson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... url) {

            urlPost = url[0];
            try {

                JSONObject jsonObjPoints = doGet(activity, urlPost, "UTF-8");

                if (jsonObjPoints.length() != 0) {

                    JSONObject jsonObjectResult = jsonObjPoints.getJSONObject("result");
                    JSONObject jsonObjectGeometry = jsonObjectResult.getJSONObject("geometry");
                    JSONObject jsonObjLocation = jsonObjectGeometry.getJSONObject("location");
                    Log.wtf(TAG, "lat:" + jsonObjLocation.getString("lat") + " lng:" + jsonObjLocation.getString("lng"));

                    latitude = jsonObjLocation.getDouble("lat");
                    longitude = jsonObjLocation.getDouble("lng");

                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (error) {
                error = false;
            }

            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);


            LatLng sydney = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions()
                    .title(description)
                    .snippet("" + latitude + "," + longitude)
                    .position(sydney));

            CameraUpdate center = CameraUpdateFactory.newLatLng(sydney);

            map.moveCamera(center);
            map.animateCamera(CameraUpdateFactory.zoomIn());
            map.animateCamera(zoom, 3000, null);


        }
    }

    public static JSONObject doGet(Activity act, String url, String charset) {

        HttpGet conn = new HttpGet(url);
        conn.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        JSONObject o = null;
        HttpResponse resp;
        int status = 0;
        resp = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            resp = httpClient.execute(conn);

        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        try {
            if (resp != null) {

                status = resp.getStatusLine().getStatusCode();
            }
            if (status == 200) {

                InputStream content = resp.getEntity().getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));

                String text;
                StringBuilder sb = new StringBuilder();

                while ((text = buffer.readLine()) != null) {
                    sb.append(text);
                }

                return new JSONObject(sb.toString());
            } else {
                JSONObject obj = new JSONObject();
                obj.put("status", String.valueOf(status));
                return obj;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method will hide the keyboard.
     */
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}