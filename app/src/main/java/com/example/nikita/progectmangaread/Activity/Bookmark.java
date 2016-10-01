package com.example.nikita.progectmangaread.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterBookmark;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseListMang;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassMainTop;
import com.example.nikita.progectmangaread.classPMR.ClassRecentlyRead;
import com.example.nikita.progectmangaread.fragment.fragmentBookmark;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionMang;

import java.util.ArrayList;
import java.util.StringTokenizer;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 09.05.2016.
 */
public class Bookmark extends BaseActivity {
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private ArrayList<String> nameSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nameSite = new ArrayList<>();
        getLayoutInflater().inflate(R.layout.activity_top_mang, frameLayout);
        classDataBaseViewedHead = new ClassDataBaseViewedHead(this);
        int count = getCountSite();
        if (count > 0){
            adapterFragment adapterPargerFragment = new adapterFragment(getSupportFragmentManager(), count);
            ViewPager pager = (ViewPager) this.findViewById(R.id.pager);
            pager.setAdapter(adapterPargerFragment);
        }
    }

    private int getCountSite(){
        int count = 0;
        if (classDataBaseViewedHead.whetherThereIsABookmarkInSite("readmanga")) {
            nameSite.add("readmanga");
            count++;
        }
        if (classDataBaseViewedHead.whetherThereIsABookmarkInSite("mintmanga")) {
            nameSite.add("mintmanga");
            count++;
        }
        return count;
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    public class adapterFragment  extends FragmentPagerAdapter {
        int kol;

        adapterFragment(FragmentManager mgr, int kol) {
            super(mgr);
            this.kol = kol;
        }
        @Override
        public int getCount() {
            return(kol);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (nameSite.size() > position){
                return nameSite.get(position);
            }else return "Magic";
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentBookmark.newInstance(nameSite.get(position));
        }
    }

}