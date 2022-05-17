package com.lucideye.lucideye.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucideye.R;


public class TutorialFragment extends Fragment {
    private View RootView;
    private String[] lines = new String[6];
    private TextView[] textViews = new TextView[6];
    private TextView[] imTextViews = new TextView[6];
    private ImageView[] imageViews = new ImageView[8];
    private Button continueBtn;
    private Button returnBtn;
    private short page;


    public TutorialFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_tutorial, container, false);


        textViews[0] = RootView.findViewById(R.id.upperText);
        textViews[1] = RootView.findViewById(R.id.middleText);
        textViews[2] = RootView.findViewById(R.id.lowerText);
        textViews[3] = RootView.findViewById(R.id.upperText2);
        textViews[4] = RootView.findViewById(R.id.middleText2);
        textViews[5] = RootView.findViewById(R.id.middleText3);
        imTextViews[0] = RootView.findViewById(R.id.tv_image_left);
        imTextViews[1] = RootView.findViewById(R.id.tv_image_middle);
        imTextViews[2] = RootView.findViewById(R.id.tv_image_right);
        imTextViews[3] = RootView.findViewById(R.id.tv_image_left2);
        imTextViews[4] = RootView.findViewById(R.id.tv_image_middle2);
        imTextViews[5] = RootView.findViewById(R.id.tv_image_right2);
        imageViews[0] = RootView.findViewById(R.id.image_left);
        imageViews[1] = RootView.findViewById(R.id.image_middle);
        imageViews[2] = RootView.findViewById(R.id.image_right);
        imageViews[3] = RootView.findViewById(R.id.image_left2);
        imageViews[4] = RootView.findViewById(R.id.image_middle2);
        imageViews[5] = RootView.findViewById(R.id.image_right2);
        imageViews[6] = RootView.findViewById(R.id.color_picker_1);
        imageViews[7] = RootView.findViewById(R.id.color_picker_2);
        continueBtn = RootView.findViewById(R.id.continue_btn);
        returnBtn = RootView.findViewById(R.id.return_btn);

        page = 1;
        setLines();

        for (TextView tv : textViews) tv.setAlpha(0f);
        for (TextView tv : imTextViews) tv.setAlpha(0f);
        for (ImageView iv : imageViews) iv.setAlpha(0f);
        continueBtn.setAlpha(0f);
        returnBtn.setAlpha(0f);
        returnBtn.setVisibility(GONE);


        continueBtn.setOnClickListener(v -> {
            if (page == 1) page = 2;
            else if (page == 2) {
                page = 3;
                returnBtn.setVisibility(VISIBLE);
                continueBtn.setVisibility(GONE);


            }
        });


        returnBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());


        //The key thread that will play the animations and switch pages
        Thread thread = new Thread(() -> {
            try {
                textViews[0].animate().alpha(1.0f).setDuration(2000).setListener(null);
                Thread.sleep(1500);
                textViews[1].animate().alpha(1.0f).setDuration(2000).setListener(null);
                Thread.sleep(1500);


                for (int i = 0; i < 3; i++) {
                    imageViews[i].animate().alpha(1.0f).setDuration(2000).setListener(null);
                    imTextViews[i].animate().alpha(1.0f).setDuration(2000).setListener(null);
                }

                textViews[2].animate().alpha(1.0f).setDuration(2000).setListener(null);
                Thread.sleep(1000);


                continueBtn.animate().alpha(1.0f).setDuration(500).setListener(null);
                while (page != 2) {
                }
                Thread.sleep(500);

                requireActivity().runOnUiThread(() -> {
                    for (int i = 0; i < 3; i++) {
                        textViews[i].animate().alpha(0.0f).setDuration(1000).setListener(null);
                        imageViews[i].animate().alpha(0.0f).setDuration(1000).setListener(null);
                        imTextViews[i].animate().alpha(0.0f).setDuration(1000).setListener(null);

                        continueBtn.animate().alpha(0.0f).setDuration(1000).setListener(null);
                    }
                });


                Thread.sleep(1500);

                textViews[3].animate().alpha(1.0f).setDuration(2000).setListener(null);
                Thread.sleep(1500);
                textViews[4].animate().alpha(1.0f).setDuration(2000).setListener(null);
                Thread.sleep(1500);

                for (int i = 3; i < 6; i++) {
                    imageViews[i].animate().alpha(1.0f).setDuration(2000).setListener(null);
                    imTextViews[i].animate().alpha(1.0f).setDuration(2000).setListener(null);
                }

                Thread.sleep(1000);
                continueBtn.animate().alpha(1.0f).setDuration(500).setListener(null);
                while (page != 3) {
                }
                Thread.sleep(250);

                requireActivity().runOnUiThread(() -> {
                    for (int i = 3; i < 6; i++) {
                        textViews[i].animate().alpha(0.0f).setDuration(1000).setListener(null);
                        imageViews[i].animate().alpha(0.0f).setDuration(1000).setListener(null);
                        imTextViews[i].animate().alpha(0.0f).setDuration(1000).setListener(null);
                    }
                });


                Thread.sleep(1500);
                textViews[5].animate().alpha(1.0f).setDuration(2000).setListener(null);
                Thread.sleep(1500);
                imageViews[6].animate().alpha(1.0f).setDuration(2000).setListener(null);
                imageViews[7].animate().alpha(1.0f).setDuration(2000).setListener(null);
                Thread.sleep(1000);
                returnBtn.animate().alpha(1.0f).setDuration(500).setListener(null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        thread.start();


        return RootView;
    }

    //Setting the lines for the textviews
    void setLines() {
        lines[0] = getString(R.string.tutorial_page1_line1);
        lines[1] = getString(R.string.tutorial_page1_line2);
        lines[2] = getString(R.string.tutorial_page1_line3);
        lines[3] = getString(R.string.tutorial_page2_line1);
        lines[4] = getString(R.string.tutorial_page2_line2);
        lines[5] = getString(R.string.tutorial_page3_line1);

        textViews[0].setText(lines[0]);
        textViews[1].setText(lines[1]);
        textViews[2].setText(lines[2]);
        textViews[3].setText(lines[3]);
        textViews[4].setText(lines[4]);
        textViews[5].setText(lines[5]);
    }
}