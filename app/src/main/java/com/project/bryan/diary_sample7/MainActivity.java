package com.project.bryan.diary_sample7;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.project.bryan.diary_sample7.Diary.Diary;
import com.project.bryan.diary_sample7.Diary.DiaryDBHelper;
import com.project.bryan.diary_sample7.Diary.DiaryEditActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CALENDAR = 1;
    static final int REQUEST_DIARY_EDIT = 2;
    static final int NUM_ITEMS = 3;
    FragmentAdapter fragmentAdapter;
    ViewPager viewPager;

    Toolbar toolbar;
    TextView tv_year, tv_month, tv_day;
    Button calendarBtn;

    static int today_year = CalendarDay.today().getYear();
    static int today_month = CalendarDay.today().getMonth() + 1;
    static int today_day = CalendarDay.today().getDay();

    public static int selected_year = today_year;
    public static int selected_month = today_month;
    public static int selected_day = today_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_year = (TextView) findViewById(R.id.tv_year);
        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_day = (TextView) findViewById(R.id.tv_day);

        tv_year.setText(Integer.toString(selected_year));
        tv_month.setText(Integer.toString(selected_month));
        tv_day.setText(Integer.toString(selected_day));

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        calendarBtn = (Button) findViewById(R.id.btn_viewCalendar);
        calendarBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivityForResult(intent, REQUEST_CALENDAR);
            }
        });

    }
    //-------------------------End of MainActivity's onCreate




    //-----------------------Start of FragmentAdapter
    public static class FragmentAdapter extends FragmentStatePagerAdapter {
        DiaryFragment diaryFragment;
        ScheduleFragment scheduleFragment;
        CheckListFragment checkListFragment;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);

            diaryFragment = DiaryFragment.newInstance();
            scheduleFragment = ScheduleFragment.newInstance();
            checkListFragment = CheckListFragment.newInstance();
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return diaryFragment;
                case 1:
                    return scheduleFragment;
                case 2:
                    return checkListFragment;
                default:
                        return null;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Diary";
                case 1:
                    return "Schedule";
                case 2:
                    return "CheckList";

                //default 안잡아주면 마지막 프래그먼트가 어댑터 연결이 안되는것 같다.
                default:
                    return null;
            }
        }


    }
    //-----------------------End of FragmentAdapter




    //-----------------------Start of DiaryFragment

    public static class DiaryFragment extends Fragment {
        FloatingActionButton fab;
        String date;
        SQLiteDatabase db;
        DiaryDBHelper dbHelper;
        TextView mContents;

        public  DiaryFragment(){
        }
        static DiaryFragment newInstance() {
            return new DiaryFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_diary, container, false);

            mContents = (TextView) v.findViewById(R.id.textView_of_DiaryFrag);
            mContents.setMovementMethod(new ScrollingMovementMethod());
            dbHelper = new DiaryDBHelper(getActivity());

            final Intent intent_DiaryEdit = new Intent(getActivity(), DiaryEditActivity.class);
            fab = (FloatingActionButton) v.findViewById(R.id.fab);
            fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent_DiaryEdit.putExtra("date", date);
                    startActivityForResult(intent_DiaryEdit,REQUEST_DIARY_EDIT);
                }
            });

            refresh();
            return v;
        }

        // Diary refresh
        public void refresh() {
            date = Integer.toString(selected_year) + Integer.toString(selected_month) + Integer.toString(selected_day);

            String sql = "select * from DIARY_TB where DATE = " + date + ";"  ;
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if(cursor.getCount() == 0) {
                mContents.setText("");
                mContents.setHint("저장된 내용이 없습니다.");
                cursor.close();
            }
            else{
                cursor.moveToFirst();
                String getContents = cursor.getString(1);
                mContents.setText(getContents);
                cursor.close();
            }

            db.close();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            //다이어리 편집에게 온 편지
            if(requestCode == REQUEST_DIARY_EDIT && resultCode != RESULT_CANCELED ){
                Toast.makeText(getActivity(),"다이어리 프래그먼트 새로고침",Toast.LENGTH_SHORT).show();refresh();
            }

        }
    }
    //-----------------------End of DiaryFragment





    //-----------------------Start of ScheduleFragment
    public static class ScheduleFragment extends Fragment {
        TextView mContents;
        public ScheduleFragment(){
        }
        static ScheduleFragment newInstance() {
            return new ScheduleFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @NonNull
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_schedule, container, false);
            mContents = (TextView) v.findViewById(R.id.textView_of_ScheduleFrag);

            refresh();
            return v;
        }

        public void refresh() {
            mContents.setText(Integer.toString(selected_year) + " / " + Integer.toString(selected_month) + " / " + Integer.toString(selected_day));
        }
    }
    //-----------------------End of ScheduleFragment





    //-----------------------Start of CheckListFragment
    public static class CheckListFragment extends Fragment {
        TextView mContents;
        public CheckListFragment(){
        }
        static CheckListFragment newInstance() {
            return new CheckListFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @NonNull
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_checklist, container, false);
            mContents = (TextView)v.findViewById(R.id.textView_of_CheckListFrag);

            refresh();
            return v;
        }

        public void refresh() {
            String new_contents = Integer.toString(selected_year) + " / " + Integer.toString(selected_month) + " / " + Integer.toString(selected_day);
            mContents.setText(new_contents);
        }

    }
    //-----------------------End of CheckListFragment





    //----------Start of onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        //달력에게서 온 편지
        if (requestCode == REQUEST_CALENDAR && resultCode != RESULT_CANCELED && data != null){

                    selected_year = data.getIntExtra("year", 18);
                    selected_month = data.getIntExtra("month", 18);
                    selected_day = data.getIntExtra("day", 18);

                    tv_year.setText(Integer.toString(selected_year) + "년");
                    tv_month.setText(Integer.toString(selected_month) + "월");
                    tv_day.setText(Integer.toString(selected_day) + "일");

                    Log.i("onActivityResult",Integer.toString(selected_year));
                    Log.i("onActivityResult",Integer.toString(selected_month));
                    Log.i("onActivityResult",Integer.toString(selected_day));


                    fragmentAdapter.getItem(0);

                    ((DiaryFragment) fragmentAdapter.getItem(0)).refresh();
                    ((ScheduleFragment) fragmentAdapter.getItem(1)).refresh();
                    ((CheckListFragment) fragmentAdapter.getItem(2)).refresh();
        }


    }//----------End of onActivityResult





}//End of MainActivity
