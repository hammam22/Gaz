package com.example.gazz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Adapt_SelSub extends BaseAdapter {

    ArrayList<MySub> arrayList;
    Context context;

    public Adapt_SelSub(Context context,ArrayList<MySub> arrayList) {
        this.context=context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

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
        myholder.tv_req_item_name.setVisibility(View.VISIBLE);
        myholder.tv_req_item_name.setText(arrayList.get(position).cli);
        myholder.tv_req_item_phone.setText(arrayList.get(position).phone);

        myholder.bt_req_accept.setText("اتصل");
        myholder.bt_req_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+arrayList.get(position).phone));
                context.startActivity(intent);
            }
        });

        if (arrayList.get(position).done.equals("0")){
            myholder.bt_req_reject.setText("استلم");
        } else if (arrayList.get(position).done.equals("1")){
            myholder.bt_req_reject.setText("إلغاء");
        }
        myholder.bt_req_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.get(position).done.equals("0")){
                    ((act_selSubsManager) context).istalam(position);
                }else {
                    ((act_selSubsManager) context).maIstalam(position);
                }
            }
        });

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
