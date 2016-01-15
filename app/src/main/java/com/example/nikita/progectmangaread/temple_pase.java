package com.example.nikita.progectmangaread;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.greenrobot.event.EventBus;

public class temple_pase extends AppCompatActivity {
    private classMang mang;
    private fragmentTemplePase tFragment;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_pase);
        tFragment = new fragmentTemplePase();
        ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frgmCont, tFragment);
        ft.commit();
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
        tFragment.editTemplePase(mang);
        ft.replace(0,tFragment);
    }
}
