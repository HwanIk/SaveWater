package com.example.hwanik.water;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import android.graphics.*;
public class WaterVolumebyMonth extends AppCompatActivity {

    BarChart[] chart = new BarChart[3];
    BarData[] data = new BarData[3];
    BarDataSet[] dataset = new BarDataSet[3];
    int TotalWaterVolumes;
    ArrayList<BarEntry> volumeEntries;
    ArrayList<BarEntry> sPriceEntries;
    ArrayList<BarEntry> gPriceEntries;
    Toolbar toolbar;
    NumberPicker yearPicker;
    NumberPicker monthPicker;

    ArrayList<String> labels;
    MaterialDialog.Builder dialogBuilder;
    MaterialDialog mDialog;
    MaterialDialog.Builder dialogBuilder1;
    MaterialDialog mDialog1;

    int userFmailyNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_volume_by_month);
        toolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(4);
        setTitle("월 별 수도내역");

        userFmailyNumber=Integer.parseInt(ParseUser.getCurrentUser().get("FamilyMember").toString());

        chart[0] = (BarChart) findViewById(R.id.chart);
        chart[1] = (BarChart) findViewById(R.id.chart1);
//        chart[2] = (BarChart) findViewById(R.id.chart2);
        volumeEntries = new ArrayList<>();
        sPriceEntries = new ArrayList<>();
        gPriceEntries = new ArrayList<>();

        labels = new ArrayList<String>();
        for(int i=0;i<12;i++) {
            volumeEntries.add(new BarEntry(0, i));
            sPriceEntries.add(new BarEntry(0, i));
            gPriceEntries.add(new BarEntry(0, i));
            labels.add((i+1)+"월");
        }

        dialogBuilder = new MaterialDialog.Builder(WaterVolumebyMonth.this);
        dialogBuilder.title("데이터 로드중..")
                .content("잠시만 기다려주세요")
                .progress(true, 0);
        mDialog = dialogBuilder.build();
        mDialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWaterData");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> waterVolumes, ParseException e) {
                if (e == null) {
                    if (waterVolumes.size() != 0) {
                        JSONArray[] tmp = new JSONArray[3];
                        tmp[0] = (JSONArray) waterVolumes.get(0).getJSONArray("waterVolumes");
                        tmp[1] = (JSONArray) waterVolumes.get(0).getJSONArray("waterSPrice");
                        tmp[2] = (JSONArray) waterVolumes.get(0).getJSONArray("waterGPrice");
                        for (int i = 0; i < 12; i++) {
                            try {
                                volumeEntries.set(i, new BarEntry(tmp[0].getInt(i), i));
                                sPriceEntries.set(i, new BarEntry(tmp[1].getInt(i), i));
                                gPriceEntries.set(i, new BarEntry(tmp[2].getInt(i), i));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        for (int i = 0; i < 2; i++) {
                            data[i] = new BarData(labels, dataset[i]);
                            chart[i].setData(data[i]);
                            chart[i].invalidate();
                        }

                        mDialog.dismiss();
                    } else {
                        mDialog.dismiss();
                    }
                } else {
                    Log.d("UserWaterData", "Error: " + e.getMessage());
                }
            }
        });

        dataset[0] = new BarDataSet(volumeEntries, "물 사용량(m³)");
        dataset[1] = new BarDataSet(sPriceEntries, "세대 요금(원)");
        dataset[1].setColor(Color.parseColor("#FFF6FF49"));
//        dataset[2] = new BarDataSet(gPriceEntries, "공용 요금");
//        dataset[2].setColor(Color.parseColor("#FFFF9100"));
        data[0] = new BarData(labels, dataset[0]);
        for(int i=0;i<2;i++) {
            chart[i].setData(data[0]);
            chart[i].setDescription("");
        }

        chart[0].setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(final Entry e, int dataSetIndex, Highlight h) {
                final int month=e.getXIndex();
                final EditText waterVolume;
                boolean wrapInScrollView = true;

                MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(WaterVolumebyMonth.this);
                dialogBuilder.title((month+1)+"월 물 사용량 수정")
                        .customView(R.layout.edit_water_by_month, wrapInScrollView)
                        .positiveText("수정")
                        .negativeText("데이터 삭제");
                MaterialDialog mDialog = dialogBuilder.build();

                waterVolume=(EditText)mDialog.findViewById(R.id.waterVolume);

                dialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        volumeEntries.set(month, new BarEntry(getDataByDay(month+1,waterVolume.getText().toString()), month));
                        data[0] = new BarData(labels, dataset[0]);
                        chart[0].setData(data[0]);
                        chart[0].invalidate();
                    }
                });
                dialogBuilder.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        volumeEntries.set(month, new BarEntry(0, month));
                        data[0] = new BarData(labels, dataset[0]);
                        chart[0].setData(data[0]);
                        chart[0].invalidate();
                    }
                });
                mDialog.show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        chart[1].setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(final Entry e, int dataSetIndex, Highlight h) {
                final int month = e.getXIndex();
                final EditText sPrice;
                final EditText gPrice;
                boolean wrapInScrollView = true;

                MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(WaterVolumebyMonth.this);
                dialogBuilder.title((month + 1) + "월 수도요금 수정")
                        .customView(R.layout.edit_price_by_month, wrapInScrollView)
                        .positiveText("수정")
                        .negativeText("데이터 삭제");
                MaterialDialog mDialog = dialogBuilder.build();

                sPrice = (EditText) mDialog.findViewById(R.id.sPrice);
                gPrice = (EditText) mDialog.findViewById(R.id.gPrice);

                dialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sPriceEntries.set(month, new BarEntry(getDataByDay(month + 1, sPrice.getText().toString()), month));
                        gPriceEntries.set(month, new BarEntry(getDataByDay(month + 1, gPrice.getText().toString()), month));
                        data[1] = new BarData(labels, dataset[1]);
                        chart[1].setData(data[1]);
                        chart[1].invalidate();
                    }
                });
                dialogBuilder.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        sPriceEntries.set(month, new BarEntry(0, month));
                        gPriceEntries.set(month, new BarEntry(0, month));
                        for (int i = 0; i < 2; i++) {
                            data[i] = new BarData(labels, dataset[i]);
                            chart[i].setData(data[i]);
                            chart[i].invalidate();
                        }
                    }
                });
                mDialog.show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_water_volumeby_month, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addItem) {
            addDialog();
            return true;
        }
        if (id == R.id.update) {
            updateToServer();
            return true;
        }
        if (id == R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateToServer() {

        dialogBuilder1 = new MaterialDialog.Builder(WaterVolumebyMonth.this);
        dialogBuilder1.title("서버에 업데이트중..")
                .content("잠시만 기다려주세요")
                .progress(true, 0);
        mDialog1 = dialogBuilder1.build();
        mDialog1.show();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWaterData");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if(list.size()!=0) {
                        query.getInBackground(list.get(0).getObjectId(), new GetCallback<ParseObject>() {
                            public void done(ParseObject data, ParseException e) {
                                if (e == null) {
                                    // Now let's update it with some new data. In this case, only cheatMode and score
                                    // will get sent to the Parse Cloud. playerName hasn't changed.
                                    JSONArray waterVolumes = new JSONArray();
                                    JSONArray waterSPrice = new JSONArray();
                                    JSONArray waterGPrice = new JSONArray();
                                    for (int i = 0; i < 12; i++) {
                                        try {
                                            TotalWaterVolumes+=volumeEntries.get(i).getVal();
                                            waterVolumes.put(volumeEntries.get(i).getVal());
                                            waterSPrice.put(sPriceEntries.get(i).getVal());
                                            waterGPrice.put(gPriceEntries.get(i).getVal());
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                    data.put("waterVolumes", waterVolumes);
                                    data.put("waterSPrice", waterSPrice);
                                    data.put("waterGPrice", waterGPrice);
                                    data.put("TotalWaterVolumes",TotalWaterVolumes);
                                    data.saveInBackground();
                                }
                            }
                        });
                    }else{
                        ParseObject data = new ParseObject("UserWaterData");
                        JSONArray waterVolumes = new JSONArray();
                        JSONArray waterSPrice = new JSONArray();
                        JSONArray waterGPrice = new JSONArray();

                        data.put("year", 2015);
                        data.put("user", ParseUser.getCurrentUser().getObjectId().toString());
                        for (int i = 0; i < 12; i++) {
                            try {
                                TotalWaterVolumes+=volumeEntries.get(i).getVal();
                                waterVolumes.put(volumeEntries.get(i).getVal());
                                waterSPrice.put(sPriceEntries.get(i).getVal());
                                waterGPrice.put(gPriceEntries.get(i).getVal());
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        data.put("waterVolumes", waterVolumes);
                        data.put("waterSPrice", waterSPrice);
                        data.put("waterGPrice", waterGPrice);
                        data.put("TotalWaterVolumes",TotalWaterVolumes);
                        data.saveInBackground();
                    }
                } else {

                }
            }
        });
        Toast.makeText(this,"업데이트가 완료되었습니다.",Toast.LENGTH_SHORT).show();
        mDialog1.dismiss();
    }

    private void addDialog(){
        final EditText waterSPrice;
        final EditText waterGPrice;
        final EditText waterVolume;
        boolean wrapInScrollView = true;

        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this);
        dialogBuilder.title("물 사용량, 물 값 입력")
                .customView(R.layout.custom_dialog_view, wrapInScrollView)
                .positiveText("추가")
                .negativeText("취소");
        MaterialDialog mDialog = dialogBuilder.build();

        waterSPrice=(EditText)mDialog.findViewById(R.id.sPrice);
        waterGPrice=(EditText)mDialog.findViewById(R.id.gPrice);
        waterVolume=(EditText)mDialog.findViewById(R.id.waterVolume);

        yearPicker=(NumberPicker)mDialog.findViewById(R.id.year);
        monthPicker=(NumberPicker)mDialog.findViewById(R.id.month);
        yearPicker.setMaxValue(2020);
        yearPicker.setMinValue(2015);
        yearPicker.setValue(2015);
        yearPicker.setWrapSelectorWheel(false);
        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(1);
        monthPicker.setWrapSelectorWheel(false);

        dialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                volumeEntries.set(monthPicker.getValue() - 1, new BarEntry(getDataByDay(monthPicker.getValue(),waterVolume.getText().toString()), monthPicker.getValue() - 1));
                sPriceEntries.set(monthPicker.getValue() - 1, new BarEntry(getDataByDay(monthPicker.getValue(),waterSPrice.getText().toString()), monthPicker.getValue() - 1));
                gPriceEntries.set(monthPicker.getValue() - 1, new BarEntry(getDataByDay(monthPicker.getValue(),waterGPrice.getText().toString()), monthPicker.getValue() - 1));
                for (int i = 0; i < 2; i++) {
                    data[i] = new BarData(labels, dataset[i]);
                    chart[i].setData(data[i]);
                    chart[i].invalidate();
                }
            }
        });
        mDialog.show();
    }
    public int getDataByDay(int month, String s){
        int data;
        int days;

        if(month%2==1){
            days=31;
        } else {
            days=30;
        }

        if(s.equals("")){
            data=0;
        }else{
            data=Integer.parseInt(s)/days/userFmailyNumber;
        }
        return data;
    }
}
