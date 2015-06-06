package lv.kasparsj.android.dwob;

import lv.kasparsj.android.feed.FeedItem;

public class PaliWordFeedItem extends FeedItem {

    @Override
    public PaliWordFeedItem copy() {
        PaliWordFeedItem copy = copy(PaliWordFeedItem.class);
        return copy;
    }
}
