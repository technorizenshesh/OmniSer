package com.technorizen.omniser.pharmacydelivery.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.adapters.AdapterRecentsLocations;
import com.technorizen.omniser.databinding.ActivityPharmacyShopListBinding;
import com.technorizen.omniser.fooddelivery.activities.MyFoodCartActivity;
import com.technorizen.omniser.fooddelivery.activities.PinLocationActivity;
import com.technorizen.omniser.fooddelivery.adapters.AdapterAllRes;
import com.technorizen.omniser.fooddelivery.adapters.AdapterHomeFoodCat;
import com.technorizen.omniser.fooddelivery.models.ModelAllRes;
import com.technorizen.omniser.grocerydelivery.activities.GraceryShopListActivity;
import com.technorizen.omniser.grocerydelivery.adapters.AdapterAllGrocery;
import com.technorizen.omniser.models.ModelLocations;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemandmodels.ModelOnDemand;
import com.technorizen.omniser.pharmacydelivery.adapters.AdapterAllPharmacy;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
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

public class PharmacyShopListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Context mContext = PharmacyShopListActivity.this;
    ActivityPharmacyShopListBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelAllRes modelAllRes;
    TextView tvHomeAddress,tvWorkAddress;
    ImageView ivAddHomeAddress,ivAddWorkAddress,ivHomeEdit,ivWorkEdit;
    ModelLocations recentLocation = null;
    int AUTOCOMPLETE_REQUEST_CODE = 3;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 3000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private static final int REQUEST_CODE = 101;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_shop_list);
        ProjectUtil.changeStatusBarColor(PharmacyShopListActivity.this);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        EnableGPSAutoMatically();

        recentLocation = sharedPref.getLocationModel(AppConstant.LOCATION_DETAILS);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        String previousAdd = sharedPref.getDevLocation(AppConstant.DevADD);

        if(previousAdd != null && (!previousAdd.equals(""))) {
            binding.tvLocation.setText(previousAdd);
            currentLocation = new Location("");
            currentLocation.setLatitude(Double.parseDouble(sharedPref.getDevLocation(AppConstant.Devlat)));
            currentLocation.setLongitude(Double.parseDouble(sharedPref.getDevLocation(AppConstant.Devlon)));
            getAllResCategories();
        } else {
            fetchLocation();
        }

        init();

    }

    private void init() {
        binding.tvFavorite.setOnClickListener(v -> {
            getAllFavoriteRes(currentLocation);
        });

        binding.tvAll.setOnClickListener(v -> {
            fetchLocation();
        });

        binding.rlCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MyFoodCartActivity.class));
        });

        binding.tvLocation.setOnClickListener(v -> {

            if(recentLocation != null) {
                recentLocationsDialog(recentLocation);
            } else {
                getRecentLocation();
            }

        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchLocation();
            }
        });

        binding.etSerach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                try {

                    query = query.toString().toLowerCase();

                    final ArrayList<ModelAllRes.Result> filteredList = new ArrayList<ModelAllRes.Result>();

                    if(modelAllRes != null) {
                        for (int i = 0; i < modelAllRes.getResult().size(); i++) {

                            final String text = modelAllRes.getResult().get(i).getName().toLowerCase();

                            if (text.contains(query)) {
                                filteredList.add(modelAllRes.getResult().get(i));
                            }

                        }

                        AdapterAllRes adapterCarShippment = new AdapterAllRes(mContext,filteredList);
                        binding.rvAllRes.setAdapter(adapterCarShippment);

                    }
                } catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.ivFilter.setOnClickListener(v -> {
            dialogFilter();
        });

    }

    private void getAllFavoriteRes(Location location) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

       //  Toast.makeText(mContext, modelLogin.getResult().getId(), Toast.LENGTH_SHORT).show();
        Log.e("hgfaksdfhs","location.getLatitude() = " + location.getLatitude());
        Log.e("hgfaksdfhs","location.getLongitude() = " + location.getLongitude());

        call = ApiFactory.loadInterface().getFavPharmacy(String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()),
                modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("jhlashfldasfasf","response = " + response);
                    Log.e("jhlashfldasfasf","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelAllRes = new Gson().fromJson(responseString, ModelAllRes.class);

                        AdapterAllPharmacy adapterAllGrocery = new AdapterAllPharmacy(mContext, modelAllRes.getResult());
                        binding.rvAllRes.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvAllRes.setAdapter(adapterAllGrocery);

                    } else {
                        AdapterAllPharmacy adapterAllGrocery = new AdapterAllPharmacy(mContext,null);
                        binding.rvAllRes.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvAllRes.setAdapter(adapterAllGrocery);
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                // Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }

        });

    }

    private void getAllResCategories() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getAllSubServiceTypes("6");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("dfsdfsdsdfs","responseString = " + response);
                    Log.e("dfsdfsdsdfs","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelOnDemand modelcat = new Gson().fromJson(responseString, ModelOnDemand.class);

                        AdapterHomeFoodCat adapterHomeFoodCat = new AdapterHomeFoodCat(mContext,
                                modelcat.getResult(),PharmacyShopListActivity.this::getRestByCat);
                        binding.rvCategory.setAdapter(adapterHomeFoodCat);

                        getRestaurants(currentLocation,"34");

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                // Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }

        });

    }

    public void getRestByCat(String id) {
        getRestaurants(currentLocation,id);
    }

    private void recentLocationsDialog(ModelLocations modelLocations) {
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

            if(!tvHomeAddress.getText().toString().trim().isEmpty()) {
                binding.tvLocation.setText(tvHomeAddress.getText().toString().trim());
                sharedPref.setDevLocation(AppConstant.DevADD,tvHomeAddress.getText().toString().trim());
                sharedPref.setDevLocation(AppConstant.Devlat,recentLocation.getHome_location().getLat());
                sharedPref.setDevLocation(AppConstant.Devlon,recentLocation.getHome_location().getLon());
                dialog.dismiss();
            }
        });

        tvWorkAddress.setOnClickListener(v -> {

            Log.e("asfsafdsf","tvWorkAddress = " + tvWorkAddress.getText().toString().trim());

            if(!tvWorkAddress.getText().toString().trim().isEmpty()) {
                binding.tvLocation.setText(tvWorkAddress.getText().toString().trim());
                sharedPref.setDevLocation(AppConstant.DevADD,tvWorkAddress.getText().toString().trim());
                sharedPref.setDevLocation(AppConstant.Devlat,recentLocation.getWork_location().getLat());
                sharedPref.setDevLocation(AppConstant.Devlon,recentLocation.getWork_location().getLon());
                dialog.dismiss();
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
            setCurrentLocation();
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
                binding.tvLocation.setText(data.getAddress());
                currentLocation.setLatitude(Double.parseDouble(data.getLat()));
                currentLocation.setLongitude(Double.parseDouble(data.getLon()));

                sharedPref.setDevLocation(AppConstant.DevADD,data.getAddress());
                sharedPref.setDevLocation(AppConstant.Devlat,data.getLat());
                sharedPref.setDevLocation(AppConstant.Devlon,data.getLon());
            }
        });

        etSerach.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            intent.putExtra("type","");
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            dialog.dismiss();
        });

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        cvLocation.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(mContext, PinLocationActivity.class);
            intent.putExtra("type","");
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

    private void getRecentLocation() {
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
                        recentLocationsDialog(recentLocation);
                    } else {
                        recentLocation = new Gson().fromJson(responseString, ModelLocations.class);
                        recentLocationsDialog(recentLocation);
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                // Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });

    }

    void setCurrentLocation() {

        if (ActivityCompat.checkSelfPermission (
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PharmacyShopListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(mContext, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(currentLocation.getLatitude()
                                , currentLocation.getLongitude(), 1);
                        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        String address = addresses.get(0).getAddressLine(0);

                        binding.tvLocation.setText(address);
                        sharedPref.setDevLocation(AppConstant.DevADD,address);
                        sharedPref.setDevLocation(AppConstant.Devlat, String.valueOf(currentLocation.getLatitude()));
                        sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Toast.makeText(getActivity(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                } else {
                    startLocationUpdates();
//                  currentLocation = new Location("");
//                  currentLocation.setLatitude(Double.parseDouble(modelLogin.getResult().getLat()));
//                  currentLocation.setLongitude(Double.parseDouble(modelLogin.getResult().getLon()));
                    Log.e("ivCurrentLocation", "location = " + location);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCardCount();
        startLocationUpdates();
    }

    private void getCardCount() {
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getCartCountApi(modelLogin.getResult().getId(),AppConstant.GROCERY);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        if(!"0".equals(jsonObject.getString("result"))) {
                            binding.rlCart.setVisibility(View.VISIBLE);
                            binding.tvCartCount.setText(jsonObject.getString("result"));
                        }

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                // Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }

        });

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

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        // onLocationChanged(locationResult.getLastLocation());
                        // getLastLocation();

                        if(locationResult != null ) {
                            Log.e("hdasfkjhksdf", "Location = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                        } else {
                            currentLocation = null;
                            fetchLocation();
                        }

                    }
                },
                Looper.myLooper());

    }

    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // toast("Success");
                            Log.e("dskgfhsdfsdf", "Success");
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //toast("GPS is not on");
                            Log.e("dskgfhsdfsdf", "No GPS");
                            Toast.makeText(PharmacyShopListActivity.this, "GPS is not on", Toast.LENGTH_SHORT).show();
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(PharmacyShopListActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // toast("Setting change not allowed");

                            Toast.makeText(PharmacyShopListActivity.this, "Setting change not allowed", Toast.LENGTH_SHORT).show();
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PharmacyShopListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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

                    getAllResCategories();

                    Log.e("ivCurrentLocation", "location = " + location);
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(mContext, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        String address = addresses.get(0).getAddressLine(0);

                        binding.tvLocation.setText(address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Toast.makeText(getActivity(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                } else {
                    currentLocation = new Location("");
                    currentLocation.setLatitude(Double.parseDouble(modelLogin.getResult().getLat()));
                    currentLocation.setLongitude(Double.parseDouble(modelLogin.getResult().getLon()));
                    Log.e("ivCurrentLocation", "location = " + location);
                }
            }
        });

    }

    private void getRestaurants(Location location,String id) {

        Log.e("mmojosdjols","user_id = " + modelLogin.getResult().getId());
        Log.e("mmojosdjols","location.getLatitude() = " + location.getLatitude());
        Log.e("mmojosdjols","location.getLongitude() = " + location.getLongitude());

        String lat="",lon="";

        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getAllPharmacyStore(String.valueOf(location.getLatitude())
                , String.valueOf(location.getLongitude()),modelLogin.getResult().getId(),id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("jhlashfldasfasf","response = " + response);
                    Log.e("jhlashfldasfasf","response = " + response);

                    if(jsonObject.getString("status").equals("1")) {

                        modelAllRes = new Gson().fromJson(responseString, ModelAllRes.class);

                        AdapterAllPharmacy adapterAllRes = new AdapterAllPharmacy(mContext, modelAllRes.getResult());
                        binding.rvAllRes.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvAllRes.setAdapter(adapterAllRes);

                    } else {
                        AdapterAllPharmacy adapterAllRes = new AdapterAllPharmacy(mContext,null);
                        binding.rvAllRes.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvAllRes.setAdapter(adapterAllRes);
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
               // Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });
    }

    private void dialogFilter() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.restaurant_filter_dialog);

        Button btApply = dialog.findViewById(R.id.btApply);

        btApply.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialog.show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}