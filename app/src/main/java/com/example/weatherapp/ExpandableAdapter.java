package com.example.weatherapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ExpandableAdapter extends BaseExpandableListAdapter {

    Typeface weatherFont;
    LayoutInflater mInflater;
    Context mContext;
    int mGroupLayout;
    int mChildLayout;
    List<String> mGroupArray;
    Map<String, ArrayList<List>> mChildMap;

    public ExpandableAdapter(Context context, int groupLayout,
                              int childLayout) {
        mContext = context;
        mGroupLayout = groupLayout;
        mChildLayout = childLayout;
        mGroupArray = new ArrayList<>();
        mChildMap = new HashMap<>();
        mInflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        weatherFont = Typeface.createFromAsset(mContext.getAssets(), "weather.ttf");
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(mGroupArray.size()>0){
            String groupName = mGroupArray.get(groupPosition);
            if(mChildMap.size()>0)
                return mChildMap.get(groupName).get(childPosition);
            else
                return null;
        }
        else
            return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View v;
        if ( convertView == null ) {
            v = mInflater.inflate(mGroupLayout, null);
        } else {
            v = convertView;
        }
        bindGroupView(v, mGroupArray.get(groupPosition));
        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View v;
        if ( convertView == null ) {
            v = mInflater.inflate(mChildLayout, null);
        } else {
            v = convertView;
        }
        if(mGroupArray.size()>0){
            String groupName = mGroupArray.get(groupPosition);
            bindChildView(v, mChildMap.get(groupName).get(childPosition));
        }
        return v;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(mGroupArray.size()>0){
            String groupName = mGroupArray.get(groupPosition);
            if(mChildMap.size()>0) {
                return mChildMap.get(groupName).size();
            }
            else
                return 0;
        }
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupArray.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mGroupArray.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    private void bindGroupView(View v, String title) {
        TextView titleTv = v.findViewById(R.id.group_title);
        titleTv.setText(title);
    }

    private void bindChildView(View v, List data) {
        TextView date = v.findViewById(R.id.child_date);
        TextView weather = v.findViewById(R.id.child_weather);
        TextView ico = v.findViewById(R.id.child_ico);
        ico.setTypeface(weatherFont);
        ico.setText(data.get(8).toString());
        date.setText(data.get(2).toString());
        weather.setText(data.get(3).toString() + " " + data.get(5).toString() + "~" + data.get(4).toString() + "ºC");
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addGroup(String title) {
        if(mChildMap.size()>0){
            if(mChildMap.containsKey(title))
                return;
        }
        mGroupArray.add(title);
        ArrayList<List> weathersList  = new ArrayList<List>();
        mChildMap.put(title, weathersList);
    }

    public void updateWeather(String groupName, ArrayList<List> newDataList)
    {
        if(mChildMap.size()>0) {
            ArrayList<List> weathersList = new ArrayList<List>();
            for(int i=0; i< newDataList.size(); i++){
                List onDay = newDataList.get(i);
                String city  = onDay.get(0).toString();
                String country  = onDay.get(1).toString();
                String date  = onDay.get(2).toString();
                String weather  = onDay.get(3).toString();
                String maxTemp  = onDay.get(4).toString();
                String minTemp  = onDay.get(5).toString();
                String humidity  = onDay.get(6).toString();
                String pressure  = onDay.get(7).toString();
                String icon  = onDay.get(8).toString();
                weathersList.add(i, Arrays.asList(city, country, date, weather, maxTemp, minTemp, humidity, pressure, icon));
            }
            mChildMap.put(groupName, weathersList);
            notifyDataSetChanged();
        }
    }

    public List<String> getAllGroup() {
        return mGroupArray;
    }

}



