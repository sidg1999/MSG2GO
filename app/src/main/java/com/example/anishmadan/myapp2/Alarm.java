package com.example.anishmadan.myapp2;

/**
 * Created by Anish Madan on 8/27/2017.
 */

public class Alarm {
     long timeTillRing;
     String number;
     String msg;
    public Alarm(String num,String msg,long time){
        this.timeTillRing=time;
        this.number=num;
        this.msg=msg;
    }


}
