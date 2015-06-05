package lv.kasparsj.android.dwob;

import lv.kasparsj.android.feed.SaxFeedParser;

public class PaliWordFeedParser extends SaxFeedParser<PaliWordFeedItem> {

    public PaliWordFeedParser(String feedUrl) {
        super(feedUrl);
    }
}
