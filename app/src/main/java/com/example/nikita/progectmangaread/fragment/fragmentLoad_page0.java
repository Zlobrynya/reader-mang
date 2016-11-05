package com.example.nikita.progectmangaread.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.example.nikita.progectmangaread.classPMR.ClassTransport;
import com.example.nikita.progectmangaread.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Nikita on 25.02.2016.
 */
public class fragmentLoad_page0 extends Fragment{
    private final String PROBLEM = "ProblemTime";
    fragmentTopMang topMang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    /* Inflate the layout for this ffragment */
        View view = inflater.inflate(R.layout.fragment_load, container, false);
        try {
            topMang = new fragmentTopMang();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_load,topMang,"TopManga");
            transaction.commit();
        }catch (IllegalStateException e){
            Crashlytics.logException(e);
        }
        return view;
    }

    public static fragmentLoad_page0 newInstance(int page) {
        fragmentLoad_page0 fragment = new fragmentLoad_page0();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
     public void onEvent(ClassTransport event) {
        if (event != null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            transaction.replace(R.id.fragment_load, frag,"SearchAndGenres");
            fragmentTopMang frag = (fragmentTopMang) fragmentManager.findFragmentByTag("SearchAndGenres");
            if (frag == null){
                frag = new fragmentTopMang();
            }
            frag.clearData();
            frag.add(event);
            transaction.replace(R.id.fragment_load, frag,"SearchAndGenres");
          //  transaction.addToBackStack(null);
          //  transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (fragmentManager.getBackStackEntryCount() == 0) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
     }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(PROBLEM, "Stop fragment Top Manga");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
