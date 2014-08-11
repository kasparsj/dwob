package lv.kasparsj.android.dwob;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

abstract public class AppFragment extends Fragment {

    private int layout_id;

    public AppFragment(int layout_id) {
        this.layout_id = layout_id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(layout_id, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    abstract public void updateView();

}
