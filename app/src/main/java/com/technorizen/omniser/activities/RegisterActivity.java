package com.technorizen.omniser.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityRegisterBinding;
import com.technorizen.omniser.fooddelivery.activities.PinLocationActivity;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.Compress;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Context mContext = RegisterActivity.this;
    ActivityRegisterBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    File file= null;
    String path = "",registerId = "";
    private int GALLERY = 1, CAMERA = 2;
    private Geocoder geocoder;
    private LatLng locationLatLong;
    int AUTOCOMPLETE_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register);
        sharedPref = SharedPref.getInstance(mContext);

        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (!task.isSuccessful()) {
                    return;
                }

                String token = task.getResult().getToken();
                registerId = token;

            }

        });

        if (!Places.isInitialized()) {
            Places.initialize(mContext,getString(R.string.places_api_key));
        }

        init();

    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void init() {

        binding.address.setOnClickListener(v -> {

            if(isGPSEnabled()) {
                Intent intent = new Intent(mContext, PinLocationActivity.class);
                intent.putExtra("type","register");
                startActivityForResult(intent, 10);
            } else {
                enableGPSAutoMatically();
            }

        });

        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(mContext,LoginActivity.class));
        });

        binding.ivProfile.setOnClickListener(v -> {
            requestPermission();
        });

        binding.rlRegister.setOnClickListener(v -> {

            if(file == null) {
                Toast.makeText(mContext, "Please upload profile photo", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.name.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter name", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.email.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if(!isValidEmail(binding.email.getText().toString().trim())) {
                Toast.makeText(mContext, "Invalid Email", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.phone.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.address.getText().toString().trim())) {
                Toast.makeText(mContext, "Please select address", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.landMarkAddress.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter landmark address", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.password.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter password", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.confirmPassword.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter confirm password", Toast.LENGTH_SHORT).show();
            } else if(!(binding.password.getText().toString().trim().equals(binding.confirmPassword.getText().toString()))) {
                Toast.makeText(mContext, "Password not matched", Toast.LENGTH_SHORT).show();
            } else if(!(binding.password.getText().toString().trim().length() >= 4)) {
                Toast.makeText(mContext, "Password must be at least 4 character long", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String,String> params = new HashMap<>();

                params.put("name",binding.name.getText().toString().trim());
                params.put("email",binding.email.getText().toString().trim());
                params.put("password",binding.password.getText().toString().trim());
                params.put("type","User");
                params.put("mobile",binding.phone.getText().toString().trim());
                params.put("address",binding.address.getText().toString().trim());
                params.put("land_mark_add",binding.landMarkAddress.getText().toString().trim());
                params.put("lat", String.valueOf(locationLatLong.latitude));
                params.put("lon",String.valueOf(locationLatLong.longitude));
                params.put("register_id",registerId);
                params.put("description","");

                HashMap<String, File> fileParams = new HashMap<>();
                fileParams.put("image",file);

                String mobileWithCounCode = (binding.ccp.getSelectedCountryCodeWithPlus()
                        + binding.phone.getText().toString().trim()).replace(" ","");

                startActivity(new Intent(mContext,VerifyOtpActivity.class)
                        .putExtra("resgisterHashmap" , params)
                        .putExtra("resgisterfileHashmap" , fileParams)
                        .putExtra("mobile" , mobileWithCounCode)
                );

            }

        });

    }

    private void requestPermission() {

        Dexter.withActivity((Activity) mContext)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                              showPictureDialog();

//                            final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
//                            dialog.setOnPickCancel(new IPickCancel() {
//                                @Override
//                                public void onCancelClick() {
//                                    dialog.dismiss();
//                                }
//                            }).setOnPickResult(new IPickResult() {
//                                @Override
//                                public void onPickResult(PickResult r) {
//                                    if (r.getError() == null) {
//
//                                        path = r.getPath();
//                                        file = new File(path);
//
//                                        Picasso.get().load(file).into(binding.ivProfile);
//
//                                    } else {
//                                        // Handle possible errors
//                                        // TODO: do what you have to do with r.getError();
//                                        Toast.makeText(mContext, r.getError().getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//
//                                }
//
//                            }).show(RegisterActivity.this);

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            openSettingsDialog();
                        }

                       /* // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // showPictureDialog();
                            final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                            dialog.setOnPickCancel(new IPickCancel() {
                                @Override
                                public void onCancelClick() {
                                    dialog.dismiss();
                                }
                            }).setOnPickResult(new IPickResult() {
                                @Override
                                public void onPickResult(PickResult r) {
                                    if (r.getError() == null) {

                                        path = r.getPath();
                                        file = new File(path);

                                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                        body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                                        Log.e("filefile","file.getName() = " + file.getName());

                                        Picasso.get().load(new File(path)).into(binding.profileImage);

                                    } else {
                                        // Handle possible errors
                                        Toast.makeText(mContext, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }

                            }).show(RegisterActivity.this);

                        }
*/

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(mContext, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }

                });

        pictureDialog.show();

    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    private void openSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


    private void signUpApi() {

    //        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
//        AndroidNetworking.upload(AppConstant.BASE_URL + "signup")
//                .addMultipartParameter(params)
//                .addMultipartFile(fileParams)
//                .build()
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//                        ProjectUtil.pauseProgressDialog();
//
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            if(jsonObject.getString("status").equals("1")) {
//                                modelLogin = new Gson().fromJson(response, ModelLogin.class);
//
//                                sharedPref.setBooleanValue(AppConstant.IS_REGISTER,true);
//                                sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);
//
//                                startActivity(new Intent(mContext,DashboardActivity.class));
//                                finish();
//                            } else {
//                                Toast.makeText(mContext, "Email Already Exist", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        ProjectUtil.pauseProgressDialog();
//                    }
//
//                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

            String add = data.getStringExtra("add");
            double lat = data.getDoubleExtra("lat",0.0);
            double lon = data.getDoubleExtra("lon",0.0);

            locationLatLong = new LatLng(lat,lon);

            binding.address.setText(add);

            //            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                locationLatLong = place.getLatLng();
//                try {
//
//                    List<Address> addresses;
//                    geocoder = new Geocoder(mContext, Locale.getDefault());
//
//                    try {
//                        addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                        String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                        String address2 = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                        String city = addresses.get(0).getLocality();
//                        String state = addresses.get(0).getAdminArea();
//                        String country = addresses.get(0).getCountryName();
//                        String postalCode = addresses.get(0).getPostalCode();
//
//                        binding.address.setText(address1);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    //setMarker(latLng);
//                }
//
//            }

        } else if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    // Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = getRealPathFromURI(contentURI);

                    file = new File(path);
                    binding.ivProfile.setImageURI(contentURI);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CAMERA) {

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String path = getRealPathFromURI(getImageUri(mContext, thumbnail));

            file = new File(path);

            binding.ivProfile.setImageBitmap(thumbnail);

            // mfile = new File(path);

        }

    }

    private void enableGPSAutoMatically() {
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
                    final LocationSettingsStates state = result.getLocationSettingsStates();

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // toast("Success");
                            Log.e("dskgfhsdfsdf","Success");
                            Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //toast("GPS is not on");
                            Log.e("dskgfhsdfsdf","No GPS");
                            Toast.makeText(RegisterActivity.this, "GPS is not on", Toast.LENGTH_SHORT).show();
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(RegisterActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // toast("Setting change not allowed");

                            Toast.makeText(RegisterActivity.this, "Setting change not allowed", Toast.LENGTH_SHORT).show();
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}