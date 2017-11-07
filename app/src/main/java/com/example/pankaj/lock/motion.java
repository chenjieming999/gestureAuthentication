package com.example.pankaj.lock;

import java.io.Serializable;

/**
 * Created by pankaj on 1/11/17.
 */

public class motion implements Serializable
{

    private static final long serialVersionUID = 5006436351296113861L;
    float x ,y,vx,vy;


    public float getx()
    {
        return x;
    }

    public float gety()
    {
        return y;
    }
}
