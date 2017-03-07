package lv.kasparsj.android.dwob.feed;

import android.os.AsyncTask;

import net.hockeyapp.android.ExceptionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import lv.kasparsj.android.dwob.model.FeedModel;
import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.feed.SaxFeedParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoadFeedTask extends AsyncTask<String, Void, Boolean>
{
    static final int TIMEOUT = 10000;

    protected String feedUrl;
    protected FeedModel model;
    protected SaxFeedParser feedParser;

    public LoadFeedTask(String feedUrl, FeedModel model, SaxFeedParser feedParser) {
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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(feedUrl)
                .addHeader("Content-Type", "application/rss+xml")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().byteStream();
    }
}