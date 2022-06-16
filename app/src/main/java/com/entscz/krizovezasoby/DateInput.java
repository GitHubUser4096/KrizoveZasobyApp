package com.entscz.krizovezasoby;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateInput extends FrameLayout {

    EditText dayInput;
    EditText monthInput;
    EditText yearInput;

    public DateInput(@NonNull Context context) {
        super(context);
        initView();
    }

    public DateInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DateInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        inflate(getContext(), R.layout.date_input, this);

        dayInput = findViewById(R.id.dateDay);
        monthInput = findViewById(R.id.dateMonth);
        yearInput = findViewById(R.id.dateYear);

        dayInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==2){
                    monthInput.requestFocus();
                    monthInput.selectAll();
                }
            }
        });

        monthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==2){
                    yearInput.requestFocus();
                    yearInput.selectAll();
                }
            }
        });

    }

    public void setValue(int day, int month, int year){

        dayInput.setText(Integer.toString(day));
        monthInput.setText(Integer.toString(month));
        yearInput.setText(Integer.toString(year));

    }

    public String getValue(){

        String day = dayInput.getText().toString();
        String month = monthInput.getText().toString();
        String year = yearInput.getText().toString();

        if(day.length()==0 && month.length()==0 && year.length()==0) return "";

        if(month.length()==0 || year.length()==0) throw new RuntimeException("Prosím zadejte alespoň měsíc a rok!");

        int iday = 0, imonth, iyear;

        try {
            if(day.length()>0) iday = Integer.parseInt(day);
            imonth = Integer.parseInt(month);
            iyear = Integer.parseInt(year);
        } catch(Exception e){
            throw new RuntimeException("Minimální trvanlivost: prosím zadejte číslo!");
        }

        if(iyear<2000 || iyear>=3000) throw new RuntimeException("Minimální trvanlivost: rok mimo rozsah!");
        if(imonth<1 || imonth>12) throw new RuntimeException("Minimální trvanlivost: měsíc mimo rozsah!");
        if(day.length()>0 && (iday<1 || iday>31)) throw new RuntimeException("Minimální trvanlivost: den mimo rozsah!");

        Date date;
        try {
            if (day.length() == 0) {
                date = new Date(iyear-1900, imonth-1+1, 0);
//                date = new SimpleDateFormat("yyyy-MM-dd").parse(iyear + "-" + (imonth+1) + "-0");
            } else {
                date = new Date(iyear-1900, imonth-1, iday);
//                date = new SimpleDateFormat("yyyy-MM-dd").parse(iyear + "-" + imonth + "-" + iday);
            }
        } catch(Exception e){
            throw new RuntimeException("Neočekávaná chyba!");
        }

//        Log.i("zasoby", new SimpleDateFormat("yyyy-MM-dd").format(date));

        if((date.getMonth()+1)!=imonth) throw new RuntimeException("Minimální trvanlivost: den mimo rozsah měsíce!");
//        throw new RuntimeException("test");

        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

}
