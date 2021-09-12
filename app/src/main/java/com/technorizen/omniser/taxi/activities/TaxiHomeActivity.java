package com.technorizen.omniser.taxi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.adapters.AdapterRecentsLocations;
import com.technorizen.omniser.databinding.ActivityTaxiHomeBinding;
import com.technorizen.omniser.fooddelivery.activities.FoodHomeActivity;
import com.technorizen.omniser.fooddelivery.activities.PinLocationActivity;
import com.technorizen.omniser.models.ModelLocations;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.DrawPollyLine;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.google.android.gms.maps.model.JointType.ROUND;

public class TaxiHomeActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 3;
    Context mContext = TaxiHomeActivity.this;
    ActivityTaxiHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    GoogleMap mMap;
    ArrayList<LatLng> polyLineLatlng;
    private LocationRequest mLocationRequest;
    TextView tvHomeAddress,tvWorkAddress;
    ImageView ivAddHomeAddress,ivAddWorkAddress,ivHomeEdit,ivWorkEdit;
    private long UPDATE_INTERVAL = 3000; /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    ModelLocations recentLocation = null;
    LatLng pickUpLatLng, dropOffLatLng;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    PolylineOptions polylineOptions;
    String isPickUpOrDropOff = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_taxi_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(TaxiHomeActivity.this);
        recentLocation = sharedPref.getLocationModel(AppConstant.LOCATION_DETAILS);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        String previousAdd = sharedPref.getDevLocation(AppConstant.DevADD);

        fetchLocation();

        init();

    }

    private void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.tvPickUp.setOnClickListener(v -> {
            isPickUpOrDropOff = "pick";
            if(recentLocation != null) {
                recentLocationsDialog(recentLocation,"pick");
            } else {
                getRecentLocation("pick");
            }
        });

        binding.tvDropOff.setOnClickListener(v -> {
            isPickUpOrDropOff = "drop";
            if(recentLocation != null) {
                recentLocationsDialog(recentLocation,"drop");
            } else {
                getRecentLocation("drop");
            }
        });

        binding.btNext.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.tvPickUp.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_pickup_address), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.tvDropOff.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_dropoff_address), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(mContext,RideOptActivity.class)
                        .putExtra("polylines",polyLineLatlng)
                        .putExtra("picklatlon",pickUpLatLng)
                        .putExtra("droplatlon",dropOffLatLng)
                );
            }
        });

    }

    private void recentLocationsDialog(ModelLocations modelLocations,String pickOrDrop) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.recent_locations_dialog);

        ProjectUtil.changeDialogStatusBar(dialog,mContext);

        ImageView ivBack = dialog.findViewById(R.id.ivBack);
        CardView cvLocation = dialog.findViewById(R.id.cvLocation);
        ListView locationList = dialog.findViewById(R.id.locationList);
        EditText etSerach = dialog.findViewById(R.id.etSerach);

        tvHomeAddress = dialog.findViewById(R.id.tvHomeAddress);
        TextView tvSetCurrentLoc = dialog.findViewById(R.id.tvSetCurrentLoc);
        TextView tvSetLocMap = dialog.findViewById(R.id.tvSetLocMap);
        ivHomeEdit = dialog.findViewById(R.id.ivHomeEdit);
        ivWorkEdit = dialog.findViewById(R.id.ivWorkEdit);
        ivAddHomeAddress = dialog.findViewById(R.id.ivAddHomeAddress);
        ivAddWorkAddress = dialog.findViewById(R.id.ivAddWorkAddress);
        tvWorkAddress = dialog.findViewById(R.id.tvWorkAddress);

        if(modelLocations.getHome_location() != null) {
            tvHomeAddress.setText(modelLocations.getHome_location().getAddress());
            ivAddHomeAddress.setVisibility(View.GONE);
            ivHomeEdit.setVisibility(View.VISIBLE);
        } else {
            ivAddHomeAddress.setVisibility(View.VISIBLE);
            ivHomeEdit.setVisibility(View.GONE);
        }

        if(modelLocations.getWork_location() != null) {
            tvWorkAddress.setText(modelLocations.getWork_location().getAddress());
            ivAddWorkAddress.setVisibility(View.GONE);
            ivWorkEdit.setVisibility(View.VISIBLE);
        } else {
            ivAddWorkAddress.setVisibility(View.VISIBLE);
            ivWorkEdit.setVisibility(View.GONE);
        }

        tvHomeAddress.setOnClickListener(v -> {

            Log.e("asfsafdsf","tvHomeAddress = " + tvHomeAddress.getText().toString().trim());

            if(pickOrDrop.equals("pick")) {
                if(!tvHomeAddress.getText().toString().trim().isEmpty()) {
                    binding.tvPickUp.setText(tvHomeAddress.getText().toString().trim());
                    Double lat = Double.valueOf(recentLocation.getHome_location().getLat());
                    Double lon = Double.valueOf(recentLocation.getHome_location().getLon());
                    pickUpLatLng = new LatLng(lat,lon);
                    if(pickUpLatLng != null && dropOffLatLng!=null){
                        drawRoute(pickUpLatLng,dropOffLatLng);
                    }
                    dialog.dismiss();
                }
            } else {
                if(!tvHomeAddress.getText().toString().trim().isEmpty()) {
                    binding.tvDropOff.setText(tvHomeAddress.getText().toString().trim());
                    Double lat = Double.valueOf(recentLocation.getHome_location().getLat());
                    Double lon = Double.valueOf(recentLocation.getHome_location().getLon());
                    dropOffLatLng = new LatLng(lat,lon);
                    if(pickUpLatLng != null && dropOffLatLng!=null) {
                        drawRoute(pickUpLatLng,dropOffLatLng);
                    }
                    dialog.dismiss();
                }
            }

        });

        tvWorkAddress.setOnClickListener(v -> {

            Log.e("asfsafdsf","tvWorkAddress = " + tvWorkAddress.getText().toString().trim());

            if(pickOrDrop.equals("pick")) {
                if(!tvWorkAddress.getText().toString().trim().isEmpty()) {
                    binding.tvPickUp.setText(tvWorkAddress.getText().toString().trim());
                    Double lat = Double.valueOf(recentLocation.getHome_location().getLat());
                    Double lon = Double.valueOf(recentLocation.getHome_location().getLon());
                    pickUpLatLng = new LatLng(lat,lon);
                    if(pickUpLatLng != null && dropOffLatLng!=null){
                        drawRoute(pickUpLatLng,dropOffLatLng);
                    }
                    dialog.dismiss();
                }
            } else {
                if(!tvWorkAddress.getText().toString().trim().isEmpty()) {
                    binding.tvDropOff.setText(tvWorkAddress.getText().toString().trim());
                    Double lat = Double.valueOf(recentLocation.getHome_location().getLat());
                    Double lon = Double.valueOf(recentLocation.getHome_location().getLon());
                    dropOffLatLng = new LatLng(lat,lon);
                    if(pickUpLatLng != null && dropOffLatLng!=null){
                        drawRoute(pickUpLatLng,dropOffLatLng);
                    }
                    dialog.dismiss();
                }
            }

        });

        AdapterRecentsLocations adapterRecentsLocations = new AdapterRecentsLocations(mContext,modelLocations.getRecent_location());
        locationList.setAdapter(adapterRecentsLocations);

        ivAddHomeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            intent.putExtra("type","home");
            startActivityForResult(intent, 5);
        });

        ivAddWorkAddress.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            intent.putExtra("type","work");
            startActivityForResult(intent, 4);
        });

        ivHomeEdit.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            intent.putExtra("type","edithome");
            intent.putExtra("id",modelLocations.getHome_location().getId());
            startActivityForResult(intent, 6);
        });

        ivWorkEdit.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            intent.putExtra("type","editwork");
            intent.putExtra("id",modelLocations.getWork_location().getId());
            startActivityForResult(intent, 7);
        });

        tvSetCurrentLoc.setOnClickListener(v -> {
            dialog.dismiss();
            setCurrentLocation(pickOrDrop);
        });

        tvSetLocMap.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            intent.putExtra("type","map");
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            dialog.dismiss();
        });

        locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelLocations.Recent_location data = modelLocations.getRecent_location().get(position);
                dialog.dismiss();

                if(pickOrDrop.equals("pick")) {
                    binding.tvPickUp.setText(data.getAddress());
                    pickUpLatLng = new LatLng(Double.parseDouble(data.getLat()),Double.parseDouble(data.getLon()));
                } else {
                    binding.tvDropOff.setText(data.getAddress());
                    dropOffLatLng = new LatLng(Double.parseDouble(data.getLat()),Double.parseDouble(data.getLon()));
                }

                if(pickUpLatLng != null && dropOffLatLng != null) {
                    drawRoute(pickUpLatLng,dropOffLatLng);
                } else {
                    Toast.makeText(mContext, getString(R.string.please_select_both_loc), Toast.LENGTH_SHORT).show();
                }

            }
        });

        etSerach.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            if(pickOrDrop.equals("pick"))
                intent.putExtra("type","pick");
            else
                intent.putExtra("type","drop");
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            dialog.dismiss();
        });

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        cvLocation.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            if(pickOrDrop.equals("pick"))
                intent.putExtra("type","pick");
            else
                intent.putExtra("type","drop");
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    private void setCurrentLocation(String pickOrDrop) {
        if(pickOrDrop.equals("pick")) {
            if(currentLocation != null) {
                pickUpLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                String strAdd = ProjectUtil.getCompleteAddressString(mContext,pickUpLatLng.latitude,pickUpLatLng.longitude);
                binding.tvPickUp.setText(strAdd);
                if(pickUpLatLng != null && dropOffLatLng!=null){
                    drawRoute(pickUpLatLng,dropOffLatLng);
                }
            }
        } else {
             if(currentLocation != null) {
                dropOffLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                String strAdd = ProjectUtil.getCompleteAddressString(mContext,dropOffLatLng.latitude,dropOffLatLng.longitude);
                binding.tvDropOff.setText(strAdd);

                if(pickUpLatLng != null) {
                    drawRoute(pickUpLatLng,dropOffLatLng);
                } else {
                    Toast.makeText(mContext, getString(R.string.please_select_pick_loc), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void getRecentLocation(String pickOrDrop) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface()
                .getRecentLocations(modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("skjgfjs","response = " + response);

                    if(jsonObject.getString("status").equals("1")) {
                        recentLocation = new Gson().fromJson(responseString, ModelLocations.class);
                        sharedPref.setLocationModel(AppConstant.LOCATION_DETAILS,recentLocation);
                        recentLocationsDialog(recentLocation,pickOrDrop);
                    } else {
                        recentLocation = new Gson().fromJson(responseString, ModelLocations.class);
                        recentLocationsDialog(recentLocation,pickOrDrop);
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission (
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TaxiHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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

                    String address = ProjectUtil
                            .getCompleteAddressString(mContext, currentLocation.getLatitude()
                                    , currentLocation.getLongitude());

                    if (TextUtils.isEmpty(binding.tvPickUp.getText().toString().trim())) {
                        binding.tvPickUp.setText(address);
                        pickUpLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        setCurrentLocationMap(currentLocation);
                    }

                } else {
                    startLocationUpdates();
                    Log.e("ivCurrentLocation", "location = " + location);
                }
            }
        });

    }

    private void setCurrentLocationMap(Location currentLocation) {
        if (mMap != null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker))
                    .title(ProjectUtil.getCompleteAddressString(mContext, currentLocation.getLatitude(), currentLocation.getLongitude()));
            mMap.addMarker(markerOptions);
            LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        Log.e("hdasfkjhksdf", "Location = ");

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "Location = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                            String address = ProjectUtil.getCompleteAddressString(mContext, currentLocation.getLatitude(), currentLocation.getLongitude());

                            if (TextUtils.isEmpty(binding.tvPickUp.getText().toString().trim())) {
                                binding.tvPickUp.setText(address);
                                setCurrentLocationMap(currentLocation);
                            }
                        } else {
                            fetchLocation();
                        }

                    }
                },
                Looper.myLooper());

    }

    private void taxiPaymentSelectorDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.taxi_payment_dialog);

        RelativeLayout rlDone = dialog.findViewById(R.id.rlDone);
        CheckBox cbWallet = dialog.findViewById(R.id.cbWallet);
        CheckBox cbCash = dialog.findViewById(R.id.cbCash);
        CheckBox cbOnline = dialog.findViewById(R.id.cbOnline);

        cbWallet.setOnClickListener(v -> {
            cbCash.setChecked(false);
            cbOnline.setChecked(false);
        });

        cbCash.setOnClickListener(v -> {
            cbWallet.setChecked(false);
            cbOnline.setChecked(false);
        });

        cbOnline.setOnClickListener(v -> {
            cbCash.setChecked(false);
            cbWallet.setChecked(false);
        });

        rlDone.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void openApplyCouponDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.apply_coupon_dialog);

        ImageView ivBack = dialog.findViewById(R.id.ivBack);
        TextView tvApply = dialog.findViewById(R.id.tvApply);

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        tvApply.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == 222) {
//            String add = data.getStringExtra("add");
//            double lat = data.getDoubleExtra("lat", 0.0);
//            double lon = data.getDoubleExtra("lon", 0.0);
//            pickUpLatLng = new LatLng(lat, lon);
//            binding.tvPickUp.setText(add);
//        } else if (resultCode == 333) {
//            String add = data.getStringExtra("add");
//            double lat = data.getDoubleExtra("lat", 0.0);
//            double lon = data.getDoubleExtra("lon", 0.0);
//            dropOffLatLng = new LatLng(lat, lon);
//            binding.tvDropOff.setText(add);
//
//            if (pickUpLatLng != null) {
//                drawRoute(pickUpLatLng, dropOffLatLng);
//            } else {
//                Toast.makeText(mContext, getString(R.string.please_select_pick_loc), Toast.LENGTH_SHORT).show();
//            }
//
//        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            String add = data.getStringExtra("add");
            double lat = data.getDoubleExtra("lat",0.0);
            double lon = data.getDoubleExtra("lon",0.0);

            currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);

            if(isPickUpOrDropOff.equals("pick")) {
                binding.tvPickUp.setText(add);
                pickUpLatLng = new LatLng(lat,lon);
            } else {
                binding.tvDropOff.setText(add);
                dropOffLatLng = new LatLng(lat,lon);

                if(pickUpLatLng != null && dropOffLatLng != null){
                    drawRoute(pickUpLatLng,dropOffLatLng);
                } else {
                    Toast.makeText(mContext, getString(R.string.please_select_both_loc), Toast.LENGTH_SHORT).show();
                }

            }

            sharedPref.setDevLocation(AppConstant.DevADD,add);
            sharedPref.setDevLocation(AppConstant.Devlat,String.valueOf(currentLocation.getLatitude()));
            sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));
        } else if(requestCode == 5) {

            recentLocation = (ModelLocations) data.getSerializableExtra("data");
            sharedPref.setLocationModel(AppConstant.LOCATION_DETAILS,recentLocation);

            String add = data.getStringExtra("add");
            double lat = data.getDoubleExtra("lat",0.0);
            double lon = data.getDoubleExtra("lon",0.0);

            currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);

            tvHomeAddress.setText(add);
            ivAddHomeAddress.setVisibility(View.GONE);
            ivHomeEdit.setVisibility(View.VISIBLE);

            sharedPref.setDevLocation(AppConstant.DevADD,add);
            sharedPref.setDevLocation(AppConstant.Devlat,String.valueOf(currentLocation.getLatitude()));
            sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));
        } else if(requestCode == 4) {

            recentLocation = (ModelLocations) data.getSerializableExtra("data");
            sharedPref.setLocationModel(AppConstant.LOCATION_DETAILS,recentLocation);

            String add = data.getStringExtra("add");
            double lat = data.getDoubleExtra("lat",0.0);
            double lon = data.getDoubleExtra("lon",0.0);

            currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);

            tvWorkAddress.setText(add);
            ivAddWorkAddress.setVisibility(View.GONE);
            ivWorkEdit.setVisibility(View.VISIBLE);

            sharedPref.setDevLocation(AppConstant.DevADD,add);
            sharedPref.setDevLocation(AppConstant.Devlat,String.valueOf(currentLocation.getLatitude()));
            sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));
        } else if (resultCode == 6) {

            recentLocation = (ModelLocations) data.getSerializableExtra("data");
            sharedPref.setLocationModel(AppConstant.LOCATION_DETAILS,recentLocation);

            String add = data.getStringExtra("add");
            double lat = data.getDoubleExtra("lat",0.0);
            double lon = data.getDoubleExtra("lon",0.0);

            currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);

            tvHomeAddress.setText(add);
            ivAddHomeAddress.setVisibility(View.GONE);
            ivHomeEdit.setVisibility(View.VISIBLE);

            sharedPref.setDevLocation(AppConstant.DevADD,add);
            sharedPref.setDevLocation(AppConstant.Devlat,String.valueOf(currentLocation.getLatitude()));
            sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));
        } else if (resultCode == 7) {

            recentLocation = (ModelLocations) data.getSerializableExtra("data");
            sharedPref.setLocationModel(AppConstant.LOCATION_DETAILS,recentLocation);

            String add = data.getStringExtra("add");
            double lat = data.getDoubleExtra("lat",0.0);
            double lon = data.getDoubleExtra("lon",0.0);

            currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);

            tvWorkAddress.setText(add);
            ivAddWorkAddress.setVisibility(View.GONE);
            ivWorkEdit.setVisibility(View.VISIBLE);

            sharedPref.setDevLocation(AppConstant.DevADD,add);
            sharedPref.setDevLocation(AppConstant.Devlat,String.valueOf(currentLocation.getLatitude()));
            sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));

        }

    }

    private void drawRoute(LatLng pickUpLatLng, LatLng dropOffLatLng) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        DrawPollyLine.get(this).setOrigin(pickUpLatLng)
                .setDestination(dropOffLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                ProjectUtil.pauseProgressDialog();
                mMap.clear();
                polyLineLatlng = latLngs;
                addPickDropMarkerOnMap(pickUpLatLng, dropOffLatLng);
                addPolyLinesOnMap(latLngs);
            }
        });
    }

    private void addPickDropMarkerOnMap(LatLng pickUpLatLng, LatLng dropOffLatLng) {
        MarkerOptions markerOptionsPick = new MarkerOptions()
                .position(new LatLng(pickUpLatLng.latitude, pickUpLatLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker))
                .title(ProjectUtil.getCompleteAddressString(mContext, pickUpLatLng.latitude, pickUpLatLng.longitude));

        MarkerOptions markerOptionsDrop = new MarkerOptions()
                .position(new LatLng(dropOffLatLng.latitude, dropOffLatLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker))
                .title(ProjectUtil.getCompleteAddressString(mContext, dropOffLatLng.latitude, dropOffLatLng.longitude));

        mMap.addMarker(markerOptionsPick);
        mMap.addMarker(markerOptionsDrop);
        ArrayList<LatLng> latlngList = new ArrayList<>();
        latlngList.add(pickUpLatLng);
        latlngList.add(dropOffLatLng);

        zoomRoute(mMap, latlngList);
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

    private void addPolyLinesOnMap(ArrayList<LatLng> latLngs) {
        polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngs);
        polylineOptions.color(R.color.black);
        polylineOptions.width(10);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.geodesic(true);
        polylineOptions.zIndex(5f);
        mMap.addPolyline(polylineOptions);
    }

    private void multipleVehicleSectorDialog() {

        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_taxi_list_dialog);

        LinearLayout llSelectPayment = dialog.findViewById(R.id.llSelectPayment);
        LinearLayout llApplyCoupon = dialog.findViewById(R.id.llApplyCoupon);

        llSelectPayment.setOnClickListener(v -> {
            taxiPaymentSelectorDialog();
        });

        llApplyCoupon.setOnClickListener(v -> {
            openApplyCouponDialog();
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

}