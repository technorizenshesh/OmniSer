package com.technorizen.omniser.fooddelivery.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.activities.FoodHomeActivity;
import com.technorizen.omniser.fooddelivery.activities.RestaurantDetailsActivity;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;
import com.technorizen.omniser.utils.UpdateFoodItemsModel;
import org.json.JSONObject;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterResItems extends
        RecyclerView.Adapter<AdapterResItems.MyViewHolder> {

    Context mContext;
    static ArrayList<ModelResTypeItems.Result.Item_data> itemList = new ArrayList<>();
    SharedPref sharedPref;
    ModelLogin modelLogin;
    UpdateFoodItemsModel updateFoodItemsModel;
    String idd;
    public static String toppingTotalMain;
    static int count=1;
    static double itemTotal = 0.0;
    static double OptionitemTotal=0.0;
    static int parentPosition = 0,currentPosition = 0;
    static Dialog dialog;
    int toppingTotal=0;

    public AdapterResItems(Context mContext,ArrayList<ModelResTypeItems.Result.Item_data> reslist,String id,int position) {
        this.mContext = mContext;
        this.itemList = reslist;
        this.idd = id;
        this.parentPosition = position;
        updateFoodItemsModel = (UpdateFoodItemsModel) mContext;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_res_tems,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {

        ModelResTypeItems.Result.Item_data data = itemList.get(position);

        Log.e("asfasdasd","datadatadata = " + data.getItem_name());

        if("es".equals(sharedPref.getLanguage("lan"))) {
            holder.tvName.setText(data.getItem_name_es());
            holder.tvShortDesp.setText(data.getShort_description_es());
        } else {
            holder.tvName.setText(data.getItem_name());
            holder.tvShortDesp.setText(data.getShort_description());
        }

        holder.tvPrice.setText(AppConstant.DOLLER_SIGN + data.getItem_price());

        if(!data.getTag().equals("")) {
            holder.tvHashtag.setText(data.getTag());
            holder.tvHashtag.setVisibility(View.VISIBLE);
        } else {
            holder.tvHashtag.setVisibility(View.INVISIBLE);
        }

        Log.e("item_quantity","item_quantity = " + data.getItem_quantity());
        Log.e("item_quantity","getItem_name = " + data.getItem_name());

        Glide.with(mContext)
                .load(AppConstant.IMAGE_URL + data.getImage())
                .apply(new RequestOptions()
                .override(400,400))
                .into(holder.ivItemImage);

        holder.tvAdd.setOnClickListener(v -> {
            currentPosition = position;
            openItemDetailDialog(data,position);
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static void updatePrice(String toppingTotal,String price) {
        Log.e("sdfsfsdf","Topping updatePrice");
        Log.e("sdfsfsdf","toppingTotal = " + toppingTotal);
        Log.e("sdfsfsdf","itemTotal = " + itemTotal);
        Log.e("sdfsfsdf","count = " + count);
        Log.e("sdfsfsdf","currentPosition = " + currentPosition);

        itemTotal = Double.parseDouble(price) * count;
        itemTotal = itemTotal + (count * Double.parseDouble(toppingTotal));
        // Log.e("sdfsfsdf","itemList.get(currentPosition).getItem_price() = " + itemList.get(currentPosition).getItem_price());
        Log.e("sdfsfsdf","currentPosition = " + currentPosition);
        Log.e("sdfsfsdf","itemTotal after update = " + itemTotal);

        TextView textPrice = dialog.findViewById(R.id.tvPrice);
        textPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);
    }

    private void openItemDetailDialog(ModelResTypeItems.Result.Item_data data,int pos) {

        itemTotal = 0.0;
        currentPosition = pos;

        dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.food_item_detail_dialog);
        dialog.setCancelable(true);

        ImageView ivItemImage = dialog.findViewById(R.id.ivItemImage);
        ImageView ivMinus = dialog.findViewById(R.id.ivMinus);
        ImageView ivPlus = dialog.findViewById(R.id.ivPlus);
        ImageView ivBack = dialog.findViewById(R.id.ivBack);
        TextView tvItemName = dialog.findViewById(R.id.tvItemName);
        ListView toppingListView = dialog.findViewById(R.id.toppingListView);
        TextView tvQuantity = dialog.findViewById(R.id.tvQuantity);
        TextView tvItemDesp = dialog.findViewById(R.id.tvItemDesp);
        TextView tvAddItem = dialog.findViewById(R.id.tvAddItem);
        TextView tvPrice = dialog.findViewById(R.id.tvPrice);

        itemTotal = Double.parseDouble(data.getItem_price());
        Glide.with(mContext)
                .load(AppConstant.IMAGE_URL + data.getImage())
                .apply(new RequestOptions().override(400,400))
                .into(ivItemImage);

        tvItemName.setText(data.getItem_name());
        tvPrice.setText(AppConstant.DOLLER_SIGN + " " + data.getItem_price());
        tvItemDesp.setText(data.getDescription());

        AdapterFoodTopping adapterFoodTopping = new AdapterFoodTopping(mContext,data.getTopping(),data.getItem_price());
        toppingListView.setAdapter(adapterFoodTopping);

        ivMinus.setOnClickListener(v -> {
            if(count > 1) {
                count--;
                data.setItem_quantity(String.valueOf(count));
                tvQuantity.setText(data.getItem_quantity());
                itemTotal = Double.parseDouble(data.getItem_price()) * count;
                itemTotal = itemTotal + (getToppingTotal(data) * count);
                tvPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);
            }
        });

        ivPlus.setOnClickListener(v -> {
            count++;
            data.setItem_quantity(String.valueOf(count));
            tvQuantity.setText(data.getItem_quantity());
            itemTotal = Double.parseDouble(data.getItem_price()) * count;
            itemTotal = itemTotal + (getToppingTotal(data) * count);
            tvPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);
        });

        tvAddItem.setOnClickListener(v -> {

              Log.e("fsdfsdf","getSelectedToppingIds = " + getSelectedToppingIds(data));

              if(data.getItem_quantity().equals("0") || data.getItem_quantity().equals("")) {
                  data.setItem_quantity("1");
                  addToCart(tvQuantity.getText().toString().trim(),String.valueOf(itemTotal),getSelectedToppingIds(data),data,dialog);
              } else {
                  addToCart(tvQuantity.getText().toString().trim(),String.valueOf(itemTotal),getSelectedToppingIds(data),data,dialog);
              }

              itemTotal = 0.0;
              count = 1;
              toppingTotalMain = "0";
              OptionitemTotal = 0;

        });

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
            itemTotal = 0.0;
            count = 1;
            toppingTotalMain = "0";
            OptionitemTotal = 0;
        });

        dialog.show();

    }

    private String getSelectedToppingIds(ModelResTypeItems.Result.Item_data data) {
        ArrayList<String> tempList = new ArrayList<>();
        if(data.getTopping() == null || data.getTopping().size() == 0) {
            return "";
        } else {
            for(int i=0;i<data.getTopping().size();i++) {
                Log.e("sdfsfsdf","inside For = " + i);
                Log.e("sdfsfsdf","sdfsfsdf isChecked = " + data.getTopping().get(i).isChecked());
                if(data.getTopping().get(i).isChecked()) {
                    tempList.add(data.getTopping().get(i).getId());
                }
            }
            return TextUtils.join(",",tempList);
        }
    }

    private int getToppingTotal(ModelResTypeItems.Result.Item_data data) {
        toppingTotal = 0;
        if(data.getTopping() == null || data.getTopping().size() == 0) {
            return 0;
        } else {
            for(int i=0;i<data.getTopping().size();i++) {
                Log.e("sdfsfsdf","inside For = " + i);
                Log.e("sdfsfsdf","sdfsfsdf isChecked = " + data.getTopping().get(i).isChecked());
                if(data.getTopping().get(i).isChecked()) {
                    toppingTotal = toppingTotal + Integer.parseInt(data.getTopping().get(i).getPrice());
                }
            }
            return toppingTotal;
        }

    }

    private void deleteItems(String resId,String itemId,int position) {
        ProjectUtil.showProgressDialog(mContext,true,mContext.getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().deleteItemsApi(resId,itemId, modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    Log.e("responseresponse","response = " + stringResponse);
                    Log.e("responseresponse","response = " + response);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetChanged();
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
                // Log.e("anErroranError","anError = " + t.getMessage());
            }

        });

    }

    private void addToCart(String quantity,String price,String topping,ModelResTypeItems.Result.Item_data data,Dialog dialog) {
        ProjectUtil.showProgressDialog(mContext,true,mContext.getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().addToCart(data.getId(),
                modelLogin.getResult().getId(),
                quantity,
                data.getStore_id(),
                price,AppConstant.FOOD,topping);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    Log.e("responseresponse","response = " + stringResponse);
                    Log.e("responseresponse","response = " + response);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                        Log.e("sdgdsgdsgdsg","quantity = " + jsonObject.getString("quantity"));
                        // data.setItem_quantity(jsonObject.getString("quantity"));
                        ((RestaurantDetailsActivity) mContext).updateData(jsonObject.getString("cart_count"));
                        dialog.dismiss();
                        //notifyDataSetChanged();
                    } else {
                        data.setItem_quantity(String.valueOf(Integer.parseInt(data.getItem_quantity()) - 1));
                        //notifyDataSetChanged();
                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    ProjectUtil.pauseProgressDialog();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                // Log.e("anErroranError","anError = " + t.getMessage());
            }

        });

    }

    @Override
    public int getItemCount() {
        if(itemList == null || itemList.size() == 0) {
            return 0;
        } else {
            return itemList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView ivItemImage;
        TextView tvName,tvPrice,tvShortDesp,tvAddToCart,tvHashtag,tvAdd;
        ImageView ivMinus,ivPlus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvHashtag = itemView.findViewById(R.id.tvHashtag);
            ivMinus = itemView.findViewById(R.id.ivMinus);
            tvShortDesp = itemView.findViewById(R.id.tvShortDesp);
            tvAdd = itemView.findViewById(R.id.tvAdd);
            ivPlus = itemView.findViewById(R.id.ivPlus);
            tvAddToCart = itemView.findViewById(R.id.tvAddToCart);
        }


    }

}
