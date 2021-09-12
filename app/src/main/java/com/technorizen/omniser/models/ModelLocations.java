package com.technorizen.omniser.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelLocations implements Serializable {

    private Home_location home_location;

    private Work_location work_location;

    private ArrayList<Recent_location> recent_location;

    private String message;

    private String status;

    public void setHome_location(Home_location home_location){
        this.home_location = home_location;
    }
    public Home_location getHome_location(){
        return this.home_location;
    }
    public void setWork_location(Work_location work_location){
        this.work_location = work_location;
    }
    public Work_location getWork_location(){
        return this.work_location;
    }
    public void setRecent_location(ArrayList<Recent_location> recent_location){
        this.recent_location = recent_location;
    }
    public ArrayList<Recent_location> getRecent_location(){
        return this.recent_location;
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

    public class Home_location implements Serializable
    {
        private String id;

        private String user_id;

        private String address;

        private String lat;

        private String lon;

        private String type;

        private String date_time;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
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
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }

    public class Work_location implements Serializable
    {
        private String id;

        private String user_id;

        private String address;

        private String lat;

        private String lon;

        private String type;

        private String date_time;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
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
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }

    public class Recent_location implements Serializable
    {
        private String id;

        private String user_id;

        private String address;

        private String lat;

        private String lon;

        private String type;

        private String date_time;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
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
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }

}
