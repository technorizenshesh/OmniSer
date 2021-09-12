package com.technorizen.omniser.taxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityRideOptBinding;
import com.technorizen.omniser.databinding.SerachDriverDialogBinding;
import com.technorizen.omniser.fooddelivery.activities.RestaurentListActivity;
import com.technorizen.omniser.fooddelivery.adapters.AdapterHomeFoodCat;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemandmodels.ModelOnDemand;
import com.technorizen.omniser.taxi.adapters.AdapterCarTypes;
import com.technorizen.omniser.taxi.models.ModelCarType;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class RideOptActivity
        extends AppCompatActivity
        implements OnMapReadyCallback {

    Context mContext = RideOptActivity.this;
    ActivityRideOptBinding binding;
    SupportMapFragment mapFragment;
    ArrayList<LatLng> polyLineLatLngs;
    LatLng pickLatLng,dropLatLng;
    PolylineOptions polylineOptions;
    GoogleMap MGoogleMap;
    String carId = null;
    private String estiCost,estiDistance;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    Dialog searchDialog;
    String pickAdd,dropAdd;
    String timeZone = TimeZone.getDefault().getID();
    private String request_id,currentDate,currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_opt);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        currentDate = ProjectUtil.getCurrentDate();
        currentTime = ProjectUtil.getCurrentTime();
        polyLineLatLngs = (ArrayList<LatLng>) getIntent().getSerializableExtra("polylines");
        pickLatLng = getIntent().getExtras().getParcelable("picklatlon");
        dropLatLng = getIntent().getExtras().getParcelable("droplatlon");

        pickAdd = ProjectUtil.getCompleteAddressString(mContext,pickLatLng.latitude,pickLatLng.longitude);
        dropAdd = ProjectUtil.getCompleteAddressString(mContext,dropLatLng.latitude,dropLatLng.longitude);

        init();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("fsdfsfsdfdsfdsf","Broadcast intent = " + intent.getStringExtra("object"));
            if(intent.getStringExtra("object") != null) {
                searchDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("object"));
                    request_id = String.valueOf(jsonObject.get("request_id"));
                    startActivity(new Intent(mContext,TrackTaxiAct.class)
                            .putExtra("request_id",request_id)
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter("driver_accept_request"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void init() {

        getAllCarTypes();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(RideOptActivity.this);

        binding.btRequestNow.setOnClickListener(v -> {
            // searchDriverDialog();
            bookingRequest();
        });

    }

    private void searchDriverDialog() {
        searchDialog = new Dialog(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        searchDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        SerachDriverDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.serach_driver_dialog,null,false);
        searchDialog.setContentView(dialogBinding.getRoot());
        searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogBinding.riipleIcon.startRippleAnimation();

        dialogBinding.btnCancel.setOnClickListener(v -> {
            searchDialog.dismiss();
        });
        searchDialog.show();
    }

    private void bookingRequest() {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());
        params.put("picuplat",String.valueOf(pickLatLng.latitude));
        params.put("pickuplon",String.valueOf(pickLatLng.longitude));
        params.put("droplat",String.valueOf(dropLatLng.latitude));
        params.put("droplon",String.valueOf(dropLatLng.longitude));
        params.put("timezone",timeZone);
        params.put("car_type_id",carId);
        params.put("picuplocation",pickAdd);
        params.put("dropofflocation",dropAdd);
        params.put("booktype","NOW");
        params.put("picklatertime",currentTime);
        params.put("picklaterdate",currentDate);
        params.put("payment_type","");
        params.put("shareride_type","");
        params.put("passenger","");
        params.put("current_time",currentTime);
        params.put("status","");
        params.put("apply_code","");
        params.put("vehical_type","");
        params.put("route_img","");
        params.put("estimate_charge_amount",estiCost);

        Log.e("kjhhajshdj","hashmap = " + params.toString());

        call = ApiFactory.loadInterface().addTaxiBookingRequest(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("kklklklklkl","response = " + response);
                    Log.e("kklklklklkl","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {
                       searchDriverDialog();
                    } else if(jsonObject.getString("status").equals("2")) {
                        alreadyRideAlert();
                    } else {
                        Toast.makeText(RideOptActivity.this,getString(R.string.no_driver_aval), Toast.LENGTH_SHORT).show();
                       // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                // Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void alreadyRideAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.you_are_already_ride));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MGoogleMap = googleMap;
        addPickDropMarkerOnMap(pickLatLng,dropLatLng);
        addPolyLinesOnMap(polyLineLatLngs);
        // MGoogleMap.getUiSettings().setMapToolbarEnabled(false);
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

        MGoogleMap.addMarker(markerOptionsPick);
        MGoogleMap.addMarker(markerOptionsDrop);
        ArrayList<LatLng> latlngList = new ArrayList<>();
        latlngList.add(pickUpLatLng);
        latlngList.add(dropOffLatLng);

        zoomRoute(MGoogleMap, latlngList);
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
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(10);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.geodesic(true);
        polylineOptions.zIndex(5f);
        MGoogleMap.addPolyline(polylineOptions);
    }

    private void getAllCarTypes() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getCarTypes(
                String.valueOf(pickLatLng.latitude),String.valueOf(pickLatLng.longitude),
                String.valueOf(dropLatLng.latitude),String.valueOf(dropLatLng.longitude));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("dfsdfsdsdfs","responseString = " + response);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelCarType modelCarType = new Gson().fromJson(responseString, ModelCarType.class);

                        AdapterCarTypes adapterCarTypes = new AdapterCarTypes(mContext, modelCarType.getResult(), RideOptActivity.this::setCarId);
                        binding.rvCarTypes.setAdapter(adapterCarTypes);

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                // Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void setCarId(String carId,String amount,String distance) {
        this.carId = carId;
        estiCost = amount;
        estiDistance = distance;
        binding.tvEstiCost.setText(AppConstant.DOLLER_SIGN + amount);
        binding.tvEstiDistance.setText(distance+" Km");
        Log.e("sgfhdgshjfsf"," carId = " + carId);
    }


}