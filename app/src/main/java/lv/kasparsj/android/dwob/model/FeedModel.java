package lv.kasparsj.android.dwob.model;

import java.util.List;

import lv.kasparsj.android.feed.FeedItem;

public interface FeedModel
{
    public String getLanguage();
    public void setIsLoading(boolean isLoading);
    public void setIsLoading(boolean isLoading, boolean success);
    public void update(List<? extends FeedItem> feedItems);
}
