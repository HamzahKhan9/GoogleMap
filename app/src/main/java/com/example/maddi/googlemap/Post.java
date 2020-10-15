package com.example.maddi.googlemap;

import java.util.ArrayList;

public class Post {

   public String error,status;
   public ArrayList<LocationData> locationData;

   public String getError() {
      return error;
   }

   public void setError(String error) {
      this.error = error;
   }
   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

}
