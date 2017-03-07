package lv.kasparsj.android.dwob.feed;

import android.content.Context;
import android.os.AsyncTask;

import net.hockeyapp.android.ExceptionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import lv.kasparsj.android.dwob.model.FeedModel;
import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.util.SSLUtils;

public class LoadFeedTask extends AsyncTask<String, Void, Boolean>
{
    protected Context context;
    protected String feedUrl;
    protected FeedModel model;
    protected SaxFeedParser feedParser;

    public LoadFeedTask(Context context, String feedUrl, FeedModel model, SaxFeedParser feedParser) {
        this.context = context;
        this.feedUrl = feedUrl;
        this.model = model;
        this.feedParser = feedParser;
    }

    @Override
    protected void onPreExecute() {
        model.setIsLoading(true);
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        model.setIsLoading(false, success);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        InputStream inputStream;
        try {
            inputStream = getInputStream();
        } catch (IOException|RuntimeException e) {
            ExceptionHandler.saveException(e, null, null);
            return false;
        }
        try {
            feedParser.setDateFormat(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", new Locale(model.getLanguage())));
            List<? extends FeedItem> feedItems = feedParser.parse(inputStream);
            model.update(feedItems);
            return true;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                return false;
            }
            ExceptionHandler.saveException(e, null, null);
            return false;
        }
    }

    private InputStream getInputStream() throws IOException {
        URL url = new URL(feedUrl);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);
        if (connection instanceof HttpsURLConnection) {
            try {
                ((HttpsURLConnection) connection).setHostnameVerifier(new SSLUtils.TrustAllHostnameVerifier());
                ((HttpsURLConnection) connection).setSSLSocketFactory(SSLUtils.getTrustAllSocketFactory());
            } catch (NoSuchAlgorithmException|KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }
        return connection.getInputStream();
    }
}