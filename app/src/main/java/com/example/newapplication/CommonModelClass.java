package com.example.newapplication;

import android.app.Activity;

public class CommonModelClass
{
    public static CommonModelClass singletonObject;
    /** A private Constructor prevents any other class from instantiating. */

    private MainActivity baseActivity;

    public CommonModelClass()
    {
        //   Optional Code
    }
    public static synchronized CommonModelClass getSingletonObject()
    {
        if (singletonObject == null)
        {
            singletonObject = new CommonModelClass();
        }
        return singletonObject;
    }


    /**
     * used to clear CommonModelClass(SingletonClass) Memory
     */
    public void clear()
    {
        singletonObject = null;
    }


    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    //getters and setters starts from here.it is used to set and get a value

    public MainActivity getbaseActivity()
    {
        return baseActivity;
    }

    public void setbaseActivity(MainActivity baseActivity)
    {
        this.baseActivity = baseActivity;
    }

}
