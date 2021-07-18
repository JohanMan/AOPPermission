package com.johan.aop.permisssion;

import android.util.Log;

public class DefaultRefuse implements PermissionRefuse {

    @Override
    public void onRefuse() {
        Log.e(getClass().getName(), "Request Permission Refuse !!!");
    }

}
