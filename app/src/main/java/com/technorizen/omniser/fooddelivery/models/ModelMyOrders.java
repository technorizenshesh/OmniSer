package com.technorizen.omniser.fooddelivery.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelMyOrders implements Serializable {

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

    public class Result implements Serializable {

        private String id;

        private String order_id;

        private String user_id;

        private String driver_id;

        private String payment_id;

        private String payment_type;

        private String total_amount;

        private String delivery_time;

        private String delivery_address;

        private String delivery_lat;

        private String delivery_lon;

        private String cart_item;

        private String wallet;

        private String store_id;

        private String delivery_date;

        private String store_name;

        private String order_place_time;

        private String ready_time;

        private String div_accept_time;

        private String div_pick_time;

        private String delivered_time;

        private String store_image;

        private String feedback;

        private String status;

        private String date_time;

        private String store_address;

        private String land_mark_add;

        private DriverDetails driver_details;

        private ArrayList<Order_history> order_history;

        public ArrayList<Order_history> getOrder_history() {
            return order_history;
        }

        public void setOrder_history(ArrayList<Order_history> order_history) {
            this.order_history = order_history;
        }

        public String getStore_address() {
            return store_address;
        }

        public void setStore_address(String store_address) {
            this.store_address = store_address;
        }

        public String getLand_mark_add() {
            return land_mark_add;
        }

        public void setLand_mark_add(String land_mark_add) {
            this.land_mark_add = land_mark_add;
        }

        public String getOrder_place_time() {
            return order_place_time;
        }

        public void setOrder_place_time(String order_place_time) {
            this.order_place_time = order_place_time;
        }

        public String getReady_time() {
            return ready_time;
        }

        public void setReady_time(String ready_time) {
            this.ready_time = ready_time;
        }

        public String getDiv_accept_time() {
            return div_accept_time;
        }

        public void setDiv_accept_time(String div_accept_time) {
            this.div_accept_time = div_accept_time;
        }

        public String getDiv_pick_time() {
            return div_pick_time;
        }

        public void setDiv_pick_time(String div_pick_time) {
            this.div_pick_time = div_pick_time;
        }

        public String getDelivered_time() {
            return delivered_time;
        }

        public void setDelivered_time(String delivered_time) {
            this.delivered_time = delivered_time;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public DriverDetails getDriver_details() {
            return driver_details;
        }

        public void setDriver_details(DriverDetails driver_details) {
            this.driver_details = driver_details;
        }

        public String getStore_name() {
            return store_name;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }

        public String getStore_image() {
            return store_image;
        }

        public void setStore_image(String store_image) {
            this.store_image = store_image;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
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
        public void setPayment_id(String payment_id){
            this.payment_id = payment_id;
        }
        public String getPayment_id(){
            return this.payment_id;
        }
        public void setPayment_type(String payment_type){
            this.payment_type = payment_type;
        }
        public String getPayment_type(){
            return this.payment_type;
        }
        public void setTotal_amount(String total_amount){
            this.total_amount = total_amount;
        }
        public String getTotal_amount(){
            return this.total_amount;
        }
        public void setDelivery_time(String delivery_time){
            this.delivery_time = delivery_time;
        }
        public String getDelivery_time(){
            return this.delivery_time;
        }
        public void setDelivery_address(String delivery_address){
            this.delivery_address = delivery_address;
        }
        public String getDelivery_address(){
            return this.delivery_address;
        }
        public void setDelivery_lat(String delivery_lat){
            this.delivery_lat = delivery_lat;
        }
        public String getDelivery_lat(){
            return this.delivery_lat;
        }
        public void setDelivery_lon(String delivery_lon){
            this.delivery_lon = delivery_lon;
        }
        public String getDelivery_lon(){
            return this.delivery_lon;
        }
        public void setCart_item(String cart_item){
            this.cart_item = cart_item;
        }
        public String getCart_item(){
            return this.cart_item;
        }
        public void setWallet(String wallet){
            this.wallet = wallet;
        }
        public String getWallet(){
            return this.wallet;
        }
        public void setStore_id(String store_id){
            this.store_id = store_id;
        }
        public String getStore_id(){
            return this.store_id;
        }
        public void setDelivery_date(String delivery_date){
            this.delivery_date = delivery_date;
        }
        public String getDelivery_date(){
            return this.delivery_date;
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

        public class DriverDetails implements Serializable {

            private String driver_name;
            private String driver_lat;
            private String driver_lon;
            private String driver_mobile;
            private String driver_image;

            public String getDriver_name() {
                return driver_name;
            }

            public void setDriver_name(String driver_name) {
                this.driver_name = driver_name;
            }

            public String getDriver_lat() {
                return driver_lat;
            }

            public void setDriver_lat(String driver_lat) {
                this.driver_lat = driver_lat;
            }

            public String getDriver_lon() {
                return driver_lon;
            }

            public void setDriver_lon(String driver_lon) {
                this.driver_lon = driver_lon;
            }

            public String getDriver_mobile() {
                return driver_mobile;
            }

            public void setDriver_mobile(String driver_mobile) {
                this.driver_mobile = driver_mobile;
            }

            public String getDriver_image() {
                return driver_image;
            }

            public void setDriver_image(String driver_image) {
                this.driver_image = driver_image;
            }
        }

        public class Order_history implements Serializable {
            private String id;

            private String user_id;

            private String item_id;

            private String toping_id;

            private String res_id;

            private String price;

            private String quantity;

            private String delivery_charges;

            private String items_total_price;

            private String status;

            private String date_time;

            private Item_details item_details;

            private ArrayList<Topingss> topingss;

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
            public void setItem_id(String item_id){
                this.item_id = item_id;
            }
            public String getItem_id(){
                return this.item_id;
            }
            public void setToping_id(String toping_id){
                this.toping_id = toping_id;
            }
            public String getToping_id(){
                return this.toping_id;
            }
            public void setRes_id(String res_id){
                this.res_id = res_id;
            }
            public String getRes_id(){
                return this.res_id;
            }
            public void setPrice(String price){
                this.price = price;
            }
            public String getPrice(){
                return this.price;
            }
            public void setQuantity(String quantity){
                this.quantity = quantity;
            }
            public String getQuantity(){
                return this.quantity;
            }
            public void setDelivery_charges(String delivery_charges){
                this.delivery_charges = delivery_charges;
            }
            public String getDelivery_charges(){
                return this.delivery_charges;
            }
            public void setItems_total_price(String items_total_price){
                this.items_total_price = items_total_price;
            }
            public String getItems_total_price(){
                return this.items_total_price;
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
            public void setItem_details(Item_details item_details){
                this.item_details = item_details;
            }
            public Item_details getItem_details(){
                return this.item_details;
            }
            public void setTopingss(ArrayList<Topingss> topingss){
                this.topingss = topingss;
            }
            public ArrayList<Topingss> getTopingss(){
                return this.topingss;
            }

            public class Topingss implements Serializable
            {
                private String id;

                private String item_id;

                private String store_id;

                private String value;

                private String price;

                private String status;

                public void setId(String id){
                    this.id = id;
                }
                public String getId(){
                    return this.id;
                }
                public void setItem_id(String item_id){
                    this.item_id = item_id;
                }
                public String getItem_id(){
                    return this.item_id;
                }
                public void setStore_id(String store_id){
                    this.store_id = store_id;
                }
                public String getStore_id(){
                    return this.store_id;
                }
                public void setValue(String value){
                    this.value = value;
                }
                public String getValue(){
                    return this.value;
                }
                public void setPrice(String price){
                    this.price = price;
                }
                public String getPrice(){
                    return this.price;
                }
                public void setStatus(String status){
                    this.status = status;
                }
                public String getStatus(){
                    return this.status;
                }
            }

            public class Item_details implements Serializable
            {
                private String id;

                private String service_type;

                private String store_id;

                private String item_name;

                private String item_price;

                private String item_category;

                private String description;

                private String image;

                private String item_in_stoke_or_not;

                private String date_time;

                public void setId(String id){
                    this.id = id;
                }
                public String getId(){
                    return this.id;
                }
                public void setService_type(String service_type){
                    this.service_type = service_type;
                }
                public String getService_type(){
                    return this.service_type;
                }
                public void setStore_id(String store_id){
                    this.store_id = store_id;
                }
                public String getStore_id(){
                    return this.store_id;
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
                public void setItem_category(String item_category){
                    this.item_category = item_category;
                }
                public String getItem_category(){
                    return this.item_category;
                }
                public void setDescription(String description){
                    this.description = description;
                }
                public String getDescription(){
                    return this.description;
                }
                public void setImage(String image){
                    this.image = image;
                }
                public String getImage(){
                    return this.image;
                }
                public void setItem_in_stoke_or_not(String item_in_stoke_or_not){
                    this.item_in_stoke_or_not = item_in_stoke_or_not;
                }
                public String getItem_in_stoke_or_not(){
                    return this.item_in_stoke_or_not;
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

}
