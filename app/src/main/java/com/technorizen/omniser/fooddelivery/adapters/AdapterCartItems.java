package com.technorizen.omniser.fooddelivery.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.activities.MyFoodCartActivity;
import com.technorizen.omniser.fooddelivery.activities.RestaurantDetailsActivity;
import com.technorizen.omniser.fooddelivery.models.ModelCartItems;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;
import com.technorizen.omniser.utils.UpdateTotalPayInterface;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterCartItems extends RecyclerView.Adapter<AdapterCartItems.MyViewHolder> {

    Context mContext;
    static ArrayList<ModelCartItems.Result> cartItemList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    UpdateTotalPayInterface updateTotalPayInterface;
    public static String toppingTotalMain;
    static int count = 1;
    static double itemTotal = 0.0;
    static double OptionitemTotal = 0.0;
    static int currentPosition;
    int parentPosition = 0;
    static Dialog dialog;
    int toppingTotal = 0;
    public static boolean isFirstTime = false;
    public static String optionPrice = "0", optionId = "";

    public AdapterCartItems(Context mContext, ArrayList<ModelCartItems.Result> cartItemList) {
        this.mContext = mContext;
        this.cartItemList = cartItemList;
        sharedPref = SharedPref.getInstance(mContext);
        updateTotalPayInterface = (UpdateTotalPayInterface) mContext;
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_cart_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelCartItems.Result data = cartItemList.get(position);

        MyFoodCartActivity.storeId = data.getRes_id();

        if ("es".equals(sharedPref.getLanguage("lan"))) {
            holder.tvName.setText(data.getItem_details().getItem_name_es());
        } else {
            holder.tvName.setText(data.getItem_details().getItem_name());
        }

        holder.tvPrice.setText(AppConstant.DOLLER_SIGN + data.getItem_details().getItem_price() +
                " x " + data.getQuantity());
        holder.tvQuantity.setText(data.getQuantity());

        Glide.with(mContext).load(data.getItem_details().getImage())
                .apply(new RequestOptions()
                        .override(400, 400))
                .into(holder.ivItemImage);

        try {
            if (data.getTopingss() != null && data.getTopingss().size() != 0) {
                boolean isData = false;
                for (int i = 0; i < data.getTopingss().size(); i++) {
                    if (data.getTopingss().get(i).getStatus().equals("true")) {
                        isData = true;
                        addToppingLayout(holder.llTopping, data.getTopingss().get(i).getValue()
                                , data.getTopingss().get(i).getPrice());
                    }
                }
                if (isData) {
                    holder.tvToppingTitle.setVisibility(View.VISIBLE);
                    holder.llTopping.setVisibility(View.VISIBLE);
                } else {
                    holder.tvToppingTitle.setVisibility(View.GONE);
                    holder.llTopping.setVisibility(View.GONE);
                }
            } else {
                holder.tvToppingTitle.setVisibility(View.GONE);
                holder.llTopping.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.tvToppingTitle.setVisibility(View.GONE);
            holder.llTopping.setVisibility(View.GONE);
        }

        holder.ivEdit.setOnClickListener(v -> {
            openItemDetailDialog(data, position);
        });

        holder.ivDelete.setOnClickListener(v -> {
            deleteItems(data.getItem_details().getStore_id(), data.getId(), position);
        });

    }

    private void addToppingLayout(LinearLayout linearLayout, String name, String price) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_topping_layout, null, false);

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPrice = view.findViewById(R.id.tvPrice);

        tvName.setText(name);
        tvPrice.setText(AppConstant.DOLLER_SIGN + price);

        linearLayout.addView(view);
    }

    private void openItemDetailDialog(ModelCartItems.Result data, int pos) {

        currentPosition = pos;

        Log.e("asfsdfdsf", "data.getPrice() = " + data.getPrice());

        dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.food_item_detail_dialog);
        dialog.setCancelable(true);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    dialog.dismiss();
                return false;
            }
        });

        ImageView ivItemImage = dialog.findViewById(R.id.ivItemImage);
        ImageView ivMinus = dialog.findViewById(R.id.ivMinus);
        ImageView ivPlus = dialog.findViewById(R.id.ivPlus);
        ImageView ivBack = dialog.findViewById(R.id.ivBack);
        TextView tvItemName = dialog.findViewById(R.id.tvItemName);
        ListView toppingListView = dialog.findViewById(R.id.toppingListView);
        ListView optionListview = dialog.findViewById(R.id.optionListview);
        TextView tvQuantity = dialog.findViewById(R.id.tvQuantity);
        TextView tvItemDesp = dialog.findViewById(R.id.tvItemDesp);
        TextView tvAddItem = dialog.findViewById(R.id.tvAddItem);
        TextView tvPrice = dialog.findViewById(R.id.tvPrice);

        tvAddItem.setText(mContext.getString(R.string.update));

        itemTotal = Double.parseDouble(data.getPrice());
        count = Integer.parseInt(data.getQuantity());

        Log.e("asfsdfdsf", "data.getTopingss() = " + data.getTopingss());
        Log.e("asfsdfdsf", "data.getQuantity() = " + data.getQuantity());
        Log.e("asfsdfdsf", "count = " + count);

        Glide.with(mContext)
                .load(data.getItem_details().getImage())
                .apply(new RequestOptions().override(400, 400))
                .into(ivItemImage);

        tvQuantity.setText(data.getQuantity());

        if ("es".equals(sharedPref.getLanguage("lan"))) {
            tvItemName.setText(data.getItem_details().getItem_name_es());
        } else {
            tvItemName.setText(data.getItem_details().getItem_name());
        }

        tvPrice.setText(AppConstant.DOLLER_SIGN + " " + data.getPrice());
        tvItemDesp.setText(data.getItem_details().getDescription());

        AdapterEditFoodTopping adapterFoodTopping = new AdapterEditFoodTopping(mContext, data.getTopingss(), data.getItem_details().getItem_price());
        toppingListView.setAdapter(adapterFoodTopping);

        AdapterFoodEditOptions adapterFoodOptions = new AdapterFoodEditOptions(mContext, data.getExtra_options());
        optionListview.setAdapter(adapterFoodOptions);

        ivMinus.setOnClickListener(v -> {
            if (count > 1) {
                count--;
                data.setQuantity(String.valueOf(count));
                tvQuantity.setText(data.getQuantity());
                itemTotal = Double.parseDouble(data.getItem_details().getItem_price()) * count;
                itemTotal = itemTotal + (getToppingTotal(data) * count);
                itemTotal = itemTotal + (Double.parseDouble(optionPrice) * count);
                tvPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);
            }
        });

        ivPlus.setOnClickListener(v -> {
            count++;
            data.setQuantity(String.valueOf(count));
            tvQuantity.setText(data.getQuantity());
            itemTotal = Double.parseDouble(data.getItem_details().getItem_price()) * count;
            itemTotal = itemTotal + (getToppingTotal(data) * count);
            itemTotal = itemTotal + (Double.parseDouble(optionPrice) * count);
            tvPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);
        });

        tvAddItem.setOnClickListener(v -> {

            Log.e("fsdfsdf", "getSelectedToppingIds = " + getSelectedToppingIds(data));

            if (data.getQuantity().equals("0") || data.getQuantity().equals("")) {
                data.setQuantity("1");
                addToCart(tvQuantity.getText().toString().trim(), String.valueOf(itemTotal), getSelectedToppingIds(data), data, dialog);
            } else {
                addToCart(tvQuantity.getText().toString().trim(), String.valueOf(itemTotal), getSelectedToppingIds(data), data, dialog);
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

    public static void updatePrice(String toppingTotal, String price) {
        Log.e("sdfsfsdf", "Topping updatePrice");
        Log.e("sdfsfsdf", "toppingTotal = " + toppingTotal);
        Log.e("sdfsfsdf", "itemTotal = " + itemTotal);
        Log.e("sdfsfsdf", "count = " + count);

        itemTotal = Double.parseDouble(price) * count;
        itemTotal = itemTotal + (count * Double.parseDouble(toppingTotal));
        itemTotal = itemTotal + (count * Double.parseDouble(optionPrice));
        Log.e("sdfsfsdf", "itemList.get(currentPosition).getItem_price() = " + cartItemList.get(currentPosition).getItem_details().getItem_price());
        Log.e("sdfsfsdf", "currentPosition = " + currentPosition);
        Log.e("sdfsfsdf", "itemTotal after update = " + itemTotal);

        TextView textPrice = dialog.findViewById(R.id.tvPrice);
        textPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);

    }

    public static void updateOption(int prePosition, int realPosition, String price) {
        Log.e("optionPrice", "updateOption = " + optionPrice);
        Log.e("optionPrice", "realPosition = " + realPosition);
        Log.e("optionPrice", "price = " + price);
        Log.e("optionPrice", "isFirstTime = " + isFirstTime);
        ListView optionListview = dialog.findViewById(R.id.optionListview);

        View view = optionListview.getChildAt(prePosition);
        RadioButton radioButton = view.findViewById(R.id.rbOption);

        if (isFirstTime) {
            itemTotal = itemTotal - (count * Double.parseDouble(optionPrice));
            itemTotal = itemTotal + (count * Double.parseDouble(price));
            radioButton.setChecked(false);
        } else {
            itemTotal = itemTotal + (count * Double.parseDouble(price));
            radioButton.setChecked(true);
        }

        if (prePosition == realPosition) {
            radioButton.setChecked(true);
        }

        optionPrice = price;

        TextView textPrice = dialog.findViewById(R.id.tvPrice);
        textPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);
    }

    private void addToCart(String quantity, String price, String topping, ModelCartItems.Result data, Dialog dialog) {
        ProjectUtil.showProgressDialog(mContext, true, mContext.getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().addToCart(
                data.getItem_id(),
                modelLogin.getResult().getId(),
                quantity,
                data.getRes_id(),
                price, AppConstant.FOOD, topping, optionId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    Log.e("responseresponse", "response = " + stringResponse);
                    Log.e("responseresponse", "response = " + response);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        Log.e("sdgdsgdsgdsg", "quantity = " + jsonObject.getString("quantity"));
                        // data.setItem_quantity(jsonObject.getString("quantity"));
                        //((RestaurantDetailsActivity) mContext).updateData(jsonObject.getString("cart_count"));
                        updateTotalPayInterface.updateCartData("", "", "");
                        dialog.dismiss();
                        notifyDataSetChanged();
                    } else {
                        data.setQuantity(String.valueOf(Integer.parseInt(data.getQuantity()) - 1));
                        notifyDataSetChanged();
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

    private String getSelectedToppingIds(ModelCartItems.Result data) {
        ArrayList<String> tempList = new ArrayList<>();
        if (data.getTopingss() == null || data.getTopingss().size() == 0) {
            return "";
        } else {
            for (int i = 0; i < data.getTopingss().size(); i++) {
                Log.e("sdfsfsdf", "inside For = " + i);
                Log.e("sdfsfsdf", "sdfsfsdf isChecked = " + data.getTopingss().get(i).getStatus());
                if (data.getTopingss().get(i).getStatus().equals("true")) {
                    tempList.add(data.getTopingss().get(i).getId());
                }
            }
            return TextUtils.join(",", tempList);
        }
    }

    private int getToppingTotal(ModelCartItems.Result data) {
        toppingTotal = 0;
        if (data.getTopingss() == null || data.getTopingss().size() == 0) {
            return 0;
        } else {
            for (int i = 0; i < data.getTopingss().size(); i++) {
                Log.e("sdfsfsdf", "inside For = " + i);
                Log.e("sdfsfsdf", "sdfsfsdf isChecked = " + data.getTopingss().get(i).getStatus());
                if (data.getTopingss().get(i).getStatus().equals("true")) {
                    toppingTotal = toppingTotal + Integer.parseInt(data.getTopingss().get(i).getPrice());
                }
            }
            return toppingTotal;
        }

    }

    //    private void openItemDetailDialog(ModelCartItems.Result data) {
//        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
//        dialog.setContentView(R.layout.food_item_detail_dialog);
//
//        ImageView ivItemImage = dialog.findViewById(R.id.ivItemImage);
//        ImageView ivMinus = dialog.findViewById(R.id.ivMinus);
//        ImageView ivPlus = dialog.findViewById(R.id.ivPlus);
//        ImageView ivBack = dialog.findViewById(R.id.ivBack);
//        TextView tvItemName = dialog.findViewById(R.id.tvItemName);
//        TextView tvQuantity = dialog.findViewById(R.id.tvQuantity);
//        TextView tvItemDesp = dialog.findViewById(R.id.tvItemDesp);
//        TextView tvAddItem = dialog.findViewById(R.id.tvAddItem);
//        TextView tvPrice = dialog.findViewById(R.id.tvPrice);
//
//        count = Integer.parseInt(data.getQuantity());
//        tvQuantity.setText(String.valueOf(count));
//        itemTotal = Double.parseDouble(data.getItem_details().getItem_price());
//        Glide.with(mContext)
//                .load(/*AppConstant.IMAGE_URL + */data.getItem_details().getImage())
//                .apply(new RequestOptions()
//                        .override(400,400))
//                .into(ivItemImage);
//
//        tvItemName.setText(data.getItem_details().getItem_name());
//        tvPrice.setText(AppConstant.DOLLER_SIGN + " " + data.getItem_details().getItem_price());
//        tvItemDesp.setText(data.getItem_details().getDescription());
//
//        ivMinus.setOnClickListener(v -> {
//            if(count > 1) {
//                count--;
//                data.setQuantity(String.valueOf(count));
//                tvQuantity.setText(String.valueOf(count));
//                itemTotal = Double.parseDouble(data.getItem_details().getItem_price()) * count;
//                tvPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);
//            }
//        });
//
//        ivPlus.setOnClickListener(v -> {
//            count++;
//            tvQuantity.setText(String.valueOf(count));
//            data.setQuantity(String.valueOf(count));
//            itemTotal = Double.parseDouble(data.getItem_details().getItem_price()) * count;
//            tvPrice.setText(AppConstant.DOLLER_SIGN + " " + itemTotal);
//        });
//
//        tvAddItem.setOnClickListener(v -> {
//            // addToCart(data,dialog);
//            itemTotal = 0.0;
//            count = 1;
////          FoodHomeActivity.cartHash.put(data.getId(),data);
////          sharedPref.setCartHash(AppConstant.CARTHASH,FoodHomeActivity.cartHash);
////
////          Toast.makeText(mContext, "" + itemTotal, Toast.LENGTH_SHORT).show();
////          ((RestaurantDetailsActivity) mContext).updateData();
////          dialog.dismiss();
//
//        });
//
//        ivBack.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        dialog.show();
//
//    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*private void addToCart(ModelCartItems.Result data,Dialog dialog) {
        ProjectUtil.showProgressDialog(mContext,true,mContext.getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().addToCart(data.getItem_id(),
                modelLogin.getResult().getId(),
                String.valueOf(data.getQuantity()),
                data.getRes_id(),
                data.getPrice());

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
                        // updateTotalPayInterface.updateCartData();
                        Log.e("sdgdsgdsgdsg","quantity = " + jsonObject.getString("quantity"));
                        data.setQuantity(jsonObject.getString("quantity"));
                        dialog.dismiss();
                        updateTotalPayInterface.updateCartData(
                                jsonObject.getString("items_total_price"),
                                jsonObject.getString("delivery_charges"),
                                jsonObject.getString("total_pay"));
                        notifyDataSetChanged();
                    } else {
                        // data.setQuantity(String.valueOf(Integer.parseInt(data.getQuantity()) - 1));
                        notifyDataSetChanged();
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

    }*/

    private void deleteItems(String resId, String itemId, int position) {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().deleteItemsApi(resId, itemId, modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    Log.e("responseresponse", "response = " + stringResponse);
                    Log.e("responseresponse", "response = " + response);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        cartItemList.remove(position);


                        updateTotalPayInterface.updateCartData(jsonObject.getString("items_total_price"),
                                jsonObject.getString("delivery_charges"),
                                jsonObject.getString("total_pay"));
                        ((MyFoodCartActivity) mContext).hideContinue(cartItemList);
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetChanged();
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
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView ivItemImage;
        ImageView ivDelete, ivMinus, ivPlus, ivEdit;
        LinearLayout llTopping;
        TextView tvName, tvPrice, tvQuantity, tvAddToCart, tvToppingTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivMinus = itemView.findViewById(R.id.ivMinus);
            ivPlus = itemView.findViewById(R.id.ivPlus);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvAddToCart = itemView.findViewById(R.id.tvAddToCart);
            tvToppingTitle = itemView.findViewById(R.id.tvToppingTitle);
            llTopping = itemView.findViewById(R.id.llTopping);
        }
    }

}
