package org.semtex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by ZEUS on 01.09.13.
 */
public class RaplaPreviewWidgetIntentReciever extends BroadcastReceiver {

    private static int clickCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(RaplaUtilies.ACTION_REFRESH)){
            updateWidgetRefreshListener(context);
        } else if(intent.getAction().equals(RaplaUtilies.ACTION_LS_CLICK_1)){
            updateWidgetLeftClickListener(context);
        } else if(intent.getAction().equals(RaplaUtilies.ACTION_LS_CLICK_2)){
            updateWidgetLeftClickListener(context);
        }
    }

    private void updateWidgetRefreshListener(Context c) {
        RemoteViews rv = new RemoteViews(c.getPackageName(), R.layout.main);

        if (RaplaPreviewWidgetProvider.canReload) {
            RaplaPreviewWidgetProvider.startUpdate(c);
        } else {
            RaplaPreviewWidgetProvider.updateUI(c, rv, "Abort ...", "----", "----", "----", "----", true);
        }

        //REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
        rv.setOnClickPendingIntent(R.id.widgetLeftLayout, RaplaPreviewWidgetProvider.buildIntent(c, RaplaUtilies.ACTION_REFRESH));
        RaplaPreviewWidgetProvider.pushWidgetUpdate(c.getApplicationContext(), rv);
    }

    private void updateWidgetLeftClickListener(Context c) {
        RemoteViews rv = new RemoteViews(c.getPackageName(), R.layout.main);

        RaplaPreviewWidgetProvider.switchSubtextDisplay(c, rv);

        //REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
        rv.setOnClickPendingIntent(R.id.textViewMain, RaplaPreviewWidgetProvider.buildIntent(c, RaplaUtilies.ACTION_LS_CLICK_1));
        rv.setOnClickPendingIntent(R.id.textViewSubText, RaplaPreviewWidgetProvider.buildIntent(c, RaplaUtilies.ACTION_LS_CLICK_2));

        RaplaPreviewWidgetProvider.pushWidgetUpdate(c.getApplicationContext(), rv);
    }
}