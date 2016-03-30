package com.example.nikita.progectmangaread;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classMang;
import com.example.nikita.progectmangaread.classPMR.classTransport;
import com.example.nikita.progectmangaread.fragment.fragmentLoad_page0;
import com.example.nikita.progectmangaread.fragment.fragmentGenres;
import com.example.nikita.progectmangaread.fragment.fragmentSearchAndGenres;
import com.example.nikita.progectmangaread.AdapterPMR.AdapterSlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import de.greenrobot.event.EventBus;

/**
 * Класс для топа вывода манг, с помощью ViewPager,
 * происходит пролитывания fragments
 *
 *
 */

public class temple_pase extends AppCompatActivity {
    private classMang mang;
    private ViewPager pager;
    private AdapterPargerFragment gg;
    private MainClassTop classTop;
    private SlidingMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_pase);
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new AdapterPargerFragment(getSupportFragmentManager(),3);
        pager.setAdapter(gg);
        pager.setCurrentItem(1);

        // <------------------------------------------------------->
        //      Sliding Menu
        final Context context = this;
        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.sliding_menu, null, true);
        final ListView lv=(ListView) v.findViewById(R.id.listView);

        menu = new SlidingMenu(this);
        AdapterSlidingMenu ma = new AdapterSlidingMenu(context);
        lv.setAdapter(ma);

        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.1f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(v);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                          SlidingMenuConstant constant = null;
                                          switch (position){
                                              case 0: constant = SlidingMenuConstant.LIST_MANG;
                                                  break;
                                              case 1: constant = SlidingMenuConstant.FAVORITES;
                                                  break;
                                              case 2: constant = SlidingMenuConstant.DOWLAND;
                                                  break;
                                              default:
                                                  constant = SlidingMenuConstant.MAGIС;
                                          }
                                          EventBus.getDefault().post(constant);
                                      }
                                  }

        );

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // <-------------------------------------------------------------------------->

    }

    //Метод для открытия бокового меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {  // узнаем ID нажатой кнопки
            case android.R.id.home: // если это кнопка-иконка ActionBar,
                menu.toggle(true);        // открываем меню (или закрываем)
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void onEvent(classTransport event) {
        pager.setCurrentItem(1);
    }

    public class AdapterPargerFragment extends FragmentPagerAdapter  {
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
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Жанры";
                case 1:
                    return "Манга";
                case 2:
                    return "Поиск";
                default:
                    return "Magic";
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return fragmentGenres.newInstance(position);
                case 1:
                    return fragmentLoad_page0.newInstance(position);
                case 2:
                    return fragmentSearchAndGenres.newInstance(position);
            }
            return null;
        }
    }

}
