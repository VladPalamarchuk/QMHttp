package qm.vp.kiev.qmhttplib;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;


public class QMActivity extends Activity implements QMListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        QMApplication.setCurrent(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        QMApplication.clearCurrent(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        QMApplication.setCurrent(this);
        super.onResume();
    }


    @Override
    public void QMComplete(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public void QMError(Request request, int code) {
        Toast.makeText(this, R.string.check_network_connection, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void networkError() {

    }
}
