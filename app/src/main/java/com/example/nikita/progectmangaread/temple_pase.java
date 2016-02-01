package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ViewFlipper;

import de.greenrobot.event.EventBus;

/**
 * Класс для топа вывода манг, с помощью ViewPager,
 * происходит пролитывания fragments
 */

public class temple_pase extends AppCompatActivity {
    private classMang mang;
    private fragmentTemplePase tFragment;
    private ViewPager pager;
    private AdapterPargerFragment gg;
    private int numberPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_pase);
        //установка слушателя
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new AdapterPargerFragment(getSupportFragmentManager(),10);
        pager.setAdapter(gg);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                mang.editWhere(position);
                EventBus.getDefault().post(mang);
            }
        });
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

    public void onEvent(MainClassTop event){
        Toast toast = Toast.makeText(this,
                event.getURL_characher(), Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(temple_pase.this,DescriptionMang.class);
        startActivity(intent);
    }

    public void onEvent(classMang event){
        mang = new classMang();
        mang = event;
    }

}
