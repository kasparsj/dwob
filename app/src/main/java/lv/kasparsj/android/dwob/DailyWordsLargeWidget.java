package lv.kasparsj.android.dwob;

public class DailyWordsLargeWidget extends DailyWordsWidget
{
    @Override
    protected float getDefaultTextSize(int numLines) {
        switch (numLines) {
            case 1:
            case 2:
            case 3:
            case 4:
                return 20;
            case 5:
                return 18;
            case 6:
                return 16;
            case 7:
                return 15;
        }
        return 14;
    }

    @Override
    protected int getMaxLines() {
        return 8;
    }
}
