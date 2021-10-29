package com.technorizen.omniser.utils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @POST("forgot_password")
    Call<ResponseBody> forgotPass(@Query("email") String email);

    @POST("delete_cart_item")
    Call<ResponseBody> deleteCartItems(@Query("cart_id") String cart_id);

    @POST("get_wallet_amount")
    Call<ResponseBody> getWalletAmount(@Query("user_id") String user_id);

    @POST("get_my_orders")
    Call<ResponseBody> getMyOrders(@Query("user_id") String user_id,
                                   @Query("type") String type,
                                   @Query("status") String status);

    @POST("getRestaurantsDetail")
    Call<ResponseBody> getRestaurantDetails(@Query("res_id") String res_id,
                                            @Query("user_id") String user_id);

    @POST("getGroceryStoreDetail")
    Call<ResponseBody> getGroceryShopDetails(@Query("gros_id") String gros_id,
                                             @Query("user_id") String user_id);

    @POST("getPharmacyStoreDetail")
    Call<ResponseBody> getPharmacyShopDetails(@Query("pharmacy_id") String pharmacy_id,
                                              @Query("user_id") String user_id);

    @POST("get_cart_count")
    Call<ResponseBody> getCartCountApi(@Query("user_id") String user_id,
                                       @Query("type") String type);

    @POST("get_rettaurentItems_by_type")
    Call<ResponseBody> getResItemsWithType(@Query("res_id") String res_id,
                                           @Query("user_id") String user_id);

    @POST("get_resto_search_items")
    Call<ResponseBody> getSerachItemsApis(@Query("res_id") String res_id);

    @POST("setLike")
    Call<ResponseBody> setLike(@Query("res_id") String res_id,
                               @Query("status") String status,
                               @Query("user_id") String user_id);

    @POST("get_category")
    Call<ResponseBody> getAllServiceTypes();

    @POST("get6Restaurants")
    Call<ResponseBody> getRestaurants(@Query("user_id") String user_id,
                                      @Query("sub_cat_id") String sub_cat_id);

    @POST("get6GroceryStores")
    Call<ResponseBody> getGrocerStores(@Query("user_id") String user_id,
                                       @Query("sub_cat_id") String sub_cat_id);

    @POST("get6PharmacyStores")
    Call<ResponseBody> getPharmacyStores(@Query("user_id") String user_id,
                                         @Query("sub_cat_id") String sub_cat_id);

    @POST("get_all_ondemand_subcategory")
    Call<ResponseBody> getAllOnDemands();

    @POST("get_sub_category")
    Call<ResponseBody> getAllSubServiceTypes(@Query("category_id") String category_id);

    @POST("get_vehical_type")
    Call<ResponseBody> getCarTypes(@Query("picklat") String picklat,
                                   @Query("picklon") String picklon,
                                   @Query("droplat") String droplat,
                                   @Query("droplon") String droplon);
    @FormUrlEncoded
    @POST("booking_request")
    Call<ResponseBody> addTaxiBookingRequest(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST("add_user_taxi_feedback")
    Call<ResponseBody> addUserTaxiFeedbackApi(@FieldMap HashMap<String, String> param);

    @POST("getAllRestaurants")
    Call<ResponseBody> getAllRestaurants(@Query("lat") String lat,
                                         @Query("lon") String lon,
                                         @Query("user_id") String user_id,
                                         @Query("sub_cat_id") String sub_cat_id);

    @POST("favroit_restro")
    Call<ResponseBody> getFavRestaurants(@Query("lat") String lat,
                                         @Query("lon") String lon,
                                         @Query("user_id") String user_id);

    @POST("favroit_grocery")
    Call<ResponseBody> getFavGrocery(@Query("lat") String lat,
                                     @Query("lon") String lon,
                                     @Query("user_id") String user_id);

    @POST("favroit_pharmacy")
    Call<ResponseBody> getFavPharmacy(@Query("lat") String lat,
                                      @Query("lon") String lon,
                                      @Query("user_id") String user_id);

    @POST("getAllGroceryStore")
    Call<ResponseBody> getAllGroceryStore(@Query("lat") String lat,
                                          @Query("lon") String lon,
                                          @Query("user_id") String user_id,
                                          @Query("sub_cat_id") String sub_cat_id);

    @POST("getAllPharmacyStore")
    Call<ResponseBody> getAllPharmacyStore(@Query("lat") String lat,
                                           @Query("lon") String lon,
                                           @Query("user_id") String user_id,
                                           @Query("sub_cat_id") String sub_cat_id);

    @POST("get_wallet")
    Call<ResponseBody> getWallet(@Query("user_id") String user_id);

    @POST("get_enable_service")
    Call<ResponseBody> getMyEnableServices(@Query("provider_id") String provider_id);

    @POST("add_wallet_amount")
    Call<ResponseBody> addWalletAmount(@Query("user_id") String user_id,
                                       @Query("wallet_amount") String wallet_amount);

    @POST("get_transaction")
    Call<ResponseBody> getAllTransactions(@Query("user_id") String user_id);

    @POST("send_money")
    Call<ResponseBody> sendMoneyApi(@Query("user_id") String user_id,
                                    @Query("email") String email,
                                    @Query("amount") String amount);

    @POST("accept_and_reject_request_by_user")
    Call<ResponseBody> confirmByUser(@Query("provider_id") String provider_id,
                                     @Query("request_id") String request_id,
                                     @Query("user_id") String user_id,
                                     @Query("status") String status);

    @POST("get_onDemand_enable_service")
    Call<ResponseBody> getOnDemandEnableServices(@Query("provider_id") String provider_id,
                                                 @Query("cat_id") String cat_id);

    @POST("get_onDemand_disable_services")
    Call<ResponseBody> getOnDemandDisableService(@Query("provider_id") String provider_id,
                                                 @Query("cat_id") String cat_id);

    @POST("get_user_service_request_detail")
    Call<ResponseBody> getUserRequestDetails(/*@Query("user_id") String user_id,*/
            @Query("request_id") String request_id);

    @POST("get_profile")
    Call<ResponseBody> getProfile(@Query("user_id") String user_id);

    @POST("get_all_carts")
    Call<ResponseBody> getCards(@Query("user_id") String user_id);

    @POST("get_order_status")
    Call<ResponseBody> getOrderStatus(@Query("user_id") String user_id,
                                      @Query("order_id") String cat_id);

    @POST("get_service_details")
    Call<ResponseBody> getServiceDetail(@Query("id") String id);

    @POST("delete_cart")
    Call<ResponseBody> deleteCard(@Query("id") String id);

    @POST("add_enable_service")
    Call<ResponseBody> addEnableServices(@Query("provider_id") String provider_id, @Query("service_id") String service_id);

    @POST("get_disable_services")
    Call<ResponseBody> getMyDisableServices(@Query("provider_id") String provider_id);

    @POST("update_user_wallet")
    Call<ResponseBody> cutWalletAmountApi(@Query("user_id") String user_id);

    @POST("get_invoice_details")
    Call<ResponseBody> getInvoiceDetails(@Query("provider_id") String provider_id,
                                         @Query("request_id") String request_id);

    @POST("get_user_request")
    Call<ResponseBody> getUserRequest(@Query("user_id") String user_id);

    @POST("get_user_taxi_history")
    Call<ResponseBody> getUserTaxiRequest(@Query("user_id") String user_id);

    @POST("get_recent_location")
    Call<ResponseBody> getRecentLocations(@Query("user_id") String user_id);

    @POST("get_coupan_detail")
    Call<ResponseBody> getCouponDetails(@Query("user_id") String user_id,
                                        @Query("coupon_code") String coupon_code);

    @POST("bookOnDemandService")
    Call<ResponseBody> bookOnDemandServiceApi(@Query("provider_id") String provider_id,
                                              @Query("request_id") String request_id,
                                              @Query("user_id") String user_id,
                                              @Query("total_price") String total_price,
                                              @Query("trans_id") String trans_id,
                                              @Query("pay_method") String pay_method);

    @POST("add_recent_location")
    Call<ResponseBody> addRecentLocation(@Query("user_id") String user_id,
                                         @Query("address") String address,
                                         @Query("lat") String lat,
                                         @Query("lon") String lon,
                                         @Query("type") String type);

    @POST("edit_location")
    Call<ResponseBody> editRecentLocation(@Query("user_id") String user_id,
                                          @Query("address") String address,
                                          @Query("id") String id,
                                          @Query("lat") String lat,
                                          @Query("lon") String lon);

    @POST("add_feedback")
    Call<ResponseBody> feedbackApi(@Query("order_id") String order_id,
                                   @Query("user_id") String user_id,
                                   @Query("store_id") String store_id,
                                   @Query("rating") String rating,
                                   @Query("comment") String comment);

    @POST("login")
    Call<ResponseBody> login(@Query("email") String email,
                             @Query("password") String password,
                             @Query("mobile") String mobile,
                             @Query("lat") String lat,
                             @Query("lon") String lon,
                             @Query("type") String type,
                             @Query("register_id") String register_id);

    @POST("delete_cart_item")
    Call<ResponseBody> deleteItemsApi(@Query("res_id") String res_id,
                                      @Query("item_id") String item_id,
                                      @Query("user_id") String user_id);

    @POST("user_service_request")
    Call<ResponseBody> makeRequest(@Query("user_id") String user_id,
                                   @Query("delivery_address") String delivery_address,
                                   @Query("description") String description,
                                   @Query("lat") String lat,
                                   @Query("lon") String lon,
                                   @Query("service_id") String service_id,
                                   @Query("service_time") String service_time);

    @POST("get_check_status")
    Call<ResponseBody> getFoodStatus(@Query("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_booking_details")
    Call<ResponseBody> bookingDetails(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_chat")
    Call<ResponseBody> getAllMessagesCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("insert_chat")
    Call<ResponseBody> insertChatApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("cancel_by_user")
    Call<ResponseBody> cancelTripByUser(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("user_taxi_payment")
    Call<ResponseBody> doTaxiPayment(@FieldMap Map<String, String> params);

    @POST("add_cart")
    Call<ResponseBody> addCard(@Query("user_id") String user_id,
                               @Query("holder_name") String holder_name,
                               @Query("account_number") String account_number,
                               @Query("expire_date") String expire_date,
                               @Query("cvv") String cvv);

    @POST("getDileveryBoyLocation")
    Call<ResponseBody> getDevliveryLocations(@Query("user_id") String user_id);

    @POST("add_to_cart")
    Call<ResponseBody> addToCart(@Query("item_id") String item_id,
                                 @Query("user_id") String user_id,
                                 @Query("quantity") String quantity,
                                 @Query("res_id") String res_id,
                                 @Query("price") String price,
                                 @Query("type") String type,
                                 @Query("toping_id") String toping_id,
                                 @Query("option_id") String option_id);

    @POST("get_cart")
    Call<ResponseBody> getCartItemsApi(@Query("user_id") String user_id,
                                       @Query("type") String type);

    @FormUrlEncoded
    @POST("add_food_booking")
    Call<ResponseBody> foodBookingApiNEW(@FieldMap Map<String, String> params);

    @POST("add_food_booking")
    Call<ResponseBody> foodBookingApi(@Query("payment_id") String payment_id,
                                      @Query("user_id") String user_id,
                                      @Query("store_id") String store_id,
                                      @Query("payment_type") String payment_type,
                                      @Query("total_amount") String total_amount,
                                      @Query("delivery_time") String delivery_time,
                                      @Query("delivery_address") String delivery_address,
                                      @Query("cart_item") String cart_item,
                                      @Query("delivery_lat") String delivery_lat,
                                      @Query("delivery_lon") String delivery_lon,
                                      @Query("delivery_date") String delivery_date,
                                      @Query("type") String type
    );

    @POST("payment_strip")
    Call<ResponseBody> paymentStripCall(@Query("user_id") String user_id,
                                        @Query("order_id") String order_id,
                                        @Query("payment_method") String payment_method,
                                        @Query("total_amount") String total_amount,
                                        @Query("delivery_type") String delivery_type,
                                        @Query("delivery_time") String delivery_time,
                                        @Query("delivery_date") String delivery_date,
                                        @Query("delivery_address") String delivery_address,
                                        @Query("delivery_lat") String delivery_lat,
                                        @Query("delivery_lon") String delivery_lon,
                                        @Query("token") String token,
                                        @Query("currency") String currency
    );

    @POST("change_password")
    Call<ResponseBody> changePass(@Query("user_id") String user_id,
                                  @Query("c_password") String c_password,
                                  @Query("password") String password);

    @POST("add_service")
    Call<ResponseBody> addServices(@Query("provider_id") String provider_id,
                                   @Query("name") String name,
                                   @Query("service_id") String service_id,
                                   @Query("description") String description,
                                   @Query("price") String price);

    @POST("edit_services")
    Call<ResponseBody> editServices(@Query("id") String id,
                                    @Query("name") String name,
                                    @Query("service_id") String service_id,
                                    @Query("description") String description,
                                    @Query("provider_id") String provider_id,
                                    @Query("price") String price);

    @POST("get_provid_services")
    Call<ResponseBody> getAllServices(@Query("provider_id") String provider_id);

    @POST("delete_services")
    Call<ResponseBody> deleteService(@Query("id") String id);

}
