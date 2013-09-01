package org.semtex;

import android.content.Context;

/**
 * Created by ZEUS on 30.08.13.
 */
public interface RaplaResultListener {
    public void recieve(RaplaEntry entry, RaplaConnector connector);
    public void recieveFailed(String errormsg, RaplaConnector connector);
}
