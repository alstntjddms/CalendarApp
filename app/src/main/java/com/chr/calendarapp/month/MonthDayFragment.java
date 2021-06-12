package com.chr.calendarapp.month;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.chr.calendarapp.MainActivity;
import com.chr.calendarapp.R;
import com.chr.calendarapp.database.ScheduleDatabaseManager;
import com.chr.calendarapp.database.ScheduleVO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class MonthDayFragment extends Fragment {
    public GridView grid_month, x;
    ArrayList calendarDay;
    ArrayList resultcalendarDay;

    Activity activity;
    int year, month, date, tmppos;


    FloatingActionButton fab_add;

    Calendar cal = Calendar.getInstance();

    ScheduleDatabaseManager scheduleDatabaseManager;

    ArrayList<String> items = new ArrayList<>();


    public MonthDayFragment(Activity activity, ArrayList calendarDay, int year, int month) {
        this.activity = activity;
        this.calendarDay = calendarDay;
        this.resultcalendarDay = calendarDay;
        this.year = year;
        this.month = month;

    }

    public MonthDayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cal.set(year, month - 1, 1);




        //6x7칸 맞추기
        while (true) {
            if (calendarDay.size() < 42) {
                calendarDay.add("");
            } else if (calendarDay.size() == 42) {
                break;
            }
        }
        Log.i("qwer1", calendarDay + "calandar");

        //calendarDay = new ArrayList();

        //6x7칸 맞추기

        for(int j = 0; j < 42; j++){
            // DB 에서 Title 불러오기
            selectSchedule(year, month, j);
        }



        View v = inflater.inflate(R.layout.fragment_month_day, container, false);
        grid_month = v.findViewById(R.id.grid_month_day);

        // 일정추가버튼
        fab_add = v.findViewById(R.id.fab_add);


        //세로모드
        if (MainActivity.chk == true) {
            ArrayAdapter<Integer> adapt_grid = new ArrayAdapter<Integer>(getActivity(), R.layout.month, calendarDay);
            grid_month.setAdapter(adapt_grid);
            Log.i("test ", "세로");
        }
        //가로모드
        else {
            ArrayAdapter<Integer> adapt_grid = new ArrayAdapter<Integer>(getActivity(), R.layout.month2, calendarDay);
            grid_month.setAdapter(adapt_grid);
            Log.i("test ", "가로");
        }

        int startDay = cal.get(Calendar.DAY_OF_WEEK);


        //날짜 선택시 Toast년월일 출력과 선택 색칠
        grid_month.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                int tmpDate = (position + 1 - startDay + 1);
                Toast.makeText(getContext(), year + "." + month + "." + tmpDate, Toast.LENGTH_SHORT).show();
                grid_month.getChildAt(position).setBackgroundColor(Color.CYAN);
                grid_month.getChildAt(tmppos).setBackgroundColor(Color.WHITE);
                tmppos = position;

                //선택한 날짜
                date = tmpDate;
                Log.i("qwerㅋ", tmpDate + " []");

//                if (calendarDay.get(position).toString().length() > 3) {
//                    // db select 하기
//                    ScheduleVO vo = selectSchedule(year, month, date);
//                    Log.i("qwerㅋ", date + "불러옴");
//
//                    // vo를 가지고 상세일정 창으로 이동하기
//                    Intent i = new Intent(activity, AddRegisterScheduleActivity.class);
//                    i.putExtra("year", year);
//                    i.putExtra("month", month);
//                    i.putExtra("date", date);
//                    i.putExtra("time", 7);
//                    i.putExtra("schedule", (Serializable) vo);
//                    startActivityForResult(i, 1000);
//                }

                String[] columns = new String[]{"title", "time_start", "time_end", "place", "latitude", "longitude", "memo"};
                // where
                String selection = "year=" + year + " and month=" + month + " and date=" + date;

                Cursor cursor = scheduleDatabaseManager.query(columns, selection, null, null, null, null);

                items.clear();
                while (cursor.moveToNext()) {
                    items.add(cursor.getString(0));
                }


                ////
                if (calendarDay.get(position).toString().length() > 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("리스트 추가 예제");

                    CharSequence[] items1 = items.toArray(new String[items.size()]);

                    builder.setItems(items1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            //items = calendarDay;
                            Toast.makeText(activity.getApplicationContext(), items1[pos], Toast.LENGTH_LONG).show();

                            ScheduleVO vo = selectSchedule(year, month, date);
                            Log.i("qwerㅋ", date + "불러옴");

                              // vo를 가지고 상세일정 창으로 이동하기
                          Intent i = new Intent(activity, MonthRegisterScheduleActivity.class);
                          i.putExtra("year", year);
                          i.putExtra("month", month);
                          i.putExtra("date", date);
                          //i.putExtra("time", 7);
                          i.putExtra("schedule", (Serializable) vo);
                          startActivityForResult(i, 1000);
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
                ////


        });

        fab_add.setOnClickListener(click);

        return v;
    }

    //일정추가버튼누를시
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 일정 추가 버튼 클릭 시
                case R.id.fab_add:
                    Intent i = new Intent(activity, MonthRegisterScheduleActivity.class);
                    i.putExtra("year", year);
                    i.putExtra("month", month);
                    i.putExtra("date", date);
                    i.putExtra("time", cal.get(Calendar.HOUR));
                    i.putExtra("schedule", (Serializable) null);
                    startActivityForResult(i, 1000);
                    break;
            }
        }
    };


    // 인터페이스 추가 정의
    public interface OnSetYearMonthListener {
        void onSetYearMonth(int year, int month);
    }


    // select
    public ScheduleVO selectSchedule(int year, int month, int date) {
        scheduleDatabaseManager = ScheduleDatabaseManager.getInstance(activity);

        int date1 = date + cal.get(Calendar.DAY_OF_WEEK) -2;

        String day = "";

        try {
            // 날짜 gridView에서 인덱스에 맞는 날짜 가져오기
            day = (grid_month.getItemAtPosition(date)).toString();
            Log.i("qwer1", day);

        } catch (Exception e) {
            // 날짜가 존재하지 않으면 " "로 초기화
            day = "";
            e.printStackTrace();
            Log.i("qwer", date + "날짜 없음");

        } finally {
            // db query에 'date= ' 이렇게 빈 값으로 들어가는 것을 방지하기 위해 day를 0으로 세팅
            if (day.equals(" ") || day == " " || day == null || day.equals("")) {
                day = 0 + "";
                Log.i("qwer", date + "final");

            }
        }


        // SELECT title FROM schedule WHERE year=2021 and month=6 and date=3 and time_start like '06%'
        // select
        String[] columns = new String[]{"title", "time_start", "time_end", "place", "latitude", "longitude", "memo"};
        // where
        String selection = "year=" + year + " and month=" + month + " and date=" + date;
        Log.i("WeekDayFragment1", ""+date);
        Log.i("WeekDayFragment1", selection);

        Cursor cursor = scheduleDatabaseManager.query(columns, selection, null, null, null, null);
        Cursor cursorChk = scheduleDatabaseManager.query(columns, selection, null, null, null, null);

        // DB에서 가져 온 데이터들 저장하기
        ScheduleVO vo = new ScheduleVO();

        if (cursor != null) {

            // title이 존재하지 않으면 격자 grid에 빈 칸 넣기
            if (!cursorChk.moveToNext()) {
                calendarDay.set(date, calendarDay.get(date) );
                Log.i("qwer", date +"타이틀없음" + calendarDay.get(date));

            }
                while (cursor.moveToNext()) {
                    String subTitle;
                    // title이 존재하면 격자 grid에 title 넣기
                    if(calendarDay.get(date).equals("")){
                        Log.i("qwer", "빈값" +calendarDay.get(date) );

                    }else {
                        // title이 너무 길면 자름
                        if(cursor.getString(0).length() > 5){
                            subTitle = cursor.getString(0).substring(0,5) + "...";
                        }else{
                            subTitle = cursor.getString(0);
                        }
                        calendarDay.set(date1 , calendarDay.get(date1) + "\n" + subTitle); // title

                        //items.add(cursor.getString(0));

                        Log.i("qwer", "셋");
                        //Log.i("qwer", "tmp" + tmpdate);


                        vo.setTitle(cursor.getString(0)); // title
                        //vo.setTime_start(cursor.getString(1)); // time_start
                        //vo.setTime_end(cursor.getString(2)); // time_end
                        //vo.setPlace(cursor.getString(3)); // place
                        //vo.setLatitude(cursor.getDouble(4)); // latitude
                       // vo.setLongitude(cursor.getDouble(5)); // longitude
                        vo.setMemo(cursor.getString(6)); // memo
                    }
                }

        }


        cursor.close(); // cursor 닫기

        return vo;

    }


    // 일정 등록 액티비티 수행 결과
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1000) {
            return;
        }

        //프래그먼트 재시작
        MonthFragment monthFragment = new MonthFragment(year, month, date);
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.calendar, monthFragment);
        tr.commit();



    }


}

