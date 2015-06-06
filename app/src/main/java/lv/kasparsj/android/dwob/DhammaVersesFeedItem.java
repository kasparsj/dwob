package lv.kasparsj.android.dwob;

import lv.kasparsj.android.feed.FeedItem;

public class DhammaVersesFeedItem extends FeedItem {

    @Override
    public DhammaVersesFeedItem copy() {
        DhammaVersesFeedItem copy = copy(DhammaVersesFeedItem.class);
        return copy;
    }
}
