package com.technorizen.omniser.models;

import java.io.Serializable;

public class ModelLogin implements Serializable {

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

        private String name;

        private String cat_id;

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
        public void setCat_id(String cat_id){
            this.cat_id = cat_id;
        }
        public String getCat_id(){
            return this.cat_id;
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
    }


}
