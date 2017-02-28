package lv.kasparsj.android.dwob;

public class DailyWordsWidget extends BaseDailyWordsWidget {

    @Override
    protected float getDefaultTextSize(int numLines) {
        switch (numLines) {
            case 1:
            case 2:
            case 3:
            case 4:
                return 13;
            case 5:
                return 10;
        }
        return 8;
    }
}
