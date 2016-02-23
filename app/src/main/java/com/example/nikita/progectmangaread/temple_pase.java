package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classMang;
import com.example.nikita.progectmangaread.fragment.fragmentTemplePase;
import com.example.nikita.progectmangaread.fragment.fragmentSearchAndGenres;

import de.greenrobot.event.EventBus;

/**
 * Класс для топа вывода манг, с помощью ViewPager,
 * происходит пролитывания fragments
 * перед прогрузкой замменять на прогресс бар фрагмент
 * как только спарсит и вставить меняем на норм фразмент ( как провернуть?)
 */

public class temple_pase extends AppCompatActivity {
    private classMang mang;
    private fragmentTemplePase tFragment;
    private ViewPager pager;
    private AdapterPargerFragment gg;
    private MainClassTop classTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_pase);
        //установка слушателя
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new AdapterPargerFragment(getSupportFragmentManager(),2);
        pager.setAdapter(gg);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        if (classTop != null) EventBus.getDefault().post(classTop);
        super.onStop();
    }

    public void onEvent(MainClassTop event){
        Toast toast = Toast.makeText(this,
                event.getURL_characher(), Toast.LENGTH_SHORT);
        toast.show();
        classTop = event;
        Intent intent = new Intent(temple_pase.this,DescriptionMang.class);
        startActivity(intent);
    }

    public void onEvent(classMang event){
        mang = new classMang();
        mang = event;
    }

    public void onClick(View view) {
        
    }


    public class AdapterPargerFragment extends FragmentStatePagerAdapter {
        int kol;
        public AdapterPargerFragment(FragmentManager mgr, int kol) {
            super(mgr);
            this.kol = kol;

        }
        @Override
        public int getCount() {
            return(kol);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return fragmentTemplePase.newInstance(position);
                case 1:
                    return fragmentSearchAndGenres.newInstance(position);
            }
            return null;
        }
    }

}
