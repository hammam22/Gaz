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
import java.util.ArrayList;

public class Adapt_activities extends BaseAdapter {

    ArrayList<MyActivity> arrayList;
    ArrayList<MySub> sublist;
    Context context;
    String cat;
    Myholder myholder = null;
    boolean isIstalam=false;
    ArrayList<Integer> sol =new ArrayList<Integer>();
    int index=-1,equalfound=0;
    boolean check = false;
    ArrayList<Boolean> isDont = new ArrayList<Boolean>();

    Adapt_activities(Context context,ArrayList<MyActivity> arrayList,String cat){
        this.arrayList=arrayList;
        this.context=context;
        this.cat=cat;
    }

    Adapt_activities(Context context,ArrayList<MyActivity> arrayList,ArrayList<MySub> sublist,String cat){
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


            Bitmap bitmap = BitmapFactory.decodeFile(arrayList.get(i).path);
            myholder.iv_gzaItem.setImageBitmap(bitmap);

            myholder.tv_gazNameItem.setText(arrayList.get(i).name);


        if (cat.equals("sel")){
            myholder.tv_gazAmountItem.setText(String.valueOf(arrayList.get(i).amount));

            if (arrayList.get(i).amount>arrayList.get(i).taken){
                myholder.tv_availableItem.setText("متوفر");
                myholder.tv_availableItem.setTextColor(context.getResources().getColor(R.color.teal_700));
            } else {
                myholder.tv_availableItem.setText("إكتمل العدد");
                myholder.tv_availableItem.setTextColor(context.getResources().getColor(R.color.orange700));

            }
            myholder.bt_gazGetItem.setVisibility(View.INVISIBLE);

        } else if (cat.equals("cli")){




            if (arrayList.get(i).isSubbed){
                isIstalam =false;
                if (sublist!=null){
                    for (int kk=0; kk<sublist.size(); kk++){
                        if (sublist.get(kk).name.equals(arrayList.get(i).name)){
                            if (sublist.get(kk).done.equals("1")){
                                isIstalam=true;
                            }
                        }
                    }
                }

                Log.d("TAG", i+" subbed , done = " +isIstalam);

                if (isIstalam){
                    myholder.tv_done.setVisibility(View.VISIBLE);
                    myholder.bt_gazGetItem.setVisibility(View.INVISIBLE);
                    isDont.add(false);
                } else {myholder.tv_availableItem.setVisibility(View.VISIBLE);
                    myholder.tv_done.setVisibility(View.INVISIBLE);
                    myholder.tv_availableItem.setText("مسجّل");
                    myholder.bt_gazGetItem.setText("إلغاء");
                    isDont.add(false);
                }
            } else {
                Log.d("TAG", i+" not subbed");
                if (arrayList.get(i).amount==arrayList.get(i).taken){
                    myholder.tv_done.setVisibility(View.VISIBLE);
                    myholder.tv_done.setText("نفدت الكميّة");
                    myholder.bt_gazGetItem.setVisibility(View.INVISIBLE);
                    isDont.add(true);
                } else {
                    myholder.tv_availableItem.setVisibility(View.INVISIBLE);
                    isDont.add(false);
                }
            }



            myholder.tv_shohna.setText("المتبقي");
            myholder.tv_gazAmountItem.setText(String.valueOf(arrayList.get(i).amount-arrayList.get(i).taken));
            myholder.bt_gazGetItem.setVisibility(View.VISIBLE);









            myholder.bt_gazGetItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isDont.get(i)){} else {
                        if (arrayList.get(i).isSubbed){
                            ((act_activity_manager) context).unsubNow(i, arrayList.get(i).name, arrayList.get(i).sel);
                            Log.d("TAG", "tounsub");
                        } else {
                            ((act_activity_manager) context).subNow(i, arrayList.get(i).name, arrayList.get(i).sel);
                            Log.d("TAG", "toSub");
                        }
                    }
                        /*if (!isItDone){
                            if (!arrayList.get(i).isSubbed) {
                                if (arrayList.get(i).taken == arrayList.get(i).amount) {
                                } else {
                                    //make text : hajz
                                    ((act_activity_manager) context).subNow(i, arrayList.get(i).name, arrayList.get(i).sel);
                                    Log.d("TAG", "toSub");
                                }
                            } else {
                                ((act_activity_manager) context).unsubNow(i, arrayList.get(i).name, arrayList.get(i).sel);
                                Log.d("TAG", "tounsub");
                            }
                        }*/


                }
            });

        }

        myholder.act_item_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!arrayList.get(i).isSubbed||!isIstalam){
                    String done = "";
                    int remain = arrayList.get(i).amount-arrayList.get(i).taken;
                    if (isIstalam){
                        done = "1";
                    }else if (!arrayList.get(i).isSubbed){
                        done = null;
                    } else {
                        done = "0";
                    }
                    ((act_activity_manager) context).goInDeatails(i,done,remain);
                }

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
