package com.technorizen.omniser.taxi.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.MyBookingActivity;
import com.technorizen.omniser.databinding.ActivityTrackTaxiBinding;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.taxi.models.ModelTaxiBookingDetail;
import com.technorizen.omniser.utils.Api;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.DrawPollyLine;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackTaxiAct extends AppCompatActivity
        implements OnMapReadyCallback {

    Context mContext = TrackTaxiAct.this;
    ActivityTrackTaxiBinding binding;
    String request_id = "";
    private LatLng pickUpLatLng, dropOffLatLng;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private Marker driverCarMarker;
    private long FASTEST_INTERVAL = 2000;
    private long UPDATE_INTERVAL = 3000;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    GoogleMap map;
    Timer timer = new Timer();
    String driverStatus = "",driverId=null,driverName="",driverImage=""
            ,driverMobile=null,driverLat,driverLon;
    private PolylineOptions lineOptions;
    private AlertDialog.Builder builder1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_taxi);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        if (getIntent() != null)
            request_id = getIntent().getStringExtra("request_id");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context,Intent intent) {
            if(intent.getStringExtra("object") != null) {
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("object"));
                    binding.ivCancel.setVisibility(View.GONE);
                    if(String.valueOf(jsonObject.get("status")).equals("Cancel_by_driver")) {
                        finish();
                        startActivity(new Intent(mContext,TaxiHomeActivity.class));
                    } else {
                        DriverArriveDialog(String.valueOf(jsonObject.get("status")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//            try {
//                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("object"));
//                    DriverArriveDialog(String.valueOf(jsonObject.get("status")));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter("Job_Status_Action"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void DriverArriveDialog(String status) {
        final Dialog dialog = new Dialog(TrackTaxiAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_driver_arrived);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        binding.titler.setText("Arriving");
        dialog.setCanceledOnTouchOutside(true);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);

        if(status.equals("Arrived")) {
            tvTitle.setText(getString(R.string.driver_arrived));
            binding.titler.setText("Driver Arrived");
        } else if(status.equals("Start")) {
            binding.titler.setText("Trip Start");
            tvTitle.setText(getString(R.string.your_trip_has_begin));
        } else if(status.equals("Finish")) {
            binding.titler.setText("Trip Finish");
            tvTitle.setText(getString(R.string.your_trip_is_finished));
        }

        TextView btnOk = dialog.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            if(status.equals("Finish")) {
                finish();
                startActivity(new Intent(mContext,MyBookingActivity.class));
            }
            dialog.dismiss();
        });

        dialog.show();

    }

    private void init() {

        binding.ivCancel.setOnClickListener(v -> {
            cancelByDriverDialog();
        });

        binding.ivChat.setOnClickListener(v -> {
            startActivity(new Intent(mContext,TaxiChatingActivity.class)
                    .putExtra("sender_id",modelLogin.getResult().getId())
                    .putExtra("receiver_id",driverId)
                    .putExtra("name",driverName)
                    .putExtra("request_id",request_id)
            );
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void cancelByDriverDialog() {
        builder1 = new AlertDialog.Builder(TrackTaxiAct.this);
        builder1.setMessage(getResources()
                .getString(R.string.are_your_sure_you_want_to_cancel_the_trip));
        builder1.setCancelable(false);

        builder1.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                cancelByUserApi();
            }
        });

        builder1.setNegativeButton("No",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private void cancelByUserApi() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.loadInterface();

        HashMap<String,String> param = new HashMap<>();
        param.put("request_id", request_id);

        Call<ResponseBody> call = api.cancelTripByUser(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        finishAffinity();
                        startActivity(new Intent(mContext, TaxiHomeActivity.class));
                    } else {
                        // Toast.makeText(mContext, getString(R.string.no_chat_found), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getBookingDetail(request_id);
    }

    private void getBookingDetail(String request_id) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        HashMap<String,String> param = new HashMap<>();
        param.put("request_id",request_id);

        Call<ResponseBody> call = ApiFactory.loadInterface().bookingDetails(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();

                Log.e("kghkljsdhkljf","request_id = " + request_id);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {

                        ModelTaxiBookingDetail data = new Gson().fromJson(stringResponse,ModelTaxiBookingDetail.class);

                        driverId = data.getResult().getDriverDetails().getId();
                        driverName = data.getResult().getDriverDetails().getName();
                        driverImage = data.getResult().getDriverDetails().getDriverImage();
                        driverMobile = data.getResult().getDriverDetails().getMobile();
                        driverLat = data.getResult().getDriverDetails().getLat();
                        driverLon = data.getResult().getDriverDetails().getLon();
                        binding.tvName.setText(driverName);

                        if("Arrived".equals(data.getResult().getStatus())) {
                            binding.titler.setText(getString(R.string.driver_arrived));
                            binding.ivCancel.setVisibility(View.GONE);
                        } else if("Start".equals(data.getResult().getStatus())) {
                            binding.titler.setText(getString(R.string.your_trip_has_begin));
                            binding.ivCancel.setVisibility(View.GONE);
                        } else if("Finish".equals(data.getResult().getStatus())) {
                            binding.titler.setText(getString(R.string.your_trip_is_finished));
                            binding.ivCancel.setVisibility(View.GONE);
                            // openPaymentSummaryDialog(data);
                        } else if("Cancel_by_user".equals(data.getResult().getStatus())){
                            binding.ivCancel.setVisibility(View.GONE);
                            binding.titler.setText(getString(R.string.your_trip_is_cancelled));
                        } else if("Cancel_by_driver".equals(data.getResult().getStatus())) {
                            binding.ivCancel.setVisibility(View.GONE);
                            binding.titler.setText(getString(R.string.your_trip_is_cancelled));
                        } else if("Paid".equals(data.getResult().getStatus())){
                            binding.ivCancel.setVisibility(View.GONE);
                            binding.titler.setText(getString(R.string.paid));
                        } else {
                            binding.ivCancel.setVisibility(View.VISIBLE);
                            binding.titler.setText(getString(R.string.driver_arriving));
                        }

                        // binding.tvCaraNumber.setText("Car No : " + data.getResult().getDriverDetails().getCarNumber());
                        // binding.tvCarModel.setText(data.getResult().getDriverDetails().getCarModel());

                        // updateDriverLatLon(driverId);

                        try {
                            Picasso.get().load(driverImage)
                                    .placeholder(R.drawable.default_profile_icon)
                                    .error(R.drawable.default_profile_icon).into(binding.ivDriverPropic);
                        } catch (Exception e) {}

                        pickUpLatLng = new LatLng(Double.parseDouble(data.getResult().getPicuplat()), Double.parseDouble(data.getResult().getPickuplon()));
                        dropOffLatLng = new LatLng(Double.parseDouble(data.getResult().getDroplat()), Double.parseDouble(data.getResult().getDroplon()));

                        Log.e("fgdfgdfgdfg","pickUpLatLng = " + pickUpLatLng);
                        Log.e("fgdfgdfgdfg","dropOffLatLng = " + dropOffLatLng);

                        if (checkPermissions()) {
                            if (isLocationEnabled()) {
                                Log.e("dropOffLatLng","pickUpLatLng inside = " + pickUpLatLng);
                                Log.e("dropOffLatLng","dropOffLatLng inside = " + dropOffLatLng);
                                setPickUpDropOffRoute();
                            } else {
                                Toast.makeText(TrackTaxiAct.this, "Turn on location", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        } else {
                            requestPermissions();
                        }

                    } else {
                        // Toast.makeText(mContext, getString(R.string.no_chat_found), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void setPickUpDropOffRoute() {
        DrawPolyLine();
    }

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(pickUpLatLng)
                .setDestination(dropOffLatLng)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        // map.clear();
                        lineOptions = new PolylineOptions();
                        lineOptions.addAll(latLngs);
                        lineOptions.width(10);
                        lineOptions.color(R.color.black);
                        ArrayList<LatLng> latlonList = new ArrayList<>();
                        latlonList.add(pickUpLatLng);
                        latlonList.add(dropOffLatLng);
                        if(driverCarMarker == null) {
                            double lat = Double.parseDouble(driverLat);
                            double lon = Double.parseDouble(driverLon);
                            String driverAddress = ProjectUtil.getCompleteAddressString(mContext, lat, lon);
                            MarkerOptions pickMarker = new MarkerOptions().title("Driver Location")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top))
                                    .position(new LatLng(lat,lon));
                            driverCarMarker = map.addMarker(pickMarker);
                        }
                        // latlonList.add(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                        zoomRoute(map,latlonList);
                        setPickDropMarker();
                    }
                });
    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {
        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 150;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    private void setPickDropMarker() {
        if (map != null) {
            // map.clear();
            if (lineOptions != null)
                map.addPolyline(lineOptions);
            if (pickUpLatLng != null) {
                String pickAdd = ProjectUtil.getCompleteAddressString(mContext,pickUpLatLng.latitude,pickUpLatLng.longitude);
                MarkerOptions pickMarker = new MarkerOptions().title("PickUp Address : " + pickAdd)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker))
                        .position(pickUpLatLng);
                map.addMarker(pickMarker);
                // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (dropOffLatLng != null) {
                String dropAdd = ProjectUtil.getCompleteAddressString(mContext,dropOffLatLng.latitude,dropOffLatLng.longitude);
                MarkerOptions dropMarker = new MarkerOptions().title("Drop Address : " + dropAdd)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker))
                        .position(dropOffLatLng);
                map.addMarker(dropMarker);
            }
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }



}