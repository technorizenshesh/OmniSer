package com.technorizen.omniser.fooddelivery.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityPinLocationBinding;
import com.technorizen.omniser.models.ModelLocations;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PinLocationActivity extends AppCompatActivity implements LocationListener {

    private ActivityPinLocationBinding binding;
    private LocationManager locationManager;
    private Location location;
    private GoogleMap mMap;
    double lat = 0, lng = 0;
    private Animation myAnim;
    private MenuItem mItem;
    Context mContext = PinLocationActivity.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String type = "", id = "";
    private int AUTOCOMPLETE_REQUEST_CODE_DIALOG_PICK = 1001;
    private LatLng pickUpLatLng;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pin_location);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(PinLocationActivity.this);

        Places.initialize(getApplicationContext(), getString(R.string.places_api_key));

        try {
            type = getIntent().getStringExtra("type");
            id = getIntent().getStringExtra("id");
        } catch (Exception e) {
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.select_location);

        initLocation();

        bindMap();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initLocation() {
        myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] arr = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
            requestPermissions(arr, 100);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        binding.tvAddress.setText(getCompleteAddressString(PinLocationActivity.this, lat, lng));
    }

    private void bindMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                if (location != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
                }
                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        lat = mMap.getCameraPosition().target.latitude;
                        lng = mMap.getCameraPosition().target.longitude;
                        binding.tvAddress.setText(getCompleteAddressString(PinLocationActivity.this, lat, lng));
                        binding.imgMarker.startAnimation(myAnim);
                    }
                });
            }
        });

        binding.tvAddress.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID,
                    Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_DIALOG_PICK);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_DIALOG_PICK) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                pickUpLatLng = place.getLatLng();
                try {
                    lat = pickUpLatLng.latitude;
                    lng = pickUpLatLng.longitude;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickUpLatLng, 18.0f));
                    binding.tvAddress.setText(getCompleteAddressString(PinLocationActivity.this, lat, lng));
                    binding.imgMarker.startAnimation(myAnim);
                } catch (Exception e) {
                }
            }
        }

    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "getting address...";
        if (context != null) {
            Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                    Log.w("My Current address", strReturnedAddress.toString());
                } else {
                    strAdd = "No Address Found";
                    Log.w("My Current address", "No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                strAdd = "Cant get Address";
                Log.w("My Current address", "Canont get Address!");
            }
        }
        return strAdd;
    }

    @Override
    public void onLocationChanged(Location loc) {
        location = loc;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mItem = item;
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pin_manu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

//        Log.e("zdfdsfds","lat = " + lat);
//        Log.e("zdfdsfds","add = " + binding.tvAddress.getText().toString().trim());
//        Log.e("zdfdsfds","lng = " + lng);

        if (lat != 0) {
            Intent intent = new Intent();
            intent.putExtra("add", binding.tvAddress.getText().toString().trim());
            intent.putExtra("lat", lat);
            intent.putExtra("lon", lng);
            if (type.equals("home")) {
                addLocationApi(type);
            } else if (type.equals("work")) {
                addLocationApi(type);
            } else if (type.equals("map")) {
                setResult(3, intent);
                finish();
            } else if (type.equals("edithome")) {
                Log.e("sdfdsfds", "edithome");
                editLocationApi(type);
            } else if (type.equals("editwork")) {
                Log.e("sdfdsfds", "edithome");
                editLocationApi(type);
            } else if (type.equals("register")) {
                setResult(10, intent);
                finish();
            } else if (type.equals("pick")) {
                setResult(222, intent);
                finish();
            } else if (type.equals("drop")) {
                setResult(333, intent);
                finish();
            } else {
                setResult(3, intent);
                finish();
            }
        } else {
            Toast.makeText(this, "Location Not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void editLocationApi(String type) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().editRecentLocation(modelLogin.getResult().getId(),
                binding.tvAddress.getText().toString().trim(),
                id, String.valueOf(lat), String.valueOf(lng));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        Log.e("dfasfasd", "response = " + response);
                        Log.e("dfasfasd", "responseString = " + responseString);

                        ModelLocations modelLocations = new Gson().fromJson(responseString, ModelLocations.class);
                        sharedPref.setLocationModel(AppConstant.LOCATION_DETAILS, modelLocations);

                        Log.e("sdfdsfds", "type = " + type);

                        if (type.equals("edithome")) {
                            Intent intent = new Intent();
                            intent.putExtra("add", binding.tvAddress.getText().toString().trim());
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lng);
                            intent.putExtra("data", modelLocations);
                            setResult(6, intent);
                            finish();
                        } else if (type.equals("editwork")) {
                            Intent intent = new Intent();
                            intent.putExtra("add", binding.tvAddress.getText().toString().trim());
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lng);
                            intent.putExtra("data", modelLocations);
                            setResult(7, intent);
                            finish();
                        }
                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }
        });
    }

    private void addLocationApi(String type) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().addRecentLocation(modelLogin.getResult().getId()
                , binding.tvAddress.getText().toString().trim(), String.valueOf(lat), String.valueOf(lng), type);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        ModelLocations modelLocations = new Gson().fromJson(responseString, ModelLocations.class);
                        sharedPref.setLocationModel(AppConstant.LOCATION_DETAILS, modelLocations);

                        if (type.equals("home")) {
                            Intent intent = new Intent();
                            intent.putExtra("add", binding.tvAddress.getText().toString().trim());
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lng);
                            intent.putExtra("data", modelLocations);
                            setResult(5, intent);
                            finish();
                        } else if (type.equals("work")) {
                            Intent intent = new Intent();
                            intent.putExtra("add", binding.tvAddress.getText().toString().trim());
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lng);
                            intent.putExtra("data", modelLocations);
                            setResult(4, intent);
                            finish();
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("add", binding.tvAddress.getText().toString().trim());
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lng);
                            setResult(3, intent);
                            finish();
                        }
                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }
        });
    }

}
