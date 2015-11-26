package com.proj.hwanik.water;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseFile;

/**
 * Created by hwanik on 2015. 11. 22..
 */
class CustomPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    String[] adImg;
    int numOfAd;
    String[] titleOfAd;
//    String[] contentOfAd;

    public CustomPagerAdapter(Context context, int numOfAd, String[] titleOfAd, String[] adImg) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.numOfAd=numOfAd;
        this.titleOfAd=titleOfAd;
        this.adImg=adImg;
//        this.contentOfAd=contentsOfAd;
    }

    @Override
    public int getCount() {
        return numOfAd;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        TextView tv = (TextView)itemView.findViewById(R.id.tv);
        tv.setText(titleOfAd[position]);
        container.addView(itemView);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailAd.class);
                intent.putExtra("title",titleOfAd[position]);
                intent.putExtra("ImgUrl",adImg[position]);
//                intent.putExtra("content",contentOfAd[position]);
                mContext.startActivity(intent);
            }
        });

        return itemView;
    }
    //        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
//        imageView.setImageResource(mResources[position]);
//
//        container.addView(itemView);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext, DetailAd.class);
//                intent.putExtra("resId",mResources[position]);
//                mContext.startActivity(intent);
//            }
//        });
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}