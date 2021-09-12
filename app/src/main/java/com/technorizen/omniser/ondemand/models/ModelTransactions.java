package com.technorizen.omniser.ondemand.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelTransactions implements Serializable {

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

        private String sender_id;

        private String recevier_id;

        private String amount;

        private String date_time;

        private Sender_details sender_details;

        private Receiver_details receiver_details;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setSender_id(String sender_id){
            this.sender_id = sender_id;
        }
        public String getSender_id(){
            return this.sender_id;
        }
        public void setRecevier_id(String recevier_id){
            this.recevier_id = recevier_id;
        }
        public String getRecevier_id(){
            return this.recevier_id;
        }
        public void setAmount(String amount){
            this.amount = amount;
        }
        public String getAmount(){
            return this.amount;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setSender_details(Sender_details sender_details){
            this.sender_details = sender_details;
        }
        public Sender_details getSender_details(){
            return this.sender_details;
        }
        public void setReceiver_details(Receiver_details receiver_details){
            this.receiver_details = receiver_details;
        }
        public Receiver_details getReceiver_details(){
            return this.receiver_details;
        }

        public class Receiver_details
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
        }

        public class Sender_details
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
        }


    }


}
