package com.proj.hwanik.water;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

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
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;

public class parsingJson extends AppCompatActivity {

    TextView ph,tak,zan,junsujang,update;

    Toolbar toolbar;
    MaterialSpinner spinner;
    String PurificationPlant_choice;
    MaterialDialog.Builder dialogBuilder;
    MaterialDialog mDialog;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    int index;
    int checkpoint;

    String[] PurificationPlant={"고령 정수장", "고산 정수장", "고양 정수장", "공주 정수장", "구미 정수장", "구천 정수장", "금산 정수장", "덕소 정수장", "덕정 정수장", "동화 정수장", "밀양 정수장", "반송 정수장",
            "반월 정수장", "별량 정수장", "보령 정수장", "부안 정수장", "사천 정수장", "산성 정수장", "석성 정수장", "성남 정수장", "송전 정수장", "수지 정수장", "시흥 정수장", "아산 정수장",
            "양산 정수장", "연초 정수장", "와부 정수장", "운문 정수장", "일산 정수장", "자인 정수장", "천안 정수장", "청주 정수장", "충주 정수장", "평림 정수장", "학야 정수장", "화순 정수장"};
    String[] PurificationPlant_code={"387", "367", "319", "357", "337", "332", "372", "317", "389", "385", "381", "335",
            "311", "368", "359", "360", "333", "363", "351", "313", "356", "316", "314", "354",
            "382", "331", "312", "340", "315", "339", "355", "353", "380", "386", "378", "364"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing_json);

        toolbar=(Toolbar)findViewById(R.id.toolbar_spinner);
        setSupportActionBar(toolbar);
        setTitle("수질정보");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PurificationPlant);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        items = (ArrayList<String>) getIntent().getSerializableExtra("data");
        index = getIntent().getIntExtra("index",1);

        ph = (TextView)findViewById(R.id.ph);
        tak = (TextView)findViewById(R.id.tak);
        zan = (TextView)findViewById(R.id.zan);
        junsujang = (TextView)findViewById(R.id.jungsujang);

        update = (TextView)findViewById(R.id.dayOfUpdate);

        dialogBuilder = new MaterialDialog.Builder(parsingJson.this);
        dialogBuilder.title("데이터 로드중..")
                .content("잠시만 기다려주세요")
                .progress(true, 0);
        mDialog = dialogBuilder.build();


        spinner = (MaterialSpinner)findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(index);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                PurificationPlant_choice = PurificationPlant_code[position];
                data.clear();
                index=position;
                new JsonLoadingTask().execute();
                junsujang.setText(PurificationPlant[index]);
                mDialog.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new JsonLoadingTask().execute();
    }
    public String getJungsujang(String code){
        for(int i=0;i<PurificationPlant_code.length;i++){
            if(PurificationPlant_code[i].equals(code))
                return PurificationPlant[i];
        }
        return null;
    }
    public int userJungsujangIndex(String code){
        int tmp = 0;
        for(int i=0;i<PurificationPlant_code.length;i++){
            if(PurificationPlant_code[i].equals(code)) {
                tmp=i;
            }
        }
        return tmp;
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
            if(index!=-1) {
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                SimpleDateFormat mSimpleDateFormat1 = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
                Date currentTime = new Date();
                String mTime = mSimpleDateFormat.format(currentTime);
                String mTime1 = mSimpleDateFormat1.format(currentTime);

                SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
                String strCurHour = CurHourFormat.format(currentTime);
                int hour = Integer.parseInt(strCurHour) - 1;
                strCurHour = String.valueOf(hour);
                Log.d("asdf", strCurHour);

                String line = getStringFromUrl("http://opendata.kwater.or.kr/openapi-data/service/pubd/rwis/waterQuality/list?_type=json&stDt=20151119&stTm=00&edDt=" + mTime + "&edTm=" + strCurHour + "&sujCode=" + PurificationPlant_choice + "&numOfRows=10&pageNo=1&serviceKey=E%2B0%2BhJolGH9ppT7r1hfU18qRRHvxTQOATomUAZ%2BIiHXSQ666uyApIt0sQCdWmGCM%2FlRiQ8wGLHTDr4aG6EyCbQ%3D%3D");
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
                    data.add(insideObject.getString("phVal") + "pH");
                    data.add("0" + insideObject.getString("tbVal") + "NTU");
                    data.add("0" + insideObject.getString("clVal") + "MG/L");
                    data.add(mTime1 + " " + strCurHour + "시 기준)");
                }
            }
//            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy-MM-dd", Locale.KOREA );
//            Date currentTime = new Date ( );
//            String mTime = mSimpleDateFormat.format ( currentTime );
//
//            SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
//            String strCurHour = CurHourFormat.format(currentTime);
//
//            String line = getStringFromUrl("http://opendata.kwater.or.kr:80/openapi-data/service/pubd/waterways/wdr/dailwater/list?_type=json&fcode="+PurificationPlant_choice+"&stdt=2015-10-01&eddt="+mTime+"&serviceKey=E%2B0%2BhJolGH9ppT7r1hfU18qRRHvxTQOATomUAZ%2BIiHXSQ666uyApIt0sQCdWmGCM%2FlRiQ8wGLHTDr4aG6EyCbQ%3D%3D");
//            String line1 = line.substring(96,line.length()-4);
//
//            /* 넘어오는 데이터 구조 { [ { } ] } JSON 객체 안에 배열안에 내부JSON 객체*/
//
//            JSONArray Array = new JSONArray(line.substring(96,line.length()-4));
//
//            // bodylist 배열안에 내부 JSON 이므로 JSON 내부 객체 생성
//            JSONObject insideObject = Array.getJSONObject(0);
//
//            // JSONObject 메소드 ( get.String(), getInt(), getBoolean() .. 등 : 객체로부터 데이터의 타입에 따라 원하는 데이터를 읽는다. )
//            data.add(insideObject.getString("item4") + "pH");
//            data.add(insideObject.getString("item5") + "NTU");
//            if(!PurificationPlant_choice.equals("337")) {
//                data.add(insideObject.getString("item6") + "mg/L");
//            }
//            data.add(mTime+" "+strCurHour);

            // for
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
        BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromUrl(url), "UTF-8"),8);

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
//            tv.setText(result);
            if(data.size()!=0) {
                ph.setText(data.get(0));
                tak.setText(data.get(1));
                zan.setText(data.get(2));
                junsujang.setText(PurificationPlant[index]);
                update.setText(" (" + data.get(3));
                mDialog.dismiss();
            }else{
                checkpoint++;
                ph.setText("점검중..");
                tak.setText("점검중..");
                zan.setText("점검중..");
                junsujang.setText("(해당지역 수질정보 미지원)");
                if(checkpoint<3)
                    new JsonLoadingTask().execute();
            }
        } // onPostExecute : 백그라운드 작업이 끝난 후 UI 작업을 진행한다.
    } // JsonLoadingTask
}