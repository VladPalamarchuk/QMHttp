package qm.vp.kiev.qmhttplib;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class QMRequest implements Runnable {

    private Builder builder;
    private DefaultHttpClient httpClient;

    public void setQmListener(QMListener qmListener) {
        this.qmListener = qmListener;
    }

    private QMListener qmListener;

    @Override
    public void run() {
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                10000);

        httpClient = new DefaultHttpClient(httpParams);
        JSONObject respJSON = null;

        switch (builder.type) {
            case QMType.GET:
                respJSON = json(get());
                break;
            case QMType.POST:
                respJSON = json(post());
                break;
        }

        if (respJSON != null) {
            try {
                qmListener.QMComplete(respJSON);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(QMRequest.class.toString(), e.toString());
                qmListener.QMError(QMErrors.INVALID_JSON);
            }
        }
    }

    private JSONObject json(String data) {
        if (data == null) {
            qmListener.QMError(QMErrors.NULLABLE_RESPONSE);
            return null;
        }
        try {
            return new JSONObject(data);
        } catch (JSONException e) {

            qmListener.QMError(QMErrors.PARSE_ERROR);

            e.printStackTrace();
            Log.e(QMRequest.class.toString(), e.toString());
            return null;
        }
    }

    private String get() {
        try {
            return toString(httpClient.execute(new HttpGet(builder.url)).getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(QMRequest.class.toString(), e.toString());

            qmListener.QMError(QMErrors.UNKNOWN);
            return null;
        }
    }

    public static String toString(InputStream stream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }

    private String post() {
        HttpPost post = new HttpPost();
        post.setEntity(multipart(builder.fileBody, builder.textBody));
        try {
            return toString(httpClient.execute(post).getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(QMRequest.class.toString(), e.toString());

            qmListener.QMError(QMErrors.UNKNOWN);
        }
        return null;
    }

    public static HttpEntity multipart(Map<String, File> fileBody, Map<String, String> textBody) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        for (String key : fileBody.keySet()) {
            entityBuilder.addPart(key, value(fileBody.get(key)));
        }
        for (String key : textBody.keySet()) {
            entityBuilder.addPart(key, value(textBody.get(key)));
        }
        return entityBuilder.build();
    }

    private static ContentBody value(File file) {
        return new FileBody(file);
    }

    private static ContentBody value(String text) {
        try {
            return new StringBody(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static class Builder {

        private String url;
        private Map<String, File> fileBody;
        private Map<String, String> textBody;

        private int type;

        public Builder() {
            fileBody = new HashMap<>();
            textBody = new HashMap<>();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder add(String key, String value) {
            textBody.put(key, value);
            return this;
        }

        public Builder add(String key, File file) {
            fileBody.put(key, file);
            return this;
        }

        public Builder type(int type) {
            if (type != QMType.GET && type != QMType.POST) {
                throw new RuntimeException("QMRequest undefined query type");
            }
            this.type = type;
            return this;
        }

        public QMRequest build() {
            QMRequest qmRequest = new QMRequest();
            qmRequest.builder = this;
            return qmRequest;
        }
    }
}
