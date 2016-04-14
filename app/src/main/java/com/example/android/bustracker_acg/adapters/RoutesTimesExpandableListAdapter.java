package com.example.android.bustracker_acg.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.android.bustracker_acg.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RoutesTimesExpandableListAdapter extends BaseExpandableListAdapter {


    // Context
    private Context _context;
    // ArrayList with the headers-route names of the expandable list
    private ArrayList<String> _listDataHeader;
    // Data in format of route name-group header,
    // time & route stop names item
    private HashMap<String, ArrayList<String>> _listDataChildStations;
    private HashMap<String, ArrayList<String>> _listDataChildTimes;

    public RoutesTimesExpandableListAdapter(Context context,
                                            ArrayList<String> listDataHeader,
                                            HashMap<String, ArrayList<String>> listChildDataStations,
                                            HashMap<String, ArrayList<String>> listChildDataTimes) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChildStations = listChildDataStations;
        this._listDataChildTimes = listChildDataTimes;
    }

    // 1st modification for the time text view
    @Override
    public List<String> getChild(int groupPosition, int childPosition) {
        List<String> children = new ArrayList<>();
        children.add(this._listDataChildStations.get(this._listDataHeader.get(groupPosition))
                .get(childPosition));
        children.add(this._listDataChildTimes.get(this._listDataHeader.get(groupPosition))
                .get(childPosition));

        return children;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 2nd modification for the time text view
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        List<String> childTexts = getChild(groupPosition, childPosition);
        final String childTextStation = childTexts.get(0);
        final String childTextTime = childTexts.get(1);


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.routes_times_list_item, null);
        }

        TextView timeTextView = (TextView) convertView
                .findViewById(R.id.list_item_time);

        timeTextView.setText(childTextTime);

        TextView stationTextView = (TextView) convertView
                .findViewById(R.id.list_item_station);

        stationTextView.setText(childTextStation);


        return convertView;
    }

    // 3rd modification for the time text view
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChildStations.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.routes_times_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.list_header_route);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
