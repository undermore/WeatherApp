package com.example.weatherapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Util {

    //解析 Json 数据
    //Json 数据结构 详见 https://openweathermap.org/forecast16
    //输入 obj
    //输出 dateList
    public static ArrayList<List> jsonParser(JSONObject obj) {

        ArrayList<List> dateList = new ArrayList<>();

        try {
            //获得 city 对象中 name 字段的值
            String city = obj.getJSONObject("city").getString("name");
            //获得 city 对象中 country 字段的值
            String country = obj.getJSONObject("city").getString("country");
            //获得 list 对象中 数组
            JSONArray arrJson = obj.getJSONArray("list");

            //遍历数组
            for (int i=0; i < arrJson.length(); i++) {
                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd EEEE");
                String date = formatter.format(new Date(arrJson.getJSONObject(i).getLong("dt") * 1000));

                //得到 weather 数组
                JSONObject details= arrJson.getJSONObject(i).getJSONArray("weather").getJSONObject(0);
                String weather = details.getString("description");
                String icon =String.valueOf(details.getInt("id"));
                String humidity = arrJson.getJSONObject(i).getString("humidity"); //湿度
                String pressure = arrJson.getJSONObject(i).getString("pressure"); //气压

                //最高温度
                String maxTemp = String.valueOf(arrJson.getJSONObject(i).getJSONObject("temp").getInt("max"));
                //最低温度
                String minTemp = String.valueOf(arrJson.getJSONObject(i).getJSONObject("temp").getInt("min"));

                dateList.add(i, Arrays.asList(city, country, date, weather, maxTemp, minTemp, humidity, pressure, icon));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return dateList;
    }

}
