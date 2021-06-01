package com.chr.calendarapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import com.chr.calendarapp.R;

import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WeekRegisterScheduleActivity extends AppCompatActivity {

    EditText et_title, et_place, et_memo;
    TimePicker time_start, time_end;
    Button btn_search, btn_save, btn_cancel, btn_delete;
    MapView mapView;

    double latitude, longitude;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_register_schedule);

        et_title = findViewById(R.id.et_title);
        et_place = findViewById(R.id.et_place);
        et_memo = findViewById(R.id.et_memo);
        time_start = findViewById(R.id.time_start);
        time_end = findViewById(R.id.time_end);
        btn_search = findViewById(R.id.btn_search);
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_delete = findViewById(R.id.btn_delete);

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);



        Intent intent = getIntent();
        int year = intent.getIntExtra("year", 0);
        int month = intent.getIntExtra("month", 0);
        int day = intent.getIntExtra("day", 0);
        int time = intent.getIntExtra("time", 0);

        et_title.setText(year + "년 " + month + "월 " + day + "일 " + time + "시");
        time_start.setHour(time);
        time_start.setMinute(0);
        time_end.setHour(time + 1);
        time_end.setMinute(0);


        btn_search.setOnClickListener(click);
        btn_save.setOnClickListener(click);
        btn_delete.setOnClickListener(click);
        btn_cancel.setOnClickListener(click);
    }

    // 버튼 클릭 이벤트
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_search:
                    // 장소 검색
                    String search = et_place.getText().toString();

                    // 검색 창이 비어있다면
                    if(search == null || search.isEmpty()){
                        Toast.makeText(WeekRegisterScheduleActivity.this, "장소를 검색해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 장소 검색을해서 위도, 경도 찾기
                    getLocation(search);

                    // 마커 띄우기
                    showMarker(search, latitude, longitude);

                    break;

                case R.id.btn_save:
                    break;

                case R.id.btn_delete:
                    break;

                    // 취소 클릭 시
                case R.id.btn_cancel:
                    // 돌아가기
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    break;
            }
        }
    };


    // 주소 이름을 통해 위도 경도 받기
    public void getLocation(String search){
        try {
            Geocoder geocoder = new Geocoder(this, Locale.KOREA);
            List<Address> addresses = geocoder.getFromLocationName(search,1);
            if (addresses.size() >0) {
                Address bestResult = (Address) addresses.get(0);
                latitude =  bestResult.getLatitude();
                longitude =  bestResult.getLongitude();
            }
        } catch (IOException e) {
            Log.e(getClass().toString(),"Failed in using Geocoder.", e);
            return;
        }
    }


    // 해당 주소에 마커 띄우기
    public void showMarker(String search, double latitude, double longitude){
        // 마커 띄우기
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(search);
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본 마커
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커 클릭 시
        mapView.addPOIItem(marker);

        // 화면 중앙에 표시 될 위치
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
    }
}