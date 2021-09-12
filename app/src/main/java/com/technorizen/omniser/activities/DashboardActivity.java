package com.technorizen.omniser.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.adapters.AdapterCategoryAds;
import com.technorizen.omniser.adapters.AdapterRecentsLocations;
import com.technorizen.omniser.databinding.ActivityDashboardBinding;
import com.technorizen.omniser.fooddelivery.activities.FoodHomeActivity;
import com.technorizen.omniser.fooddelivery.activities.PinLocationActivity;
import com.technorizen.omniser.fooddelivery.adapters.AdapterHomeRes;
import com.technorizen.omniser.fooddelivery.models.ModelTopRatedRes;
import com.technorizen.omniser.grocerydelivery.activities.GroceryHomeActivity;
import com.technorizen.omniser.models.ModelAllServies;
import com.technorizen.omniser.models.ModelLocations;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemand.activities.OnDemandActivity;
import com.technorizen.omniser.pharmacydelivery.activities.PharmacyHomeActivity;
import com.technorizen.omniser.taxi.activities.TaxiHomeActivity;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ModelDriverLatLon;
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

public class DashboardActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Context  mContext = DashboardActivity.this;
    ActivityDashboardBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 3000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Location currentLocation;
    private Geocoder geocoder;
    private static final int REQUEST_CODE = 101;
    int AUTOCOMPLETE_REQUEST_CODE = 3;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    TextView tvHomeAddress,tvWorkAddress;
    ImageView ivAddHomeAddress,ivAddWorkAddress,ivHomeEdit,ivWorkEdit;
    ModelLocations recentLocation = null;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_dashboard);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        ProjectUtil.changeStatusBarColor(DashboardActivity.this);

        databaseReference = FirebaseDatabase
                .getInstance("https://omniserprovider.firebaseio.com/")
                .getReference();

        databaseReference.child(AppConstant.DRIVER_LAT_LON).child("780")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelDriverLatLon modelDriverLatLon = snapshot.getValue(ModelDriverLatLon.class);
                        Log.e("modelDriverLatLon","Driver Id = " + modelDriverLatLon.getDriverId());
                        Log.e("modelDriverLatLon","Driver Lat = " + modelDriverLatLon.getLat());
                        Log.e("modelDriverLatLon","Driver Lon = " + modelDriverLatLon.getLon());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        if (!Places.isInitialized()) {
            Places.initialize(mContext,getString(R.string.places_api_key));
        }

        recentLocation = sharedPref.getLocationModel(AppConstant.LOCATION_DETAILS);

        String previousAdd = sharedPref.getDevLocation(AppConstant.DevADD);

        if(previousAdd != null && (!previousAdd.equals(""))) {
            binding.tvLocation.setText(previousAdd);
            currentLocation = new Location("");
            currentLocation.setLatitude(Double.parseDouble(sharedPref.getDevLocation(AppConstant.Devlat)));
            currentLocation.setLongitude(Double.parseDouble(sharedPref.getDevLocation(AppConstant.Devlon)));
        } else {
            fetchLocation();
        }

        Log.e("gpsenabledornot","isGPSEnable() = " + isGPSEnable());

        EnableGPSAutoMatically();

        getFGPImgDesp();

        init();

    }

    private void changeLangDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.change_language_dialog);
        dialog.setCancelable(true);

        ProjectUtil.changeDialogStatusBar(dialog,mContext);

        Button btContinue = dialog.findViewById(R.id.btContinue);
        RadioButton radioEng = dialog.findViewById(R.id.radioEng);
        RadioButton radioSpanish = dialog.findViewById(R.id.radioSpanish);

        if("es".equals(sharedPref.getLanguage("lan"))) {
            radioSpanish.setChecked(true);
        } else {
            radioEng.setChecked(true);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        btContinue.setOnClickListener(v -> {
            if(radioEng.isChecked()) {
                ProjectUtil.updateResources(mContext,"en");
                sharedPref.setlanguage("lan","en");
                finish();
                startActivity(new Intent(mContext, DashboardActivity.class));
                dialog.dismiss();
            } else if(radioSpanish.isChecked()){
                ProjectUtil.updateResources(mContext,"es");
                sharedPref.setlanguage("lan","es");
                finish();
                startActivity(new Intent(mContext, DashboardActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void getFGPImgDesp() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getAllServiceTypes();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    ModelAllServies modelAllServies = new Gson().fromJson(responseString,ModelAllServies.class);

                    Log.e("dfsdfsdsdfs","responseString = " + response);

                    if(jsonObject.getString("status").equals("1")) {
                        for(int i=0;i<modelAllServies.getResult().size();i++) {
                            if(modelAllServies.getResult().get(i).getId().equals("2")) {
                                Glide.with(mContext)
                                        .load(modelAllServies.getResult().get(i).getImage())
                                        .apply(new RequestOptions().override(400,400))
                                        .into(binding.ivFood);
                                if("es".equals(sharedPref.getLanguage("lan"))) {
                                    binding.tvFoodDesp.setText(modelAllServies.getResult().get(i).getDiscription_spanish().trim());
                                } else {
                                    binding.tvFoodDesp.setText(modelAllServies.getResult().get(i).getDiscription().trim());
                                }
                            } else if(modelAllServies.getResult().get(i).getId().equals("5")) {
                                Glide.with(mContext)
                                        .load(modelAllServies.getResult().get(i).getImage())
                                        .apply(new RequestOptions().override(400,400))
                                        .into(binding.ivGrocery);
                                if("es".equals(sharedPref.getLanguage("lan"))) {
                                    binding.tvGroceryText.setText(modelAllServies.getResult().get(i).getDiscription_spanish().trim());
                                } else {
                                    binding.tvGroceryText.setText(modelAllServies.getResult().get(i).getDiscription().trim());
                                }
                            } else if(modelAllServies.getResult().get(i).getId().equals("6")){
                                Glide.with(mContext)
                                        .load(modelAllServies.getResult().get(i).getImage())
                                        .apply(new RequestOptions().override(400,400))
                                        .into(binding.ivPharma);
                                if("es".equals(sharedPref.getLanguage("lan"))) {
                                    binding.tvPharmaText.setText(modelAllServies.getResult().get(i).getDiscription_spanish().trim());
                                } else {
                                    binding.tvPharmaText.setText(modelAllServies.getResult().get(i).getDiscription().trim());
                                }
                            }
                        }
                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }

        });

    }

    private boolean isGPSEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void init() {

        binding.chgLang.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFGPImgDesp();
            }
        });

        binding.tvLocation.setOnClickListener(v -> {

            Log.e("sdkjfasfds","recentLocation = " + new Gson().toJson(sharedPref.getLocationModel(AppConstant.LOCATION_DETAILS)));

            if(recentLocation != null) {
                recentLocationsDialog(recentLocation);
            } else {
                getRecentLocation();
            }

        });

        binding.llMyBooking.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyBookingActivity.class));
        });

        binding.llProfile.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyProfileActivity.class));
        });

        binding.llWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContext,WalletActivity.class));
        });

        binding.llTaxi.setOnClickListener(v -> {
            startActivity(new Intent(mContext, TaxiHomeActivity.class));
        });

        binding.cvFoodDelivery.setOnClickListener(v -> {
            startActivity(new Intent(mContext, FoodHomeActivity.class));
        });

        binding.cvGroceryDev.setOnClickListener(v -> {
            startActivity(new Intent(mContext, GroceryHomeActivity.class));
        });

        binding.llOndemand.setOnClickListener(v -> {
            startActivity(new Intent(mContext, OnDemandActivity.class));
        });

        binding.cvPharmacyDelivery.setOnClickListener(v -> {
            startActivity(new Intent(mContext, PharmacyHomeActivity.class));
        });

        ArrayList<Integer> adsList = new ArrayList<>();
        adsList.add(R.drawable.mobile_home_slider_main_1);
        adsList.add(R.drawable.mobile_home_slider_main_2);

        binding.categorAdsSlider.setSliderAdapter(new AdapterCategoryAds(mContext,adsList));
        binding.categorAdsSlider.startAutoCycle();
        binding.categorAdsSlider.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        binding.categorAdsSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.categorAdsSlider.setScrollTimeInSec(5);
        binding.categorAdsSlider.setAutoCycle(true);
        binding.categorAdsSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

    }

    private void getRecentLocationNew() {
        Call<ResponseBody> call = ApiFactory.loadInterface().getRecentLocations(modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        ModelLocations recentLocation = new Gson().fromJson(responseString, ModelLocations.class);
                        sharedPref.setLocationModel(AppConstant.LOCATION_DETAILS,recentLocation);
                    } else {}

                } catch (Exception e) {
                    //Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getRecentLocation() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().getRecentLocations(modelLogin.getResult().getId());

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

    void setCurrentLocation() {

        if (ActivityCompat.checkSelfPermission (
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
        String previousAdd = sharedPref.getDevLocation(AppConstant.DevADD);

        if(previousAdd != null && (!previousAdd.equals(""))) {
            binding.tvLocation.setText(previousAdd);
        }
        // getRecentLocationNew();
        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
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

                        if(currentLocation != null) {
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(mContext, Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                String address = addresses.get(0).getAddressLine(0);

                                if(TextUtils.isEmpty(binding.tvLocation.getText().toString().trim())){
                                    binding.tvLocation.setText(address);
                                    sharedPref.setDevLocation(AppConstant.DevADD,address);
                                    sharedPref.setDevLocation(AppConstant.Devlat,String.valueOf(currentLocation.getLatitude()));
                                    sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if(locationResult != null) {
                                Log.e("hdasfkjhksdf", "Location = " + locationResult.getLastLocation());
                                currentLocation = locationResult.getLastLocation();
                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(mContext, Locale.getDefault());

                                try {
                                    addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    String address = addresses.get(0).getAddressLine(0);

                                    if(TextUtils.isEmpty(binding.tvLocation.getText().toString().trim())) {
                                        binding.tvLocation.setText(address);
                                        sharedPref.setDevLocation(AppConstant.DevADD,address);
                                        sharedPref.setDevLocation(AppConstant.Devlat, String.valueOf(currentLocation.getLatitude()));
                                        sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // currentLocation = null;
                                fetchLocation();
                            }
                        }

                    }

                },Looper.myLooper());

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
                            fetchLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //toast("GPS is not on");
                            Log.e("dskgfhsdfsdf", "No GPS");
                            Toast.makeText(DashboardActivity.this, "GPS is not on", Toast.LENGTH_SHORT).show();
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(DashboardActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // toast("Setting change not allowed");

                            Toast.makeText(DashboardActivity.this, "Setting change not allowed", Toast.LENGTH_SHORT).show();
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
        if (ActivityCompat.checkSelfPermission (
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                        addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        String address = addresses.get(0).getAddressLine(0);

                        if(TextUtils.isEmpty(binding.tvLocation.getText().toString().trim())){
                            binding.tvLocation.setText(address);
                            sharedPref.setDevLocation(AppConstant.DevADD,address);
                            sharedPref.setDevLocation(AppConstant.Devlat,String.valueOf(currentLocation.getLatitude()));
                            sharedPref.setDevLocation(AppConstant.Devlon,String.valueOf(currentLocation.getLongitude()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Toast.makeText(getActivity(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                } else {
                    startLocationUpdates();
//                    currentLocation = new Location("");
//                    currentLocation.setLatitude(Double.parseDouble(modelLogin.getResult().getLat()));
//                    currentLocation.setLongitude(Double.parseDouble(modelLogin.getResult().getLon()));
                    Log.e("ivCurrentLocation", "location = " + location);
                }
            }
        });

    }

    private void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to close the App?")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Toast.makeText(mContext, "requestCode = " + resultCode, Toast.LENGTH_SHORT).show();

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            String add = data.getStringExtra("add");
            double lat = data.getDoubleExtra("lat",0.0);
            double lon = data.getDoubleExtra("lon",0.0);

            currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);

            binding.tvLocation.setText(add);
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

    @Override
    public void onBackPressed() {
        exitAppDialog();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

}