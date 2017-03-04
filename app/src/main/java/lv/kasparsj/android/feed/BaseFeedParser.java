package lv.kasparsj.android.feed;

public abstract class BaseFeedParser implements FeedParser {

    // names of the XML tags
    static final String RSS = "rss";
    static final String CHANNEL = "channel";
    static final String PUB_DATE = "pubDate";
    static final String DESCRIPTION = "description";
    static final String LINK = "link";
    static final String TITLE = "title";
    static final String ITEM = "item";

}
