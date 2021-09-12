package com.technorizen.omniser.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelAllServies implements Serializable {

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

        private String category_name;

        private String discription;

        private String discription_spanish;

        private String image;

        public String getDiscription_spanish() {
            return discription_spanish;
        }

        public void setDiscription_spanish(String discription_spanish) {
            this.discription_spanish = discription_spanish;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setCategory_name(String category_name){
            this.category_name = category_name;
        }
        public String getCategory_name(){
            return this.category_name;
        }
        public void setDiscription(String discription){
            this.discription = discription;
        }
        public String getDiscription(){
            return this.discription;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
    }

}
