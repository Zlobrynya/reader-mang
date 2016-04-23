package com.example.nikita.progectmangaread.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikita.progectmangaread.classPMR.classTransport;
import com.example.nikita.progectmangaread.fragment.fragmentTemplePase;
import com.example.nikita.progectmangaread.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 25.02.2016.
 */
public class fragmentLoad_page0 extends Fragment{
    public classTransport transport;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_load, container, false);
        //Сделать что бы прошлый фрагмент поиска удалялся?
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_load, new fragmentTemplePase());
        transaction.commit();
        return view;
    }

    public static fragmentLoad_page0 newInstance(int page) {
        fragmentLoad_page0 fragment = new fragmentLoad_page0();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

     public void onEvent(classTransport event) {
        if (event != null){
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */
            fragmentTemplePase frag = new fragmentTemplePase();
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
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
