package lv.kasparsj.android.dwob;

import lv.kasparsj.android.feed.SaxFeedParser;

public class DhammaVersesFeedParser extends SaxFeedParser<DhammaVersesFeedItem> {

    public DhammaVersesFeedParser(String url) {
        super(url);
    }
}
