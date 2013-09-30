package org.semtex;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ZEUS on 30.08.13.
 */
public class HTMLRequestTask extends AsyncTask<String, String, String> {

    private ResultListener reciever;

    public HTMLRequestTask(ResultListener reciever) {
        this.reciever = reciever;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000); // 5s max for connection
        HttpConnectionParams.setSoTimeout(httpParameters, 6000); // 6s max to get data

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
            reciever.recieve(result);
        } else {
            reciever.recieveFailed("");
        }
    }
}