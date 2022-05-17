package com.lucideye.lucideye.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucideye.R;


public class MenuFragment extends Fragment implements View.OnClickListener {


    private Button startCamera;
    private TextView startTutorial;
    private ImageView eye;
    private int[] colorArr;
    private Thread myThread;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eye = getActivity().findViewById(R.id.eye);
        startTutorial = getActivity().findViewById(R.id.startTutorial);
        startCamera = getActivity().findViewById(R.id.startCamera);
        startCamera.setOnClickListener(this);
        startTutorial.setOnClickListener(this);
        colorSwap();

    }

    //Method for the switching eye gradient
    public void colorSwap() {
        colorArr = new int[6];
        colorArr[0] = 192;
        colorArr[1] = 57;
        colorArr[2] = 43;
        colorArr[3] = 142;
        colorArr[4] = 68;
        colorArr[5] = 173;
        boolean[] goingUp = new boolean[6];
        for (int i = 0; i < 6; i++)
            if (i % 2 == 0) goingUp[i] = true;
            else goingUp[i] = false;


        //All the calculations for gradient are calculated in another thread
        myThread = new Thread() {
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < 6; i++) {
                        if (goingUp[i]) colorArr[i] += Math.random() * 3;
                        else colorArr[i] -= Math.random() * 3;

                        if (colorArr[i] < 50) goingUp[i] = true;
                        if (colorArr[i] > 215) goingUp[i] = false;
                    }

                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.BL_TR,
                            new int[]{0xff000000 | colorArr[0] << 16 | colorArr[1] << 8 | colorArr[2], 0xff000000 | colorArr[3] << 16 | colorArr[4] << 8 | colorArr[5]});
                    gd.setCornerRadius(1000f);

                    //Only the thread that created the view can change it
                    requireActivity().runOnUiThread(() -> eye.setBackground(gd));

                }
            }
        };
        myThread.start();

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.startCamera) {
            Fragment cameraFrament = new CameraFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right, R.animator.slide_in_left, R.animator.slide_in_right);
            transaction.replace(R.id.fragment_container_view, cameraFrament, null);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (view.getId() == R.id.startTutorial) {
            Fragment tutorialFrament = new TutorialFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right, R.animator.slide_in_left, R.animator.slide_in_right);
            transaction.replace(R.id.fragment_container_view, tutorialFrament, null);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }
}
