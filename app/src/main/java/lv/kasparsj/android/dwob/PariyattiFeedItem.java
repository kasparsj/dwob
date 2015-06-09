package lv.kasparsj.android.dwob;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lv.kasparsj.android.feed.FeedItem;

public class PariyattiFeedItem extends FeedItem {

    @Override
    public PariyattiFeedItem copy() {
        PariyattiFeedItem copy = copy(PariyattiFeedItem.class);
        return copy;
    }

    public String getDescription() {
        return stripFooter(super.getDescription());
    }

    private String stripFooter(String description) {
        Pattern regex = Pattern.compile("Pariyatti is a non-profit organization supported by purchases and contributions(.+)$", Pattern.DOTALL);
        Matcher matcher = regex.matcher(description);
        return matcher.replaceAll("");
    }
}
