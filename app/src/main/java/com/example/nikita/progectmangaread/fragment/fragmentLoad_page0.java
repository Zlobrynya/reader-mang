package com.example.nikita.progectmangaread.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.example.nikita.progectmangaread.classPMR.ClassTransport;
import com.example.nikita.progectmangaread.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 25.02.2016.
 */
public class fragmentLoad_page0 extends Fragment{
    public ClassTransport transport;
    private final String PROBLEM = "ProblemTime";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    /* Inflate the layout for this ffragment */
        View view = inflater.inflate(R.layout.fragment_load, container, false);
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_load, new fragmentTopMang());
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

     public void onEvent(ClassTransport event) {
        if (event != null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
		/*
		 * When this container ffragment is created, we fill it with our first
		 * "real" ffragment
		 */
            fragmentTopMang frag = new fragmentTopMang();
            frag.add(event);
            transaction.replace(R.id.fragment_load, frag);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
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
