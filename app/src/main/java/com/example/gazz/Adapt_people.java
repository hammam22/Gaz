package com.example.gazz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapt_people extends BaseAdapter {
    ArrayList<MyUserInfo> arrayList;
    Context context;

    public Adapt_people(Context context,ArrayList<MyUserInfo> arrayList) {
        this.context=context;
        this.arrayList = arrayList;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        Myholder myholder = null;
        if (row == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = li.inflate(R.layout.req_item, viewGroup, false);
            myholder = new Myholder(row);
            //myholder.btlvmaindelete=row.findViewById(R.id.btlvmaindelete);
            row.setTag(myholder);
        } else {
            myholder = (Myholder) row.getTag();
        }
        myholder.tv_req_item_name.setText(arrayList.get(i).name);
        myholder.tv_req_item_phone.setText(arrayList.get(i).phone);
        myholder.bt_req_accept.setVisibility(View.INVISIBLE);
        myholder.bt_req_reject.setVisibility(View.INVISIBLE);

        return row;



    }

    private class Myholder {
        TextView tv_req_item_name, tv_req_item_phone;
        Button bt_req_accept,bt_req_reject;

        Myholder(View v) {
            tv_req_item_name = (TextView) v.findViewById(R.id.tv_req_item_name);
            tv_req_item_phone = (TextView) v.findViewById(R.id.tv_req_item_phone);

            bt_req_accept=v.findViewById(R.id.bt_req_accept);
            bt_req_reject=v.findViewById(R.id.bt_req_reject);
        }
    }
}
