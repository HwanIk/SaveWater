package com.proj.hwanik.water;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.proj.hwanik.water.R;
import com.squareup.picasso.Picasso;

public class DetailAd extends AppCompatActivity {
    MaterialDialog.Builder dialogBuilder;
    MaterialDialog mDialog;

    Toolbar toolbar;
    String t;
    String c;
    String adImg;
    TextView title;
    TextView content;
    ImageView ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_ad);
        toolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(4);
        setTitle("물 정보");

        Intent intent = getIntent();
        t=intent.getExtras().getString("title");
        adImg=intent.getExtras().getString("ImgUrl");
//        c=intent.getExtras().getString("content");

        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        ad=(ImageView)findViewById(R.id.ad);

        dialogBuilder = new MaterialDialog.Builder(DetailAd.this);
        dialogBuilder.title("데이터 로드중..")
                .content("잠시만 기다려주세요")
                .progress(true, 0);
        mDialog = dialogBuilder.build();
        mDialog.show();

        Picasso.with(getApplicationContext())
                .load(adImg)
                .into(ad);

        mDialog.dismiss();
//        title.setText(t);
//        content.setText(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_ad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
