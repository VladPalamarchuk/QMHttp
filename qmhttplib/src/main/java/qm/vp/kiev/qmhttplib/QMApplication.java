package qm.vp.kiev.qmhttplib;

import android.app.Application;

import com.squareup.okhttp.Request;

import java.io.IOException;


public class QMApplication extends Application {

    private static QMApplication instance;

    private QMActivity qmActivity;

    public static QMApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static void setCurrent(QMActivity qmActivity) {
        instance.qmActivity = qmActivity;
    }

    public static void clearCurrent(QMActivity qmActivity) {
        if (instance.qmActivity.equals(qmActivity)) {
            instance.qmActivity = null;
        }
    }
}
