package com.technorizen.omniser.fooddelivery.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelCartItems implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;
    private String store_name;
    private String image;
    private String store_address;
    private String store_lendmark_address;
    private String all_total_price;
    private String delivery_charges;
    private String total_pay;

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStore_address() {
        return store_address;
    }

    public void setStore_address(String store_address) {
        this.store_address = store_address;
    }

    public String getStore_lendmark_address() {
        return store_lendmark_address;
    }

    public void setStore_lendmark_address(String store_lendmark_address) {
        this.store_lendmark_address = store_lendmark_address;
    }

    public String getAll_total_price() {
        return all_total_price;
    }

    public void setAll_total_price(String all_total_price) {
        this.all_total_price = all_total_price;
    }

    public String getDelivery_charges() {
        return delivery_charges;
    }

    public void setDelivery_charges(String delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    public String getTotal_pay() {
        return total_pay;
    }

    public void setTotal_pay(String total_pay) {
        this.total_pay = total_pay;
    }

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return this.status;
    }

    public class Result
    {
        private String id;

        private String user_id;

        private String item_id;

        private String res_id;

        private String price;

        private String quantity;

        private String tempQuantity = "0";

        private String delivery_charges;

        private String items_total_price;

        private String status;

        private String date_time;

        private Item_details item_details;

        private ArrayList<Topping> topingss;

        private ArrayList<ExtraOptions> extra_options;

        public ArrayList<ExtraOptions> getExtra_options() {
            return extra_options;
        }

        public void setExtra_options(ArrayList<ExtraOptions> extra_options) {
            this.extra_options = extra_options;
        }

        public ArrayList<Topping> getTopingss() {
            return topingss;
        }

        public void setTopingss(ArrayList<Topping> topingss) {
            this.topingss = topingss;
        }

        public String getTempQuantity() {
            return tempQuantity;
        }

        public void setTempQuantity(String tempQuantity) {
            this.tempQuantity = tempQuantity;
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
        public void setItem_id(String item_id){
            this.item_id = item_id;
        }
        public String getItem_id(){
            return this.item_id;
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

        public class Topping
        {
            private String id;

            private String item_id;

            private String store_id;

            private String value;

            private String price;

            private String status;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setId(String id) {
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
        }

        public class ExtraOptions
        {
            private String id;

            private String item_id;

            private String store_id;

            private String option_name;

            private String option_price;

            private String date_time;

            private String status;

            public void setStatus(String status) {
                this.status = status;
            }

            public String getOption_name() {
                return option_name;
            }

            public void setOption_name(String option_name) {
                this.option_name = option_name;
            }

            public String getOption_price() {
                return option_price;
            }

            public void setOption_price(String option_price) {
                this.option_price = option_price;
            }

            public String getDate_time() {
                return date_time;
            }

            public void setDate_time(String date_time) {
                this.date_time = date_time;
            }

            public String getStatus() {
                return status;
            }

            public void setId(String id) {
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

        }

        public class Item_details
        {
            private String id;

            private String store_id;

            private String item_name;

            private String item_price;

            private String item_category;

            private String description;

            private String short_description_es;

            private String item_name_es;

            private String image;

            private String date_time;

            public String getShort_description_es() {
                return short_description_es;
            }

            public void setShort_description_es(String short_description_es) {
                this.short_description_es = short_description_es;
            }

            public String getItem_name_es() {
                return item_name_es;
            }

            public void setItem_name_es(String item_name_es) {
                this.item_name_es = item_name_es;
            }

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
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
            public void setDate_time(String date_time){
                this.date_time = date_time;
            }
            public String getDate_time(){
                return this.date_time;
            }
        }

    }

}
