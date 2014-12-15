package qm.vp.kiev.qmhttplib;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

public class QMRequestMaker {

    protected Handler resultHandler;
    protected Handler progressHandler;

    public void run() {

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        if (!isNetworkConnected()) {
            resultHandler.sendEmptyMessage(QMErrors.NO_INTERNET);
            return;
        }
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                10000);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        HttpPost httpPost;
        HttpGet httpGet;

        HttpResponse response = null;

        try {

            if (queryType == QUERY_GET) {
                httpGet = new HttpGet(url);

                response = httpClient.execute(httpGet);

            } else if (queryType == QUERY_POST) {

                httpPost = new HttpPost(url);
                if (entity != null) {
                    httpPost.setEntity(entity);
                }
                response = httpClient.execute(httpPost);
            }

            serverResponse = response != null ?
                    EntityUtils.toString(response.getEntity()) : null;


            Log.e(TAG, "url -> " + url);
            Log.e(TAG, "serverResponse -> " + serverResponse);


            if (resultHandler != null) {
                resultHandler.sendEmptyMessage(QM_COMPLETE);
            }

        } catch (IOException e) {
            Log.e(TAG, e.toString());

            if (resultHandler != null) {
                resultHandler.sendEmptyMessage(QM_SERVER_ERROR);
            }
        }
    }

    private void post() {

    }

    private void get() {

    }

    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) QMApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
