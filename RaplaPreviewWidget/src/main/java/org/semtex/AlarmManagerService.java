package org.semtex;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ZEUS on 07.09.13.
 */
public class AlarmManagerService extends Service {

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        onUpdate();

        return super.onStartCommand(intent, flags, startId);
    }

    private void onUpdate()
    {
        System.out.println("AlarmManager START");
        RaplaPreviewWidgetProvider.startUpdate(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
