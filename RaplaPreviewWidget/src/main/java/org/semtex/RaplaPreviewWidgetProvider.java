package org.semtex;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class RaplaPreviewWidgetProvider extends AppWidgetProvider { //TODO Log

    public static RaplaEntry currentEntry = null;
    public static int currentSubTextMode = 0;

    @Override
    public void onUpdate(Context c, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews rv = new RemoteViews(c.getPackageName(), R.layout.main);

        rv.setOnClickPendingIntent(R.id.buttonReload, buildIntent(c, RaplaUtilies.ACTION_REFRESH));
        rv.setOnClickPendingIntent(R.id.textViewMain, RaplaPreviewWidgetProvider.buildIntent(c, RaplaUtilies.ACTION_LS_CLICK_1));
        rv.setOnClickPendingIntent(R.id.textViewSubText, RaplaPreviewWidgetProvider.buildIntent(c, RaplaUtilies.ACTION_LS_CLICK_2));

        pushWidgetUpdate(c, rv);

        startUpdate(c);
    }

    public static PendingIntent buildIntent(Context context, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, RaplaPreviewWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    public static void startUpdate(Context c) {
        RaplaConnector conn = new RaplaConnector(new RaplaConnectorResultHandler(), c);

        RemoteViews rv = new RemoteViews(c.getPackageName(), R.layout.main);

        updateUI(c, rv, "Updating ...", "", "", "", "", false);

        conn.update();
    }

    public static void updateUI(Context context, RemoteViews rv, String main, String sub, String side1, String side2, String side3, boolean reloadVisible) {
        rv.setTextViewText(R.id.textViewMain, main);
        rv.setTextViewText(R.id.textViewSubText, sub);

        rv.setTextViewText(R.id.textViewWeekday, side1);
        rv.setTextViewText(R.id.textViewDate, side2);
        rv.setTextViewText(R.id.textViewTime, side3);

        rv.setViewVisibility(R.id.buttonReload, reloadVisible ? View.VISIBLE : View.INVISIBLE);

        pushWidgetUpdate(context, rv);
    }

    public static void updateEntryDisplay(Context c, RemoteViews rv) {
        if (currentEntry == null) {
            rv.setTextViewText(R.id.textViewMain, "<ERROR>");
            return;
        }

        String main = currentEntry.getTitle();
        String sub;
        switch(currentSubTextMode) {
            case 0:
                sub = currentEntry.getStringRoomList();
                break;
            case 1:
                sub = currentEntry.getStringTeacherList();
                break;
            case 2:
                sub = currentEntry.getStringCourseList();
                break;
            default:
                sub = "<?>";
                break;
        }

        String side1 = currentEntry.getTimeInfoString();
        String side2 = currentEntry.getFormattedDate();
        String side3 = currentEntry.getFormattedTimeIntervall();

        RaplaPreviewWidgetProvider.updateUI(c, rv, main, sub, side1, side2, side3, true);
    }

    public static void switchSubtextDisplay(Context c, RemoteViews rv) {
        currentSubTextMode = (currentSubTextMode + 1) % 3;
        updateEntryDisplay(c, rv);
    }
}
