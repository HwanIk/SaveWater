package com.example.hwanik.water;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialDialog.Builder dialogBuilder;
    MaterialDialog mDialog;

    //차트 그리기에 관련된 변수
    LineChart mChart;

    ArrayList<BarEntry> volumeEntries;
    BarChart chart;
    BarData data;
    BarDataSet dataset;
    ArrayList<String> labels;

    //공공데이터를 담는 리스트
    ArrayList<String> items = new ArrayList<>();
    private String PurificationPlant_choice;

    TextView ph,tak,zan;

    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;

    int [] mResources={
            R.drawable.ad1, R.drawable.ad2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(4);

        ph = (TextView)findViewById(R.id.ph);
        tak = (TextView)findViewById(R.id.tak);
        zan = (TextView)findViewById(R.id.zan);

        mCustomPagerAdapter = new CustomPagerAdapter(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
//
        PurificationPlant_choice="311";

        dialogBuilder = new MaterialDialog.Builder(MainActivity.this);
        dialogBuilder.title("데이터 로드중..")
                .content("잠시만 기다려주세요")
                .progress(true, 0);
        mDialog = dialogBuilder.build();
        mDialog.show();

        refresh();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present. 
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            logout();
            return true;
        }
        if (id == R.id.refresh){
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void LineChartSet() {
        //linechart set
        mChart = (LineChart) findViewById(R.id.chart1);

        mChart.setDrawGridBackground(false); // 그래프 회색 바탕
        mChart.setDescription("");
        mChart.setDrawBorders(false); // 그래프 겉 테두리

//        mChart.getAxisLeft().setDrawAxisLine(true); 좌측 y축 일자선
//        mChart.getAxisLeft().setDrawGridLines(true); 데이터 가로선
        mChart.getAxisRight().setDrawAxisLine(false);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false); // 데이터 세로선

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        generateLineData();
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
    }

    private void generateLineData() {
        mChart.resetTracking();

        final ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWaterData");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                int maxIndex = -1,myIndex=-1,minIndex=-1;
                int maxWV=0,minWV=0;
                maxWV = (int) list.get(0).get("TotalWaterVolumes");
                minWV = (int) list.get(0).get("TotalWaterVolumes");
                for (int i = 0; i < list.size(); i++) {
                    int tmp=Integer.parseInt(list.get(i).get("TotalWaterVolumes").toString());
                    if(list.get(i).get("user").equals(ParseUser.getCurrentUser().getObjectId())){
                        myIndex=i;
                    }
                    if (maxWV < tmp) {
                        maxIndex = i;
                        maxWV = tmp;
                    }
                    if (minWV > tmp){
                        minIndex = i;
                        minWV = tmp;
                    }
                }
                if(maxIndex!=-1) {
                    JSONArray maxArray = (JSONArray) list.get(maxIndex).getJSONArray("waterVolumes");
                    DrawLineChart("maxUser",maxIndex,maxArray,dataSets);
                }
                if(myIndex!=-1) {
                    JSONArray myArray = (JSONArray) list.get(myIndex).getJSONArray("waterVolumes");
                    DrawLineChart("my",myIndex,myArray,dataSets);
                }
                if(minIndex!=-1) {
                    JSONArray minArray = (JSONArray) list.get(minIndex).getJSONArray("waterVolumes");
                    DrawLineChart("minUser",minIndex,minArray,dataSets);
                }
            }
        });
    }

    private void DrawLineChart(String name, int index, JSONArray tmp, ArrayList<LineDataSet> dataSets) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        final ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            xVals.add((i) + "월");
        }

        for (int j = 0; j < 12; j++) {
            try {
                values.add(new Entry(tmp.getInt(j), j));
            } catch (JSONException e1) {}
        }

        LineDataSet d = new LineDataSet(values, name);
        d.setLineWidth(2.5f);
        d.setCircleSize(4f);

        int color = mColors[ index % mColors.length];
        d.setColor(color);
        d.setCircleColor(color);
        dataSets.add(d);
        // make the first DataSet dashed
//                dataSets.get(0).enableDashedLine(10, 10, 0);
//                dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
//                dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        //                dataSets.add(userAverage(list));
        LineData data = new LineData(xVals, dataSets);
        mChart.setData(data);
        mChart.invalidate();
        mDialog.dismiss();
    }

    private LineDataSet userAverage(List<ParseObject> list) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        int sum[]=new int[12];
        for(int i=0;i<list.size();i++) {
            JSONArray tmp = (JSONArray) list.get(i).getJSONArray("waterVolumes");
            for(int j=0;j<12;j++){
                try {
                    sum[j]+=tmp.getInt(j);
                } catch (JSONException e) {}
            }
        }
        for(int i=0;i<12;i++) values.add(new Entry(sum[i]/=3,i));
        LineDataSet d = new LineDataSet(values, "Average");
        return d;
    }
    
    private int[] mColors = new int[] {
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };

    private void refresh() {
        mDialog.show();
        LineChartSet();
        new JsonLoadingTask().execute();
    }

    private void logout() {
        ParseUser.logOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

//    private void loadChartData() {
//
//        chart = (BarChart) findViewById(R.id.chart);
//        volumeEntries = new ArrayList<>();
//        labels = new ArrayList<String>();
//        for(int i=0;i<12;i++) {
//            volumeEntries.add(new BarEntry(0, i));
//            labels.add((i+1)+"월");
//        }
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWaterData");
//        query.whereEqualTo("user", ParseUser.getCurrentUser().getObjectId());
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> waterVolumes, ParseException e) {
//                if (e == null) {
//                    if (waterVolumes.size() != 0) {
//                        JSONArray tmp = new JSONArray();
//                        tmp = (JSONArray) waterVolumes.get(0).getJSONArray("waterVolumes");
//                        for (int i = 0; i < 12; i++) {
//                            try {
//                                volumeEntries.set(i, new BarEntry(tmp.getInt(i), i));
//                            } catch (JSONException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                        data = new BarData(labels, dataset);
//                        chart.setData(data);
//                        chart.invalidate();
//                    } else {
//
//                    }
//                } else {
//                    Log.d("UserWaterData", "Error: " + e.getMessage());
//                }
//            }
//        });
//
//        dataset = new BarDataSet(volumeEntries, "물 사용량");
//        data = new BarData(labels, dataset);
//        chart.setData(data);
//        chart.setDescription("");
//    }

    public String getJsonText() {

        // 내부적으로 문자열 편집이 가능한 StringBuffer 생성자
        StringBuffer sb = new StringBuffer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // 해당 작업을 처리함
                    }
                });
            }
        }).start();
        try {
            String line = getStringFromUrl("http://opendata.kwater.or.kr:80/openapi-data/service/pubd/waterways/wdr/dailwater/list?_type=json&fcode="+PurificationPlant_choice+"&stdt=2014-10-01&eddt=2014-10-07&serviceKey=E%2B0%2BhJolGH9ppT7r1hfU18qRRHvxTQOATomUAZ%2BIiHXSQ666uyApIt0sQCdWmGCM%2FlRiQ8wGLHTDr4aG6EyCbQ%3D%3D");
            String line1 = null;

            line1 = line.substring(96,line.length()-4);
            if(PurificationPlant_choice.equals("337")){
                line1="["+line1+"]";
            }
                /* 넘어오는 데이터 구조 { [ { } ] } JSON 객체 안에 배열안에 내부JSON 객체*/

            JSONArray Array = new JSONArray(line1);

            // bodylist 배열안에 내부 JSON 이므로 JSON 내부 객체 생성
            JSONObject insideObject = Array.getJSONObject(0);

            // StringBuffer 메소드 ( append : StringBuffer 인스턴스에 뒤에 덧붙인다. )
            // JSONObject 메소드 ( get.String(), getInt(), getBoolean() .. 등 : 객체로부터 데이터의 타입에 따라 원하는 데이터를 읽는다. )
            if(insideObject.getString("item1")==null){
                return null;
            }else {
                items.add(insideObject.getString("item4"));
                items.add(insideObject.getString("item5"));
                items.add(insideObject.getString("item6"));
                items.add(insideObject.getString("mesurede"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    } // getJsonText

    // getStringFromUrl : 주어진 URL 페이지를 문자열로 얻는다.
    public String getStringFromUrl(String url) throws UnsupportedEncodingException {

        // 입력스트림을 "UTF-8" 를 사용해서 읽은 후, 라인 단위로 데이터를 읽을 수 있는 BufferedReader 를 생성한다.
        BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromUrl(url), "UTF-8"));

        // 읽은 데이터를 저장한 StringBuffer 를 생성한다.
        StringBuffer sb = new StringBuffer();

        try {
            // 라인 단위로 읽은 데이터를 임시 저장한 문자열 변수 line
            String line = null;

            // 라인 단위로 데이터를 읽어서 StringBuffer 에 저장한다.
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    } // getStringFromUrl

    // getInputStreamFromUrl : 주어진 URL 에 대한 입력 스트림(InputStream)을 얻는다.
    public static InputStream getInputStreamFromUrl(String url) {
        InputStream contentStream = null;
        try {
            // HttpClient 를 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            contentStream = response.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentStream;
    } // getInputStreamFromUrl

    private class JsonLoadingTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strs) {
            return getJsonText();
        } // doInBackground : 백그라운드 작업을 진행한다.
        @Override
        protected void onPostExecute(String result) {
            if(items.size()!=0) {
                float n_ph = Float.parseFloat(items.get(0));
                float n_tak = Float.parseFloat(items.get(1));
                float n_zan = Float.parseFloat(items.get(2));

                if (n_ph >= 5.8 && n_ph <= 8.5) ph.setText("매우 좋음");
                else if (n_ph < 5.8 && n_ph >= 8.5) ph.setText("보통");

                if (n_tak < 0.3) tak.setText("매우 좋음");
                else if (n_tak > 0.3 && n_tak <= 0.5) tak.setText("좋음");
                else if (n_tak > 0.5 && n_tak <= 1.0) tak.setText("보통");
                else if (n_tak > 1.0) tak.setText("나쁨");

                if (n_zan >= 0.1 && n_zan < 1.0) zan.setText("매우 좋음");
                else if (n_zan >= 1.0 && n_zan < 4.0) zan.setText("좋음");
                else if (n_zan < 0.1) zan.setText("보통");
                else if (n_zan > 4.0) zan.setText("나쁨");

//                progressDialog.dismiss();
            } else {
                ph.setText("데이터 요청중..");
                tak.setText("데이터 요청중..");
                zan.setText("데이터 요청중..");
                new JsonLoadingTask().execute();
            }
        } // onPostExecute : 백그라운드 작업이 끝난 후 UI 작업을 진행한다.
    } // JsonLoadingTask

    class CustomPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);

            container.addView(itemView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DetailAd.class);
                    intent.putExtra("resId",mResources[position]);
                    startActivity(intent);
                }
            });
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    public void goToParsingJson(View view) {
        Intent intent = new Intent(MainActivity.this,parsingJson.class);
        intent.putExtra("data", items);
        startActivity(intent);
    }
    public void goToWVM(View view) {
        Intent intent = new Intent(MainActivity.this,WaterVolumebyMonth.class);
        startActivity(intent);
    }
    public void goToWaterData(View view) {
        Intent intent = new Intent(MainActivity.this,WaterData.class);
        startActivity(intent);
    }
}