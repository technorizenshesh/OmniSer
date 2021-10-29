package com.technorizen.omniser.fooddelivery.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelResTypeItems implements Serializable {

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

    public class Result implements Serializable
    {
        private String id;
        private String category_name;
        private String date_time;
        private String res_id;
        private ArrayList<Item_data> item_data;

        public String getRes_id() {
            return res_id;
        }

        public void setRes_id(String res_id) {
            this.res_id = res_id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return this.id;
        }
        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }
        public String getCategory_name() {
            return this.category_name;
        }
        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }
        public String getDate_time() {
            return this.date_time;
        }

        public void setItem_data(ArrayList<Item_data> item_data) {
            this.item_data = item_data;
        }

        public ArrayList<Item_data> getItem_data() {
            return this.item_data;
        }

        public class Item_data implements Serializable{

            private String id;
            private String store_id;
            private String item_name;
            private String item_price;
            private String item_category;
            private String short_description;
            private String short_description_es;
            private String item_name_es;
            private String tag;
            private String description;
            private String image;
            private String item_quantity;
            private String date_time;
            private ArrayList<Topping> topping;
            private ArrayList<ExtraOptions> extra_options;

            public ArrayList<ExtraOptions> getExtra_options() {
                return extra_options;
            }

            public void setExtra_options(ArrayList<ExtraOptions> extra_options) {
                this.extra_options = extra_options;
            }

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

            public String getShort_description() {
                return short_description;
            }

            public void setShort_description(String short_description) {
                this.short_description = short_description;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public ArrayList<Topping> getTopping() {
                return topping;
            }

            public void setTopping(ArrayList<Topping> topping) {
                this.topping = topping;
            }

            public String getItem_quantity() {
                return item_quantity;
            }
            public void setItem_quantity(String item_quantity) {
                this.item_quantity = item_quantity;
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

            public class Topping implements Serializable
            {
                private String id;

                private String item_id;

                private String store_id;

                private String value;

                private String price;

                private String status;

                private boolean isChecked;

                public boolean isChecked() {
                    return isChecked;
                }

                public void setChecked(boolean checked) {
                    isChecked = checked;
                }

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

            public class ExtraOptions implements Serializable {

                private String id;

                private String item_id;

                private String store_id;

                private String value;

                private String option_name;

                private String option_price;

                private String date_time;

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
            }

        }

    }


}
