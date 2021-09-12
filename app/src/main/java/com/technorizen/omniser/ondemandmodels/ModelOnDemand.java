package com.technorizen.omniser.ondemandmodels;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelOnDemand implements Serializable {

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
        private String category_id;
        private String sub_cat_name;
        private String image;
        private String status;
        private String date_time;
        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

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
