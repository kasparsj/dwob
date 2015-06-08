package lv.kasparsj.android.dwob;

import java.util.List;

import lv.kasparsj.android.feed.SaxFeedParser;

public class DhammaVersesFeedParser extends SaxFeedParser<PariyattiFeedItem> {

    public DhammaVersesFeedParser(String url) {
        super(url);
    }

    @Override
    public List<PariyattiFeedItem> parse() {
        return parse(PariyattiFeedItem.class);
    }
}
