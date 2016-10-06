package com.example.nikita.progectmangaread.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.fragment.fragmentBookmark;

import java.util.ArrayList;

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
        getLayoutInflater().inflate(R.layout.page_view, frameLayout);
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