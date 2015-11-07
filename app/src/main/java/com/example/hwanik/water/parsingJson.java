package com.example.hwanik.water;

import android.app.Activity;
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
import android.widget.Toast;

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
import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;

public class parsingJson extends AppCompatActivity {

    TextView tv;
    Toolbar toolbar;
    MaterialSpinner spinner;
    String PurificationPlant_choice;
    MaterialDialog.Builder dialogBuilder;
    MaterialDialog mDialog;
    ArrayList<String> items = new ArrayList<>();
    ListView lv;
    listAdapter mAdapter;
    ArrayList<Listitem> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing_json);

        toolbar=(Toolbar)findViewById(R.id.toolbar_spinner);
        setSupportActionBar(toolbar);
        setTitle("수질정보");

        String[] PurificationPlant = {"고령 정수장","고산 정수장","고양 정수장","공주 정수장","구미 정수장","구천 정수장","금산 정수장","덕소 정수장","덕정 정수장","동화 정수장","밀양 정수장","반송 정수장",
                "반월 정수장","별량 정수장","보령 정수장","부안 정수장","사천 정수장","산성 정수장","석성 정수장","성남 정수장","송전 정수장","수지 정수장","시흥 정수장","아산 정수장",
                "양산 정수장","연초 정수장","와부 정수장","운문 정수장","일산 정수장","자인 정수장","천안 정수장","청주 정수장","충주 정수장","평림 정수장","학야 정수장","화순 정수장"};
        final String[] PurificationPlant_code={"387","367","319","357","337","332","372","317","389","385","381","335",
                "311","368","359","360","333","363","351","313","356","316","314","354","382","331","312","340",
                "315","339","355","353","380","386","378","364"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PurificationPlant);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dialogBuilder = new MaterialDialog.Builder(parsingJson.this);
        dialogBuilder.title("데이터 로드중..")
                .content("잠시만 기다려주세요")
                .progress(true, 0);
        mDialog = dialogBuilder.build();


        spinner = (MaterialSpinner)findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                PurificationPlant_choice = PurificationPlant_code[position];
                data.clear();
                new JsonLoadingTask().execute();
                mDialog.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mAdapter = new listAdapter(this,data);
        lv = (ListView)findViewById(R.id.lv);
        mAdapter.notifyDataSetChanged();
        lv.setAdapter(mAdapter);

        new JsonLoadingTask().execute();
    }
    public class Listitem{
        int imgId;
        String category;
        String status;
        String data;
        public Listitem(int imgId, String category, String status, String data){
            this.imgId=imgId;
            this.category=category;
            this.status=status;
            this.data=data;
        }
    }

    public class listAdapter extends BaseAdapter{
        Context context;
        ArrayList<Listitem> list = new ArrayList<>();
        public listAdapter(Context context, ArrayList<Listitem> list){
            super();
            this.context=context;
            this.list=list;
            Log.d("zz", String.valueOf(list.size()));
            Toast.makeText(parsingJson.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Listitem getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = new ViewHolder();

            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);

                viewHolder.number = (ImageView)convertView.findViewById(R.id.number);
                viewHolder.category = (TextView)convertView.findViewById(R.id.category);
                viewHolder.status = (TextView)convertView.findViewById(R.id.status);
                viewHolder.data = (TextView)convertView.findViewById(R.id.data);

                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.number.setImageResource(list.get(position).imgId);
            viewHolder.category.setText(list.get(position).category);
            viewHolder.status.setText(list.get(position).status);
            viewHolder.data.setText(list.get(position).data);

            return convertView;
        }
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
            String line = getStringFromUrl("http://opendata.kwater.or.kr:80/openapi-data/service/pubd/waterways/wdr/dailwater/list?_type=json&fcode="+PurificationPlant_choice+"&stdt=2014-10-01&eddt=2014-10-07&serviceKey=E%2B0%2BhJolGH9ppT7r1hfU18qRRHvxTQOATomUAZ%2BIiHXSQ666uyApIt0sQCdWmGCM%2FlRiQ8wGLHTDr4aG6EyCbQ%3D%3D");
            String line1 = line.substring(96,line.length()-4);
            if(PurificationPlant_choice.equals("337")){
                line1="["+line1+"]";
            }
            /* 넘어오는 데이터 구조 { [ { } ] } JSON 객체 안에 배열안에 내부JSON 객체*/

            JSONArray Array = new JSONArray(line.substring(96,line.length()-4));

            // bodylist 배열안에 내부 JSON 이므로 JSON 내부 객체 생성
            JSONObject insideObject = Array.getJSONObject(0);

            // JSONObject 메소드 ( get.String(), getInt(), getBoolean() .. 등 : 객체로부터 데이터의 타입에 따라 원하는 데이터를 읽는다. )
            data.add(new Listitem(R.drawable.n1, "ph : ", "매우 좋음", insideObject.getString("item4") + "ph"));
            data.add(new Listitem(R.drawable.n2, "탁도 : ", "매우 좋음", insideObject.getString("item5") + "NTU"));
            data.add(new Listitem(R.drawable.n3, "잔류염소 : ", "매우 좋음", insideObject.getString("item6") + "mg/L"));
            data.add(new Listitem(R.drawable.n4, "날짜", "", insideObject.getString("mesurede")));

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
            mAdapter.notifyDataSetChanged();
            mDialog.dismiss();
        } // onPostExecute : 백그라운드 작업이 끝난 후 UI 작업을 진행한다.
    } // JsonLoadingTask
}