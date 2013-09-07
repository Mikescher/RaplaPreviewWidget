package org.semtex;

import android.content.Context;
import android.widget.RemoteViews;

/**
 * Created by ZEUS on 01.09.13.
 */
public class RaplaConnectorResultHandler implements RaplaResultListener {
    @Override
    public void recieve(RaplaEntry entry, RaplaConnector connector) {
        RemoteViews rv = new RemoteViews(connector.getContext().getPackageName(), R.layout.main);
        Context c = connector.getContext();

        RaplaPreviewWidgetProvider.setEntry(c, entry);
        RaplaPreviewWidgetProvider.updateEntryDisplay(c, rv);
    }

    @Override
    public void recieveFailed(String errormsg, RaplaConnector connector) {
        RemoteViews rv = new RemoteViews(connector.getContext().getPackageName(), R.layout.main);
        Context c = connector.getContext();

        RaplaPreviewWidgetProvider.setEntry(c, null);
        RaplaPreviewWidgetProvider.updateUI(c, rv, "<ERROR>", errormsg, "???", "???", "???", true);
    }
}
