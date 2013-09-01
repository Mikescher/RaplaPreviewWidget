package org.semtex;

/**
 * Created by ZEUS on 30.08.13.
 */
public interface ResultListener {
    public void recieve(String html);
    public void recieveFailed(String errormsg);
}
