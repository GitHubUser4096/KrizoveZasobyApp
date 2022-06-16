package com.entscz.krizovezasoby;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddItemDialog extends Dialog {
    public AddItemDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AddItemDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected AddItemDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context){
        setContentView(R.layout.add_item_chooser);
    }

}
