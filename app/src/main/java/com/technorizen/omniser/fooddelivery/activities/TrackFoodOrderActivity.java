package com.technorizen.omniser.fooddelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityTrackFoodOrderBinding;
import com.technorizen.omniser.fooddelivery.models.ModelMyOrders;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.Api;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.DataParser;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackFoodOrderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context mContext = TrackFoodOrderActivity.this;
    ActivityTrackFoodOrderBinding binding;
    ModelMyOrders.Result data;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    MarkerOptions origin, destination;
    Marker originMarker,destinationMarker;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    SupportMapFragment mapFragment;
    private static final int REQUEST_CODE = 101;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_food_order);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(TrackFoodOrderActivity.this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        
        data = (ModelMyOrders.Result) getIntent().getSerializableExtra("data");

        init();

        fetchLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getLocations();
            }
        },0,5000);
    }

    private void getLocations() {

        // ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().getDevliveryLocations(data.getDriver_id());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    String stringResponse = response.body().string();

                    Log.e("getMyOrder","response = " + stringResponse);
                    Log.e("getMyOrder","response = " + response);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONObject resultObj = jsonObject.getJSONObject("result");
                        String divLat = resultObj.getString("div_lat");
                        String divlon = resultObj.getString("div_lon");

                        String add = ProjectUtil.getCompleteAddressString(mContext,
                                 Double.parseDouble(divLat)
                                ,Double.parseDouble(divlon));

                        destination = new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(divLat),
                                        Double.parseDouble(divlon)))
                                .title("Delivery Boy current location " + add);

                        if(mMap != null) {
                            animateMarker(destinationMarker,destination.getPosition(),false);
                        }
                    } else {
                        // Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    ProjectUtil.pauseProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });


    }

    private void init() {

        binding.rlPhone.setOnClickListener(v -> {
            if(data.getDriver_details().getDriver_mobile() == null ||
                    data.getDriver_details().getDriver_mobile().equals("")) {
                Toast.makeText(mContext, "No contact available", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + data.getDriver_details().getDriver_mobile()));
                mContext.startActivity(intent);
            }
        });

        Glide.with(mContext).load(data.getDriver_details().getDriver_image())
                .placeholder(R.drawable.loading)
                .error(R.drawable.default_img)
                .apply(new RequestOptions().override(300,300))
                .into(binding.ivProfile);

        binding.tvName.setText(data.getDriver_details().getDriver_name());
        binding.tvOrderId.setText(data.getOrder_id());

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void setDirectionsOnMap() {

        if(origin == null) {
            Log.e("skdjfghdfd","origin is null");
            return;
        } else if(destination == null) {
            Log.e("skdjfghdfd","destination is null");
            return;
        } else {
            Log.e("skdjfghdfd","Both not Null");
        }

        // mMap.clear();
        originMarker = mMap.addMarker(origin);
        destinationMarker = mMap.addMarker(destination);
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin.getPosition(), 18));

//        LatLngBounds group = new LatLngBounds.Builder()
//                .include(origin.getPosition())   // LatLgn object1
//                .include(destination.getPosition())  // LatLgn object2
//                .build();
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(group, 0));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(origin.getPosition()); // LatLgn object1
        builder.include(destination.getPosition());  // LatLgn object2

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }

    private void fetchLocation() {

        if (ActivityCompat.checkSelfPermission (
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TrackFoodOrderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // isCurrentLocation = true;
                    currentLocation = location;
                    Log.e("ivCurrentLocation", "location = " + location);
                    // Toast.makeText(getActivity(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                        LatLng originlatLon = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        LatLng destilatLon = new LatLng(Double.parseDouble(data.getDriver_details().getDriver_lat())
                                ,Double.parseDouble(data.getDriver_details().getDriver_lon()));

                        try {
                            String address = ProjectUtil.getCompleteAddressString(mContext,currentLocation.getLatitude(),currentLocation.getLongitude());
                            origin = new MarkerOptions().position(originlatLon).title(address);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            String address = ProjectUtil.getCompleteAddressString(mContext,destilatLon.latitude,destilatLon.longitude);
                            destination = new MarkerOptions().position(destilatLon).title("Delivery Boy Current Location " + address);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin.getPosition(),destination.getPosition());

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);

                        setDirectionsOnMap();

                        mapFragment.getMapAsync(TrackFoodOrderActivity.this);

                } else {
//                    currentLocation = new Location("");
//                    currentLocation.setLatitude(Double.parseDouble(modelLogin.getResult().getLat()));
//                    currentLocation.setLongitude(Double.parseDouble(modelLogin.getResult().getLon()));
                    Log.e("ivCurrentLocation", "location = " + location);
                }
            }
        });

    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
                // Log.e("aslfkghfjasasfd","data = " + data);
                JSONObject jsonObject = new JSONObject(data);

                JSONArray array = jsonObject.getJSONArray("routes");

                JSONObject routes = array.getJSONObject(0);

                JSONArray legs = routes.getJSONArray("legs");

                JSONObject steps = legs.getJSONObject(0);

                JSONObject duration = steps.getJSONObject("duration");

                binding.tvArrivedTime.setText("Delivery Boy will be arrived in : " + duration.getString("text"));

                /* Log.e("Distance", distance.toString());
                   Log.e("Distance",distance.getString("text")); */

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

            return data;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("aslfkghfjasasfd","result = " + result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map
            if (points.size() != 0)
                mMap.addPolyline(lineOptions);

        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //setting transportation mode
        String mode = "mode=driving";

        String sensor = "mode=driving";

        // Building the parameters to the web service
        // String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.places_api_key);

        return url;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


}