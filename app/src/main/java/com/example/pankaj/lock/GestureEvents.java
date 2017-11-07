package com.example.pankaj.lock;

import android.view.MotionEvent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pankaj on 1/11/17.
 */

public class GestureEvents implements Serializable
{

    public  ArrayList<motion> list;
     void GestureEvent(motion event)
     {
         list= new ArrayList<motion>() ;
     }

    public void add(motion e)
    {
        list.add(e);

    }
     public ArrayList<motion> getEventList()
     {
         return list;
     }

    public float getStartx()
    {
        return list.get(0).getx();
    }
    public float getStarty()
    {
        return list.get(0).gety();
    }

    public float getVelocityx()
    {
        float sum=0;
        if(list.size()>3)
        for(int i =0;i < 3; i++)
        {
            sum=sum+list.get(i).vx;
        }
        else
            return list.get(0).vx;
        return sum/3;
    }
    public float getVelocityy()
    {
        float sum=0;
        if(list.size()>3)
            for(int i =0;i < 3; i++)
            {
                sum=sum+list.get(i).vy;
            }
        else
            return list.get(0).vy;
        return sum/3;
    }

}
