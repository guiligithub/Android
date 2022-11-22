package com.iskyun.im.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GsonUtils {

   public static final Gson gson = new GsonBuilder().serializeNulls().create();

   public static String toJson(Object obj){
      return gson.toJson(obj);
   }

   public static <T> T fromJson(String json,TypeToken<T> typeToken){
      return gson.fromJson(json, typeToken.getType());
   }

   public static <T> T fromJson(String json, Class<T> classOfT){
      return gson.fromJson(json, classOfT);
   }
}
