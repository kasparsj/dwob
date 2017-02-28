package lv.kasparsj.android.dwob.feed;

import java.util.List;

import lv.kasparsj.android.feed.SaxFeedParser;

public class PaliWordFeedParser extends SaxFeedParser<PariyattiFeedItem> {

    public PaliWordFeedParser(String feedUrl) {
        super(feedUrl);
    }

    @Override
    public List<PariyattiFeedItem> parse() {
        return parse(PariyattiFeedItem.class);
    }
}
