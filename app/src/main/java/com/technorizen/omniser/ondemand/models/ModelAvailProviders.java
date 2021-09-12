package com.technorizen.omniser.ondemand.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelAvailProviders implements Serializable {

    private Result result;
    private String message;
    private String status;

    public void setResult(Result result){
        this.result = result;
    }
    public Result getResult(){
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

        private String request_id;

        private String user_id;

        private String provider_id;

        private String status;

        private String date_time;

        private Service_details service_details;

        private ArrayList<Provider_details> provider_details;

        private String delivery_address;

        private String service_description;

        private String service_price;

        private String order_id;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setRequest_id(String request_id){
            this.request_id = request_id;
        }
        public String getRequest_id(){
            return this.request_id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setProvider_id(String provider_id){
            this.provider_id = provider_id;
        }
        public String getProvider_id(){
            return this.provider_id;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setService_details(Service_details service_details){
            this.service_details = service_details;
        }
        public Service_details getService_details(){
            return this.service_details;
        }
        public void setProvider_details(ArrayList<Provider_details> provider_details){
            this.provider_details = provider_details;
        }
        public ArrayList<Provider_details> getProvider_details(){
            return this.provider_details;
        }
        public void setDelivery_address(String delivery_address){
            this.delivery_address = delivery_address;
        }
        public String getDelivery_address(){
            return this.delivery_address;
        }
        public void setService_description(String service_description){
            this.service_description = service_description;
        }
        public String getService_description(){
            return this.service_description;
        }
        public void setService_price(String service_price){
            this.service_price = service_price;
        }
        public String getService_price(){
            return this.service_price;
        }
        public void setOrder_id(String order_id){
            this.order_id = order_id;
        }
        public String getOrder_id(){
            return this.order_id;
        }

        public class Provider_details
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
        }

        public class Service_details
        {
            private String id;

            private String category_id;

            private String sub_cat_name;

            private String image;

            private String status;

            private String date_time;

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
            public void setCategory_id(String category_id){
                this.category_id = category_id;
            }
            public String getCategory_id(){
                return this.category_id;
            }
            public void setSub_cat_name(String sub_cat_name){
                this.sub_cat_name = sub_cat_name;
            }
            public String getSub_cat_name(){
                return this.sub_cat_name;
            }
            public void setImage(String image){
                this.image = image;
            }
            public String getImage(){
                return this.image;
            }
            public void setStatus(String status){
                this.status = status;
            }
            public String getStatus(){
                return this.status;
            }
            public void setDate_time(String date_time){
                this.date_time = date_time;
            }
            public String getDate_time(){
                return this.date_time;
            }
        }


    }


}
