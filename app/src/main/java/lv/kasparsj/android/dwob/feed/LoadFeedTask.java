package lv.kasparsj.android.dwob.feed;

import android.os.AsyncTask;

import net.hockeyapp.android.ExceptionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import lv.kasparsj.android.dwob.model.FeedModel;
import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.feed.SaxFeedParser;

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
            return false;
        }
        try {
            feedParser.setDateFormat(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", new Locale(model.getLanguage())));
            List<? extends FeedItem> feedItems = feedParser.parse(inputStream);
            model.update(feedItems);
            return true;
        } catch (RuntimeException e) {
            ExceptionHandler.saveException(e, null, null);
            return false;
        }
    }

    private InputStream getInputStream() throws IOException {
        URL url = new URL(feedUrl);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        return connection.getInputStream();
    }
}