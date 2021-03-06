package lv.kasparsj.android.dwob.app;

import android.app.AlertDialog;
import android.content.Context;
import android.view.MotionEvent;

import lv.kasparsj.android.dwob.model.DailyWords;

public class HelpDialog extends AlertDialog {

    public HelpDialog(Context context) {
        super(context);
    }

    @Override
    public void dismiss() {
        DailyWords dailyWords = new DailyWords(getContext());
        if (dailyWords.showHelpOnStart()) {
            dailyWords.dismissHelpOnStart();
        }
        super.dismiss();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        dismiss();
        return super.dispatchTouchEvent(ev);
    }
}