package com.technorizen.omniser.taxi.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelCarType implements Serializable {

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

        private String type;

        private String charges;

        private String image;

        private String per_km;

        private String currency;

        private String number_of_seat;

        private String date_time;

        private String distance;

        private String estimated_charge;

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getEstimated_charge() {
            return estimated_charge;
        }

        public void setEstimated_charge(String estimated_charge) {
            this.estimated_charge = estimated_charge;
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
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setCharges(String charges){
            this.charges = charges;
        }
        public String getCharges(){
            return this.charges;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
        public void setPer_km(String per_km){
            this.per_km = per_km;
        }
        public String getPer_km(){
            return this.per_km;
        }
        public void setCurrency(String currency){
            this.currency = currency;
        }
        public String getCurrency(){
            return this.currency;
        }
        public void setNumber_of_seat(String number_of_seat){
            this.number_of_seat = number_of_seat;
        }
        public String getNumber_of_seat(){
            return this.number_of_seat;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }

}
