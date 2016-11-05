package com.hahaxueche.ui.view.findCoach;

import android.text.Spanned;

import com.hahaxueche.model.base.Field;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/17.
 */

public interface FieldFilterView extends HHBaseView {
    void initMap(ArrayList<Field> fields);

    void setHints(Spanned hints);

    void setSelectFieldText(String text);
}
