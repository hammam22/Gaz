package com.example.gazz;



public class MyActivity {
    int price,amount,taken;
    String name,dis,timeplace,nei,notes,path,sel;
    boolean isSubbed =false;

    MyActivity(String sel,String name,String dis,String timeplace,String notes,String nei,int price,int amount,int taken,String path){
        this.sel=sel;
        this.name=name;
        this.dis=dis;
        this.timeplace=timeplace;
        this.notes=notes;
        this.nei=nei;
        this.price=price;
        this.amount=amount;
        this.taken=taken;
        this.path=path;
        this.isSubbed=false;
    }

    MyActivity(String sel,String name,String dis,String timeplace,String notes
            ,String nei,int price,int amount,int taken,String path,boolean isSubbed){
        this.sel=sel;
        this.name=name;
        this.dis=dis;
        this.timeplace=timeplace;
        this.notes=notes;
        this.nei=nei;
        this.price=price;
        this.amount=amount;
        this.taken=taken;
        this.path=path;
        this.isSubbed=isSubbed;
    }
}
