package com.example.weatherapp;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//继承 AsyncTask 返回 String
public class OpenWeatherMapAPI extends AsyncTask<Void, Void, String> {


    String lng = "113.27336"; //广州的经度
    String lat = "23.106391"; //广州的纬度

    String api_key_one_day= "670364ac5fb9365bcfcb4a1ea26e4b3f";
    String api_key_16_day = "1a60c49a1ee041562874026d15cd7c2f";

    String units = "metric";  //数据单位 米
    String days = "5";//天数
    String language = "zh_cn"; //语言 中文

    //根据 API 规则构造 http 网址  详见 https://openweathermap.org/forecast16
    String httpLink = "http://api.openweathermap.org/data/2.5/forecast/daily?" +
            "lat=" + lat + "&lon=" + lng + "&units=" + units + "&cnt="+ days +"&lang="+ language +"&appid=" + api_key_16_day;

    //http 连接对象
    HttpURLConnection connection;

    //通过这个接口与 Activity 交互
    OnRequestCompleted requestCompleted;

    //类的构造器
    public OpenWeatherMapAPI(){}
    //带有 OnRequestCompleted 具体实现的构造器
    public OpenWeatherMapAPI(OnRequestCompleted rc){
        requestCompleted = rc;
    }

    //返回 Json 格式的天气数据
    @Override
    protected String doInBackground(Void... v) {

        String result;

        try {
            //创建 http 连接
            URL url = new URL(httpLink); //通过 String 创建一个网址 url
            connection = (HttpURLConnection) url.openConnection(); //通过 url 得到一个新的 http 连接
            connection.setConnectTimeout(50000); //设置超时等待时间 单位ms
            connection.connect(); //执行连接

            //获取数据流
            InputStream input = connection.getInputStream();
            //将数据流转换成字符串
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int count = input.read();
            while(count > -1) {
                output.write((byte) count);
                count = input.read();
            }
            //指定 UTF-8 编码
            result =  output.toString("UTF-8");
            //关闭数据流
            input.close();
            output.close();
            //关闭 http 连接
            connection.disconnect();

        } catch (Exception e) {

            //如果出错了
            //把异常打印出来
            e.printStackTrace();

            //关闭 http 连接
            if (connection != null) {
                connection.disconnect();
            }
            //返回 error
            result = "error";
        }
        return result;
    }


    //任务执行完毕后调用
    @Override
    protected void onPostExecute(String result) {
        requestCompleted.onGetData(result);
    }

}