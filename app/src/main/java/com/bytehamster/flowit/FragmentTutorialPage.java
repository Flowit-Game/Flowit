package com.bytehamster.flowit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentTutorialPage extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tutorial_page, container, false);
        rootView.findViewById(R.id.tutorial_background).setBackgroundColor(getArguments().getInt("color", 0));
        return rootView;
    }
}
