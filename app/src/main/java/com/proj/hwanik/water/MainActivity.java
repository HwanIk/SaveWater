package com.proj.hwanik.water;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.graphics.Color;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialDialog.Builder dialogBuilder;
    MaterialDialog mDialog;
    int checkpoint=0;
    //차트 그리기에 관련된 변수http://blog.naver.com/PostList.nhn?blogId=bbaldeapp&from=postList&categoryNo=14
    LineChart mChart;

    //공공데이터를 담는 리스트
    ArrayList<String> items = new ArrayList<>();
    private String PurificationPlant_choice="";
    private String Jungsujang;
    private int userIndex=-1;

    TextView ph,tak,zan,suzilSubTitle;

    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    int numOfAd;
    String[] adImg;
    String[] titleOfAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(4);
        setTitle("水계부(" + ParseUser.getCurrentUser().getUsername() + "님)");
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingUser.class);
                startActivity(intent);
            }
        });

        ph = (TextView)findViewById(R.id.ph);
        tak = (TextView)findViewById(R.id.tak);
        zan = (TextView)findViewById(R.id.zan);
        suzilSubTitle = (TextView)findViewById(R.id.suzilSubTitle);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("AddressCode");
        query.whereEqualTo("sido", ParseUser.getCurrentUser().get("city").toString());
        Log.d("sadf", ParseUser.getCurrentUser().get("city").toString());
        query.whereEqualTo("gungu", ParseUser.getCurrentUser().get("district").toString());
        Log.d("sadf", ParseUser.getCurrentUser().get("district").toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if(list.get(0).get("matchCode") != null){
                    PurificationPlant_choice = list.get(0).get("matchCode").toString();
                    parsingJson pj = new parsingJson();
                    userIndex = pj.userJungsujangIndex(PurificationPlant_choice);
                    Jungsujang = pj.getJungsujang(PurificationPlant_choice);
                }else{
                    PurificationPlant_choice="";
                }
                refresh();
            }
        });

        dialogBuilder = new MaterialDialog.Builder(MainActivity.this);
        dialogBuilder.title("데이터 로드중..")
                .content("잠시만 기다려주세요")
                .progress(true, 0);
        mDialog = dialogBuilder.build();
        mDialog.show();

        loadAd();
    }

    private void loadAd() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ad");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> adList, ParseException e) {
                Log.d("asdf", String.valueOf(adList.size()));
                if (adList.size() != 0) {
                    numOfAd = adList.size();
                    titleOfAd = new String[numOfAd];
                    adImg = new String[numOfAd];
                    for (int i = 0; i < numOfAd; i++) {
                        titleOfAd[i] = adList.get(i).get("Title").toString();
                        ParseFile tmp = (ParseFile) adList.get(i).get("Img");
                        adImg[i] = tmp.getUrl();
                    }
                } else {
                    return;
                }
                mCustomPagerAdapter = new CustomPagerAdapter(MainActivity.this, numOfAd, titleOfAd, adImg);
                mViewPager.setAdapter(mCustomPagerAdapter);
                mCustomPagerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present. 
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void refresh() {
        mDialog.show();
        LineChartSet();
        new JsonLoadingTask().execute();
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
        mChart.getAxisRight().setDrawAxisLine(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getAxisRight().setDrawLabels(false);

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
                float maxWV=0,minWV=0;
                maxWV = Float.parseFloat(String.valueOf(list.get(0).get("TotalWaterVolumes")));
                minWV = Float.parseFloat(String.valueOf(list.get(0).get("TotalWaterVolumes")));
                for (int i = 0; i < list.size(); i++) {
                    float tmp=Float.parseFloat(list.get(i).get("TotalWaterVolumes").toString());

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
                if(maxIndex!=-1 ) {
                    String name="상위 10%";
                    JSONArray maxArray = (JSONArray) list.get(maxIndex).getJSONArray("conversionWV");
                    DrawLineChart(name,maxIndex,maxArray,dataSets);
                }
                if(myIndex!=-1) {
                    String name=ParseUser.getCurrentUser().getUsername().toLowerCase();
                    JSONArray myArray = (JSONArray) list.get(myIndex).getJSONArray("conversionWV");
                    DrawLineChart(name,myIndex,myArray,dataSets);
                }
                if(minIndex!=-1) {
                    String name="하위 10%";
                    JSONArray minArray = (JSONArray) list.get(minIndex).getJSONArray("conversionWV");
                    DrawLineChart(name,minIndex,minArray,dataSets);
                }
            }
        });
    }

    private void DrawLineChart(String name, int index, JSONArray tmp, ArrayList<LineDataSet> dataSets) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        final ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
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

        int color = 0;
        if(name.equals(ParseUser.getCurrentUser().getUsername().toLowerCase()))
            color = Color.parseColor("#FFFF0000");
        else if(name.equals("상위 10%"))
            color = Color.parseColor("#FF44C1FF");
        else if(name.equals("하위 10%"))
            color = Color.parseColor("#FFF9A600");
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
    }

    private int[] mColors = new int[] {
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };

    private void logout() {
        ParseUser.logOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

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
            if(!PurificationPlant_choice.equals("")) {
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                SimpleDateFormat mSimpleDateFormat1 = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
                Date currentTime = new Date();
                String mTime = mSimpleDateFormat.format(currentTime);
                String mTime1 = mSimpleDateFormat1.format(currentTime);

                SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
                String strCurHour = CurHourFormat.format(currentTime);
                int hour = Integer.parseInt(strCurHour) - 1;
                strCurHour = String.valueOf(hour);

                String line = getStringFromUrl("http://opendata.kwater.or.kr/openapi-data/service/pubd/rwis/waterQuality/list?_type=json&stDt=20151119&stTm=00&edDt=" + mTime + "&edTm="+strCurHour+"&sujCode=" + PurificationPlant_choice + "&numOfRows=10&pageNo=1&serviceKey=E%2B0%2BhJolGH9ppT7r1hfU18qRRHvxTQOATomUAZ%2BIiHXSQ666uyApIt0sQCdWmGCM%2FlRiQ8wGLHTDr4aG6EyCbQ%3D%3D");
                String line1 = line.substring(96, line.length() - 4);


            /* 넘어오는 데이터 구조 { [ { } ] } JSON 객체 안에 배열안에 내부JSON 객체*/

                JSONArray Array = new JSONArray(line1);

                // bodylist 배열안에 내부 JSON 이므로 JSON 내부 객체 생성
                JSONObject insideObject = Array.getJSONObject(0);

                // StringBuffer 메소드 ( append : StringBuffer 인스턴스에 뒤에 덧붙인다. )
                // JSONObject 메소드 ( get.String(), getInt(), getBoolean() .. 등 : 객체로부터 데이터의 타입에 따라 원하는 데이터를 읽는다. )
                if (insideObject.getString("phVal") == null) {
                    return null;
                } else {
                    items.add(insideObject.getString("phVal"));
                    items.add(insideObject.getString("tbVal"));
                    items.add(insideObject.getString("clVal"));
                    items.add(mTime1 + " " + strCurHour + "시 기준");
                }
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

                suzilSubTitle.setText("("+Jungsujang+" "+items.get(3)+")");
//                progressDialog.dismiss();
            } else if(items.size() == 0) {
                checkpoint++;
                ph.setText("점검중..");
                tak.setText("점검중..");
                zan.setText("점검중..");
                suzilSubTitle.setText("(해당지역 수질정보 미지원)");
                if(checkpoint<3)
                    new JsonLoadingTask().execute();
            }
            mDialog.dismiss();
        } // onPostExecute : 백그라운드 작업이 끝난 후 UI 작업을 진행한다.
    } // JsonLoadingTask



    public void goToParsingJson(View view) {
        Intent intent = new Intent(MainActivity.this,parsingJson.class);
        intent.putExtra("data", items);
        intent.putExtra("index",userIndex);
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