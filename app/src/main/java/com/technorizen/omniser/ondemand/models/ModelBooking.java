package com.technorizen.omniser.ondemand.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelBooking implements Serializable {

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

        private String spanish_status;

        private String date_time;

        private Service_details service_details;

        public String getSpanish_status() {
            return spanish_status;
        }

        public void setSpanish_status(String spanish_status) {
            this.spanish_status = spanish_status;
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
