package com.technorizen.omniser.fooddelivery.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelAllRes implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Result
    {
        private String id;
        private String name;
        private String mobile;
        private String type;
        private String email;
        private String password;
        private String image;
        private String lat;
        private String lon;
        private String address;
        private String land_mark_add;
        private String description;
        private String register_id;
        private String social_id;
        private String date_time;

        private String state;

        private String wallet_amount;

        private String provider_status;

        private String store_type;

        private String open_time;

        private String close_time;

        private String step;

        private String distance;

        private String estimate_time;

        private String setLike;

        private String accept_order_or_not;

        public String getSetLike() {
            return setLike;
        }

        public void setSetLike(String setLike) {
            this.setLike = setLike;
        }

        public String getAccept_order_or_not() {
            return accept_order_or_not;
        }

        public void setAccept_order_or_not(String accept_order_or_not) {
            this.accept_order_or_not = accept_order_or_not;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setMobile(String mobile){
            this.mobile = mobile;
        }
        public String getMobile(){
            return this.mobile;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setEmail(String email){
            this.email = email;
        }
        public String getEmail(){
            return this.email;
        }
        public void setPassword(String password){
            this.password = password;
        }
        public String getPassword(){
            return this.password;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
        public void setLat(String lat){
            this.lat = lat;
        }
        public String getLat(){
            return this.lat;
        }
        public void setLon(String lon){
            this.lon = lon;
        }
        public String getLon(){
            return this.lon;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setLand_mark_add(String land_mark_add){
            this.land_mark_add = land_mark_add;
        }
        public String getLand_mark_add(){
            return this.land_mark_add;
        }
        public void setDescription(String description){
            this.description = description;
        }
        public String getDescription(){
            return this.description;
        }
        public void setRegister_id(String register_id){
            this.register_id = register_id;
        }
        public String getRegister_id(){
            return this.register_id;
        }
        public void setSocial_id(String social_id){
            this.social_id = social_id;
        }
        public String getSocial_id(){
            return this.social_id;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setState(String state){
            this.state = state;
        }
        public String getState(){
            return this.state;
        }
        public void setWallet_amount(String wallet_amount){
            this.wallet_amount = wallet_amount;
        }
        public String getWallet_amount(){
            return this.wallet_amount;
        }
        public void setProvider_status(String provider_status){
            this.provider_status = provider_status;
        }
        public String getProvider_status(){
            return this.provider_status;
        }
        public void setStore_type(String store_type){
            this.store_type = store_type;
        }
        public String getStore_type(){
            return this.store_type;
        }
        public void setOpen_time(String open_time){
            this.open_time = open_time;
        }
        public String getOpen_time(){
            return this.open_time;
        }
        public void setClose_time(String close_time){
            this.close_time = close_time;
        }
        public String getClose_time(){
            return this.close_time;
        }
        public void setStep(String step){
            this.step = step;
        }
        public String getStep(){
            return this.step;
        }
        public void setDistance(String distance){
            this.distance = distance;
        }
        public String getDistance(){
            return this.distance;
        }
        public void setEstimate_time(String estimate_time){
            this.estimate_time = estimate_time;
        }
        public String getEstimate_time(){
            return this.estimate_time;
        }

    }

}
