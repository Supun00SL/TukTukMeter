package com.example.supunmadushanka.tuktukmeter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Supun Madushanka on 2/20/2018.
 */
public class historyfragment extends Fragment {

    Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.history_fragment, container, false);

        context = this.getActivity();

        return view;
    }
}
