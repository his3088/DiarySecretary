package com.project.bryan.diary_sample7;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.project.bryan.diary_sample7.decorators.EventDecorator;
import com.project.bryan.diary_sample7.decorators.OneDayDecorator;
import com.project.bryan.diary_sample7.decorators.SaturdayDecorator;
import com.project.bryan.diary_sample7.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarActivity extends AppCompatActivity {

    final String tag = "CalendarActivity";
    OneDayDecorator oneDayDecorator = new OneDayDecorator();
    public int selected_year=1991, selected_month=5, selected_day=5;
    MaterialCalendarView materialCalendarView;
    Button btn_cancel, btn_select;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        materialCalendarView = findViewById(R.id.calendarView);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_select = (Button)findViewById(R.id.btn_select);

        //취소 버튼
        btn_cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //확인 버튼*************
        btn_select.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                //선택한 날짜를 MainActivity에게 intent로 넘긴다.
                Intent intent_SelectedDate = new Intent();
                intent_SelectedDate.putExtra("year", selected_year);
                intent_SelectedDate.putExtra("month", selected_month);
                intent_SelectedDate.putExtra("day", selected_day);
                setResult(RESULT_OK, intent_SelectedDate);

                Log.i("year",Integer.toString(selected_year));
                Log.i("month",Integer.toString(selected_month));
                Log.i("day", Integer.toString(selected_day));

                finish();
            }
        });





        //달력 설정
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(java.util.Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2010, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2040, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        //데코 추가
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        //이벤트 표시할 날짜들 --> 나중에 DB에서 하나씩 받아와야해.
        String[] result = {"2018,03,15","2018,04,15","2018,05,05","2018,05,16","2018,07,21"};

        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        //날짜 선택 이벤트 리스너
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                selected_year = date.getYear();
                selected_month = date.getMonth() + 1;
                selected_day = date.getDay();

                Log.i(tag, "Year test: " + selected_year + " | " + date.getYear());
                Log.i(tag, "Month test: " + selected_month + " | " + date.getMonth());
                Log.i(tag, "Day test: " + selected_day + " | " + date.getDay());

                String shot_Day = selected_year + "/" + selected_month + "/" + selected_day;

                Log.i("shot_Day test", shot_Day);
                //날짜 선택 유지 여부.
//                materialCalendarView.clearSelection();

            }
        });
    }//---------------------------------onCreate 끝






//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.btn_cancel) {
//            finish();
//        }
//        else if (v.getId() == R.id.btn_select) {
//            Intent intent = new Intent();
//            intent.putExtra("year", selected_year);
//            intent.putExtra("month", selected_month);
//            intent.putExtra("day", selected_day);
//            setResult(RESULT_OK, intent);
//            finish();
//        }
//    }






    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            java.util.Calendar calendar = java.util.Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정 날짜 달력에 점 표시*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로 짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.length; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year, month - 1, dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.CYAN, calendarDays, CalendarActivity.this));
        }
    }




}//----------End of CalendarActivity
