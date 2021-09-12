package com.technorizen.omniser.ondemand.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelInvoiceDetail implements Serializable {

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

    public class Result implements Serializable
    {
        private String id;

        private String order_id;

        private String user_id;

        private String request_provider;

        private String accepted_provider_id;

        private String service_id;

        private String delivery_address;

        private String lat;

        private String lon;

        private String service_time;

        private String description;

        private String status;

        private String date_time;

        private Service_details service_details;

        private String service_description;

        private String item_total_price;

        private String service_price;

        private String worked_time;

        private String worked_price;

        private String total_price;

        private ArrayList<Item_array> item_array;

        public String getItem_total_price() {
            return item_total_price;
        }

        public void setItem_total_price(String item_total_price) {
            this.item_total_price = item_total_price;
        }

        public ArrayList<Item_array> getItem_arrays() {
            return item_array;
        }

        public void setItem_arrays(ArrayList<Item_array> item_arrays) {
            this.item_array = item_arrays;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setOrder_id(String order_id){
            this.order_id = order_id;
        }
        public String getOrder_id(){
            return this.order_id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setRequest_provider(String request_provider){
            this.request_provider = request_provider;
        }
        public String getRequest_provider(){
            return this.request_provider;
        }
        public void setAccepted_provider_id(String accepted_provider_id){
            this.accepted_provider_id = accepted_provider_id;
        }
        public String getAccepted_provider_id(){
            return this.accepted_provider_id;
        }
        public void setService_id(String service_id){
            this.service_id = service_id;
        }
        public String getService_id(){
            return this.service_id;
        }
        public void setDelivery_address(String delivery_address){
            this.delivery_address = delivery_address;
        }
        public String getDelivery_address(){
            return this.delivery_address;
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
        public void setService_time(String service_time){
            this.service_time = service_time;
        }
        public String getService_time(){
            return this.service_time;
        }
        public void setDescription(String description){
            this.description = description;
        }
        public String getDescription(){
            return this.description;
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
        public void setWorked_time(String worked_time){
            this.worked_time = worked_time;
        }
        public String getWorked_time(){
            return this.worked_time;
        }
        public void setWorked_price(String worked_price){
            this.worked_price = worked_price;
        }
        public String getWorked_price(){
            return this.worked_price;
        }
        public void setTotal_price(String total_price){
            this.total_price = total_price;
        }
        public String getTotal_price(){
            return this.total_price;
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

        public class Item_array implements Serializable
        {
            private String id;

            private String request_id;

            private String provider_id;

            private String item_name;

            private String item_price;

            private String date_time;

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
            public void setProvider_id(String provider_id){
                this.provider_id = provider_id;
            }
            public String getProvider_id(){
                return this.provider_id;
            }
            public void setItem_name(String item_name){
                this.item_name = item_name;
            }
            public String getItem_name(){
                return this.item_name;
            }
            public void setItem_price(String item_price){
                this.item_price = item_price;
            }
            public String getItem_price(){
                return this.item_price;
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
