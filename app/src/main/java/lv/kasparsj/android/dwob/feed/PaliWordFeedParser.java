package lv.kasparsj.android.dwob.feed;

import java.io.InputStream;
import java.util.List;

import lv.kasparsj.android.feed.SaxFeedParser;

public class PaliWordFeedParser extends SaxFeedParser<PariyattiFeedItem> {

    @Override
    public List<PariyattiFeedItem> parse(InputStream inputStream) {
        return parse(inputStream, PariyattiFeedItem.class);
    }
}
