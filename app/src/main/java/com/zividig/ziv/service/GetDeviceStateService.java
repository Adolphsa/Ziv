package com.zividig.ziv.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class GetDeviceStateService extends Service {

    private SharedPreferences spf;

    public GetDeviceStateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        spf = getSharedPreferences("config",MODE_PRIVATE);
    }

    private void getDeviceState(){


    }
}
