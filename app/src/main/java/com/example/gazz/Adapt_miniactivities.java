package com.example.gazz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.FileInputStream;
import java.util.ArrayList;

public class Adapt_miniactivities extends BaseAdapter {

    ArrayList<MyActivity> arrayList;
    ArrayList<MySub> sublist;
    Context context;
    String cat;
    Myholder myholder = null;
    boolean isItDone = false,changingSub=false,isSubbed=false;
    int sol=-1;
    int index=-1,equalfound=0;
    boolean check = false;
    Adapt_miniactivities(Context context,ArrayList<MyActivity> arrayList,String cat){
        this.arrayList=arrayList;
        this.context=context;
        this.cat=cat;
    }

    Adapt_miniactivities(Context context,ArrayList<MyActivity> arrayList,ArrayList<MySub> sublist,String cat){
        this.arrayList=arrayList;
        this.context=context;
        this.cat=cat;
        this.sublist=sublist;
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

        if (row == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = li.inflate(R.layout.activity_item, viewGroup, false);
            myholder = new Myholder(row);
            row.setTag(myholder);
        } else {
            myholder = (Myholder) row.getTag();
        }

        Log.d("TAG", "getView: cat="+cat);

        Bitmap bitmap = BitmapFactory.decodeFile(arrayList.get(i).path);
        myholder.iv_gzaItem.setImageBitmap(bitmap);

        myholder.tv_gazNameItem.setText(arrayList.get(i).name);

        if (cat.equals("cli")){


        

            myholder.tv_done.setVisibility(View.INVISIBLE);
            myholder.tv_availableItem.setText("مسجّل");
                    


            if (i==sol){
                myholder.tv_done.setVisibility(View.VISIBLE);
                myholder.bt_gazGetItem.setVisibility(View.INVISIBLE);
            }



            myholder.tv_shohna.setText("المتبقي");
            myholder.tv_gazAmountItem.setText(String.valueOf(arrayList.get(i).amount-arrayList.get(i).taken));
            


            myholder.bt_gazGetItem.setVisibility(View.INVISIBLE);

        }

        myholder.act_item_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((act_infos_for_cli) context).goInDeatails(i);

            }
        });

        return row;

    }

    /*public void toUnsub(int i) {
        myholder.tv_availableItem.setText("مسجّل");
        myholder.bt_gazGetItem.setText("إلغاء");
        changingSub=true;
        changingId=i;
        isSubbed=false;
        Log.d("TAG", "toUnsub: here");
    }*/

    /*public void toSub(int i) {
        myholder.tv_availableItem.setVisibility(View.INVISIBLE);
        myholder.bt_gazGetItem.setText("حجز");
        changingSub=true;
        changingId=i;
        isSubbed=true;
        Log.d("TAG", "toSub: here");

    }*/


    private class Myholder {
        TextView tv_gazNameItem,tv_gazAmountItem,tv_availableItem,tv_shohna,tv_done;
        ImageView iv_gzaItem;
        Button bt_gazGetItem;
        ConstraintLayout act_item_lay;

        Myholder(View v) {
            tv_gazNameItem = (TextView) v.findViewById(R.id.tv_gazNameItem);
            tv_gazAmountItem = (TextView)v.findViewById(R.id.tv_gazAmountItem);
            tv_availableItem = (TextView)v.findViewById(R.id.tv_availableItem);
            tv_shohna = (TextView)v.findViewById(R.id.tv_shohna);
            tv_done=(TextView)v.findViewById(R.id.tv_done);

            act_item_lay= (ConstraintLayout)v.findViewById(R.id.act_item_lay);

            iv_gzaItem=(ImageView)v.findViewById(R.id.iv_gzaItem);

            bt_gazGetItem = (Button)v.findViewById(R.id.bt_gazGetItem);
        }
    }

}
