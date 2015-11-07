package com.example.hwanik.water;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by hwanik on 2015. 10. 10..
 */
public class BaseActivity extends Application {
    @Override
    public void onCreate() {
        Parse.initialize(this, "Zkc49yMNCkGOlDxjsrOVhNWOqSpabFIxohQLoNd3", "lWU2pMiQ2o3hcyFudIRWGhc34l6kBtmNPw2UbSpi");
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/NanumBarunGothic.ttf");
        // font from assets: "assets/fonts/~.ttf,  원하는 폰트 변경
        // SERIF는 안드로이드에서 기본적으로 제공되는 폰트 이름인 것 같은데 임의의 이름으로 변경하려 했으나 에러가 뜬다..
    }
}
