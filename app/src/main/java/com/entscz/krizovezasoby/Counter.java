package com.entscz.krizovezasoby;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Counter extends FrameLayout {

    private EditText countInput;
    private int top = 0;

    public Counter(@NonNull Context context) {
        super(context);
        initView();
    }

    public Counter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Counter(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setValue(int value){
        countInput.setText(Integer.toString(value));
    }

    public void setTopValue(int top){
        this.top = top;
    }

    private void initView(){
        inflate(getContext(), R.layout.counter, this);

        countInput = findViewById(R.id.countInput);

        Button decrementBtn = findViewById(R.id.decrementBtn);
        Button incrementBtn = findViewById(R.id.incrementBtn);

        decrementBtn.setOnClickListener(v -> {
            String strVal = countInput.getText().toString();
            int value;
            if(strVal.length()==0){
                value = 0;
            } else {
                value = Integer.parseInt(strVal);
            }
            value--;
            if(value<1) value = 1;
            countInput.setText(""+value);
        });

        decrementBtn.setOnLongClickListener(v -> {
            countInput.setText("1");
            return true;
        });

        incrementBtn.setOnClickListener(v -> {
            String strVal = countInput.getText().toString();
            int value;
            if(strVal.length()==0){
                value = 0;
            } else {
                value = Integer.parseInt(strVal);
            }
            value++;
            if(top>0 && value>top){
                value = top;
            }
            countInput.setText(""+value);
        });

        incrementBtn.setOnLongClickListener(v -> {
            if(top>0){
                countInput.setText(""+top);
                return true;
            }
            return false;
        });

    }

    public int getValue(){

        String strVal = countInput.getText().toString();
        int value;
        if(strVal.length()==0){
            value = 1;
        } else {
            value = Integer.parseInt(strVal);
        }

        return value;

    }

}
