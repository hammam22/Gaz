package com.example.gazz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Adapt_gaz extends BaseAdapter {

    ArrayList<MyGaz> arrayList;
    Context context;

    public Adapt_gaz(Context context,ArrayList<MyGaz> arrayList) {
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
            row = li.inflate(R.layout.gaz_item, viewGroup, false);
            myholder = new Myholder(row);
            //myholder.btlvmaindelete=row.findViewById(R.id.btlvmaindelete);
            row.setTag(myholder);
        } else {
            myholder = (Myholder) row.getTag();
        }
        myholder.tv_gaz_name_toShow.setText(arrayList.get(i).name);
        try {
            FileInputStream inputStream = new FileInputStream(new File(arrayList.get(i).path));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            myholder.iv_gaz_show.setImageBitmap(bitmap);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return row;
    }

    private class Myholder {
        TextView tv_gaz_name_toShow;
        ImageView iv_gaz_show;


        Myholder(View v) {
            tv_gaz_name_toShow = (TextView) v.findViewById(R.id.tv_gaz_name_toShow);
            iv_gaz_show=(ImageView)v.findViewById(R.id.iv_gaz_show);


        }
    }

}
