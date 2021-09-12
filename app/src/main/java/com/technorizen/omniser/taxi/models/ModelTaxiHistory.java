package com.technorizen.omniser.taxi.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelTaxiHistory implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult() {
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

    public class Result implements Serializable{

        private String id;

        private String user_id;

        private String driver_id;

        private String picuplocation;

        private String dropofflocation;

        private String picuplat;

        private String pickuplon;

        private String droplat;

        private String droplon;

        private String booktype;

        private String driver_name;

        private String driver_image;

        private String picklaterdate;

        private String picklatertime;

        private String car_type_id;

        private String timezone;

        private String status;

        private String req_datetime;

        private String estimate_charge_amount;

        private String send_drivers;

        private String start_time;

        private String favorite_ride;

        private String user_rating_status;

        private String tip_amount;

        private String end_time;

        private String shareride_type;

        private String car_seats;

        private String passenger;

        private String booked_seats;

        private String route_img;

        private String accept_time;

        private String reason_id;

        private String payment_type;

        private String apply_code;

        private String card_id;

        private String arrived_time;

        private String finish_date;

        private String booking_status;

        private String driver_ids;

        public String getDriver_name() {
            return driver_name;
        }

        public void setDriver_name(String driver_name) {
            this.driver_name = driver_name;
        }

        public String getDriver_image() {
            return driver_image;
        }

        public void setDriver_image(String driver_image) {
            this.driver_image = driver_image;
        }

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
        public void setDriver_id(String driver_id){
            this.driver_id = driver_id;
        }
        public String getDriver_id(){
            return this.driver_id;
        }
        public void setPicuplocation(String picuplocation){
            this.picuplocation = picuplocation;
        }
        public String getPicuplocation(){
            return this.picuplocation;
        }
        public void setDropofflocation(String dropofflocation){
            this.dropofflocation = dropofflocation;
        }
        public String getDropofflocation(){
            return this.dropofflocation;
        }
        public void setPicuplat(String picuplat){
            this.picuplat = picuplat;
        }
        public String getPicuplat(){
            return this.picuplat;
        }
        public void setPickuplon(String pickuplon){
            this.pickuplon = pickuplon;
        }
        public String getPickuplon(){
            return this.pickuplon;
        }
        public void setDroplat(String droplat){
            this.droplat = droplat;
        }
        public String getDroplat(){
            return this.droplat;
        }
        public void setDroplon(String droplon){
            this.droplon = droplon;
        }
        public String getDroplon(){
            return this.droplon;
        }
        public void setBooktype(String booktype){
            this.booktype = booktype;
        }
        public String getBooktype(){
            return this.booktype;
        }
        public void setPicklaterdate(String picklaterdate){
            this.picklaterdate = picklaterdate;
        }
        public String getPicklaterdate(){
            return this.picklaterdate;
        }
        public void setPicklatertime(String picklatertime){
            this.picklatertime = picklatertime;
        }
        public String getPicklatertime(){
            return this.picklatertime;
        }
        public void setCar_type_id(String car_type_id){
            this.car_type_id = car_type_id;
        }
        public String getCar_type_id(){
            return this.car_type_id;
        }
        public void setTimezone(String timezone){
            this.timezone = timezone;
        }
        public String getTimezone(){
            return this.timezone;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setReq_datetime(String req_datetime){
            this.req_datetime = req_datetime;
        }
        public String getReq_datetime(){
            return this.req_datetime;
        }
        public void setEstimate_charge_amount(String estimate_charge_amount){
            this.estimate_charge_amount = estimate_charge_amount;
        }
        public String getEstimate_charge_amount(){
            return this.estimate_charge_amount;
        }
        public void setSend_drivers(String send_drivers){
            this.send_drivers = send_drivers;
        }
        public String getSend_drivers(){
            return this.send_drivers;
        }
        public void setStart_time(String start_time){
            this.start_time = start_time;
        }
        public String getStart_time(){
            return this.start_time;
        }
        public void setFavorite_ride(String favorite_ride){
            this.favorite_ride = favorite_ride;
        }
        public String getFavorite_ride(){
            return this.favorite_ride;
        }
        public void setUser_rating_status(String user_rating_status){
            this.user_rating_status = user_rating_status;
        }
        public String getUser_rating_status(){
            return this.user_rating_status;
        }
        public void setTip_amount(String tip_amount){
            this.tip_amount = tip_amount;
        }
        public String getTip_amount(){
            return this.tip_amount;
        }
        public void setEnd_time(String end_time){
            this.end_time = end_time;
        }
        public String getEnd_time(){
            return this.end_time;
        }
        public void setShareride_type(String shareride_type){
            this.shareride_type = shareride_type;
        }
        public String getShareride_type(){
            return this.shareride_type;
        }
        public void setCar_seats(String car_seats){
            this.car_seats = car_seats;
        }
        public String getCar_seats(){
            return this.car_seats;
        }
        public void setPassenger(String passenger){
            this.passenger = passenger;
        }
        public String getPassenger(){
            return this.passenger;
        }
        public void setBooked_seats(String booked_seats){
            this.booked_seats = booked_seats;
        }
        public String getBooked_seats(){
            return this.booked_seats;
        }
        public void setRoute_img(String route_img){
            this.route_img = route_img;
        }
        public String getRoute_img(){
            return this.route_img;
        }
        public void setAccept_time(String accept_time){
            this.accept_time = accept_time;
        }
        public String getAccept_time(){
            return this.accept_time;
        }
        public void setReason_id(String reason_id){
            this.reason_id = reason_id;
        }
        public String getReason_id(){
            return this.reason_id;
        }
        public void setPayment_type(String payment_type){
            this.payment_type = payment_type;
        }
        public String getPayment_type(){
            return this.payment_type;
        }
        public void setApply_code(String apply_code){
            this.apply_code = apply_code;
        }
        public String getApply_code(){
            return this.apply_code;
        }
        public void setCard_id(String card_id){
            this.card_id = card_id;
        }
        public String getCard_id(){
            return this.card_id;
        }
        public void setArrived_time(String arrived_time){
            this.arrived_time = arrived_time;
        }
        public String getArrived_time(){
            return this.arrived_time;
        }
        public void setFinish_date(String finish_date){
            this.finish_date = finish_date;
        }
        public String getFinish_date(){
            return this.finish_date;
        }
        public void setBooking_status(String booking_status){
            this.booking_status = booking_status;
        }
        public String getBooking_status(){
            return this.booking_status;
        }
        public void setDriver_ids(String driver_ids){
            this.driver_ids = driver_ids;
        }
        public String getDriver_ids(){
            return this.driver_ids;
        }
    }

}
