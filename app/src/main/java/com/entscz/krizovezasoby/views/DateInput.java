package com.entscz.krizovezasoby.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.entscz.krizovezasoby.R;
import com.entscz.krizovezasoby.util.ValueError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        inflate(getContext(), R.layout.view_date_input, this);

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

    public void setValue(String dateString){

        try {

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            int day = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);

            dayInput.setText(Integer.toString(day));
            monthInput.setText(Integer.toString(month));
            yearInput.setText(Integer.toString(year));

        } catch(ParseException e){
            throw new RuntimeException(e);
        }

    }

    public String getValue() throws ValueError {

        String day = dayInput.getText().toString();
        String month = monthInput.getText().toString();
        String year = yearInput.getText().toString();

        if(day.length()==0 && month.length()==0 && year.length()==0) return "";

        if(month.length()==0 || year.length()==0) throw new ValueError("Prosím zadejte alespoň měsíc a rok!");

        int iday = 0, imonth, iyear;

        try {
            if(day.length()>0) iday = Integer.parseInt(day);
            imonth = Integer.parseInt(month);
            iyear = Integer.parseInt(year);
        } catch(Exception e){
            throw new ValueError("Minimální trvanlivost: prosím zadejte číslo!");
        }

        if(iyear<2000 || iyear>=3000) throw new ValueError("Minimální trvanlivost: rok mimo rozsah!");
        if(imonth<1 || imonth>12) throw new ValueError("Minimální trvanlivost: měsíc mimo rozsah!");
        if(day.length()>0 && (iday<1 || iday>31)) throw new ValueError("Minimální trvanlivost: den mimo rozsah!");

        Date date;
        try {
            if (day.length() == 0) {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(iyear + "-" + (imonth+1) + "-0");
            } else {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(iyear + "-" + imonth + "-" + iday);
            }
        } catch(Exception e){
            throw new ValueError("Neočekávaná chyba!");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if((cal.get(Calendar.MONTH)+1)!=imonth) throw new ValueError("Minimální trvanlivost: den mimo rozsah měsíce!");

        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

}
