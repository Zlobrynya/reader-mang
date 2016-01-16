package com.example.nikita.progectmangaread;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import de.greenrobot.event.EventBus;

public class temple_pase extends AppCompatActivity {
    private classMang mang;
    private fragmentTemplePase tFragment;
    private ViewPager pager;
    private AdapterPargerFragment gg;
    //private FragmentTransaction ft;
    private ViewFlipper flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_pase);
        //tFragment = new fragmentTemplePase();
        //установка слушателя
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new AdapterPargerFragment(getSupportFragmentManager());
        pager.setAdapter(gg);
        tFragment = (fragmentTemplePase) gg.getItem(0);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                editPage(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void editPage(int position) {
        tFragment = (fragmentTemplePase) gg.getItem(position);
        tFragment.editTemplePase(mang);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(classMang event){
        mang = new classMang();
        mang = event;
        tFragment = (fragmentTemplePase) gg.getItem(0);
        tFragment.editTemplePase(mang);
    }
}
