package qm.vp.kiev.qmhttplib;

import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

public interface QMListener {

    public void QMComplete(JSONObject jsonObject) throws JSONException;

    public void QMError(int code);

    public void networkError();
}
