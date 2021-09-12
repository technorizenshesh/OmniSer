package com.technorizen.omniser.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
import com.technorizen.omniser.databinding.ActivityUpdateProfileBinding;
import com.technorizen.omniser.fooddelivery.activities.PinLocationActivity;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    Context mContext = UpdateProfileActivity.this;
    ActivityUpdateProfileBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    File file;
    Location currentLocation;
    private int GALLERY = 1, CAMERA = 2;
    private Geocoder geocoder;
    private LatLng locationLatLong;
    int AUTOCOMPLETE_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_update_profile);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(UpdateProfileActivity.this);

        init();

        if (!Places.isInitialized()) {
            Places.initialize(mContext,getString(R.string.places_api_key));
        }

    }

    private void init() {

        getProfile();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.ivProfile.setOnClickListener(v -> {
            requestPermission();
        });

        binding.address.setOnClickListener(v -> {

            Intent intent = new Intent(mContext, PinLocationActivity.class);
            intent.putExtra("type","register");
            startActivityForResult(intent, 10);

//            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,Place.Field.ADDRESS);
//
//            // Start the autocomplete intent
//            Intent intent = new Autocomplete.IntentBuilder(
//                    AutocompleteActivityMode.FULLSCREEN, fields)
//                    .build(mContext);
//            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

        });

        binding.rlUpdate.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.name.getText().toString())) {
                Toast.makeText(mContext, "Please enter name", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.email.getText().toString())) {
                Toast.makeText(mContext, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.address.getText().toString())) {
                Toast.makeText(mContext, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            } else {
                updateProfileApi();
            }
        });
    }

    private void getProfile() {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().getProfile(modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("ahjsgfhjs","response = " + response);

                    if(jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);

                        sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                        Picasso.get().load(modelLogin.getResult().getImage())
                                .error(R.drawable.default_img)
                                .placeholder(R.drawable.default_img).into(binding.ivProfile);
                        binding.name.setText(modelLogin.getResult().getName());
                        binding.email.setText(modelLogin.getResult().getEmail());
                        binding.address.setText(modelLogin.getResult().getAddress());
                        if(!(modelLogin.getResult().getLand_mark_add() == null ||
                                modelLogin.getResult().getLand_mark_add().equals(""))) {
                            binding.landMarkAddress.setText(modelLogin.getResult().getLand_mark_add());
                        }

                    } else {}

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

    private void updateProfileApi() {

        String lat,lon;

        if(locationLatLong == null){
            lat = modelLogin.getResult().getLat();
            lon = modelLogin.getResult().getLon();
        } else {
            lat = String.valueOf(locationLatLong.latitude);
            lon = String.valueOf(locationLatLong.longitude);
        }

        HashMap<String,String> params = new HashMap<>();

        params.put("user_id",modelLogin.getResult().getId());
        params.put("name",binding.name.getText().toString().trim());
        params.put("email",binding.email.getText().toString().trim());
        params.put("address",binding.address.getText().toString().trim());
        params.put("lat", lat);
        params.put("lon",lon);
        params.put("land_mark_add",binding.landMarkAddress.getText().toString().trim());

        HashMap<String,File> fileParams = new HashMap<>();
        fileParams.put("image",file);

        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        AndroidNetworking.upload(AppConstant.BASE_URL + "update_profile")
                .addMultipartParameter(params)
                .addMultipartFile(fileParams)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("status").equals("1")) {
                                modelLogin = new Gson().fromJson(response,ModelLogin.class);

                                sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                                startActivity(new Intent(mContext,DashboardActivity.class));
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            } else {}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {

            String add = data.getStringExtra("add");
            double lat = data.getDoubleExtra("lat",0.0);
            double lon = data.getDoubleExtra("lon",0.0);

            currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);

            binding.address.setText(add);

        } else if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = getRealPathFromURI(getImageUri(mContext, bitmap));
                    file = new File(path);
                    binding.ivProfile.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            binding.ivProfile.setImageBitmap(thumbnail);
            String path = getRealPathFromURI(getImageUri(mContext, thumbnail));
            file = new File(path);
        }

    }


}