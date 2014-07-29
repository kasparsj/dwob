package lv.kasparsj.android.feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class BaseFeedParser implements FeedParser {

    // names of the XML tags
    static final String RSS = "rss";
    static final String CHANNEL = "channel";
    static final String PUB_DATE = "pubDate";
    static final String DESCRIPTION = "description";
    static final String LINK = "link";
    static final String TITLE = "title";
    static final String ITEM = "item";
    static final int TIMEOUT = 10000;
    
    final URL feedUrl;

    protected BaseFeedParser(String feedUrl){
        try {
            this.feedUrl = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected InputStream getInputStream() {
        try {
        	URLConnection connection = feedUrl.openConnection();
        	connection.setConnectTimeout(TIMEOUT);
        	connection.setReadTimeout(TIMEOUT);
            return connection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
