package com.lucideye.lucideye.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.fragment.app.Fragment;

import com.example.lucideye.R;


import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;


public class GalleryFragment extends Fragment implements View.OnClickListener {


    private ImageView ivBitmap;
    private Spinner spinner;
    private Button backButton;
    private Button saveButton;
    private Button galleryButton;
    private Bitmap bitmap;
    private Bitmap savedBitmap;
    private View RootView;
    private float l, m, s;
    private String selectedFilter = "NORMAL VISION";
    private int currentRedFactor = 0;
    private int currentGreenFactor = 0;
    private int currentBlueFactor = 0;
    private int color;

    private Timer timer;


    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        saveButton = RootView.findViewById(R.id.saveButton);
        galleryButton = RootView.findViewById(R.id.gallery);
        ivBitmap = RootView.findViewById(R.id.ivBitmap);
        backButton = RootView.findViewById(R.id.backbutton);
        spinner = RootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.filterNames, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);


        ivBitmap.setVisibility(VISIBLE);
        saveButton.setVisibility(GONE);

        galleryButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        //Spinner adapter for selecting filters
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.filterNames);
                selectedFilter = choose[selectedItemPosition];
                if (!selectedFilter.equals("NORMAL VISION"))
                    applyFilter(selectedFilter);
                else if (savedBitmap != null) ivBitmap.setImageBitmap(savedBitmap);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startGallery.launch(galleryIntent);


        return RootView;

    }

    //Method for applying a filter to an image from gallery
    public void applyFilter(String selectedFilter) {


        bitmap = savedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int size = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        if (!selectedFilter.equals("NORMAL VISION")) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < pixels.length; i++) {
                    color = pixels[i];
                    int r = color >> 16 & 0xff;
                    int g = color >> 8 & 0xff;
                    int b = color & 0xff;

                    if (selectedFilter.equals("PROTANOPES (RED-BLIND)")) {
                        currentRedFactor = (int) (0.1121f * r + 0.8853f * g - 0.0005f * b);
                        currentGreenFactor = (int) (0.1127f * r + 0.8897f * g - 0.0001f * b);
                        currentBlueFactor = (int) (0.0045f * r + 0.0f * g + 1.0019f * b);
                    } else if (selectedFilter.equals("DEUTERANOPES (GREEN-BLIND)")) {
                        currentRedFactor = (int) (0.292f * r + 0.7054f * g - 0.0003f * b);
                        currentGreenFactor = (int) (0.2934f * r + 0.7089f * g + 0.0000f * b);
                        currentBlueFactor = (int) (-0.02098f * r + 0.02559f * g + 1.0019f * b);
                    } else if (selectedFilter.equals("TRITANOPES (BLUE-BLIND)")) {
                        l = 0.0000946908f * r + 0.00019291f * g + 0.00001403f * b;
                        m = 0.000046877f * r + 0.00022862f * g + 0.000026153f * b;
                        s = 0.00000539278f * r + 0.0002595562f * g + 0.00003666445f * b;


                        currentRedFactor = (int) (18140.343f * l - 15387f * m + 562.224f * s);
                        currentGreenFactor = (int) (-3730.038f * l + 7601.859f * m - 556.589f * s);
                        currentBlueFactor = (int) (98.787f * l - 640.127f * m + 3856.903f * s);
                    } else if ((selectedFilter.equals("PROTANOPES ASSISTANCE")) || (selectedFilter.equals("DEUTERANOPES ASSISTANCE")) || (selectedFilter.equals("TRITANOPES ASSISTANCE"))) {
                        double var_R = (r / 255.0);
                        double var_G = (g / 255.0);
                        double var_B = (b / 255.0);

                        double var_Min = Math.min(var_R, Math.min(var_G, var_B));
                        double var_Max = Math.max(var_R, Math.max(var_G, var_B));
                        double del_Max = var_Max - var_Min;

                        double V = var_Max;
                        double H = 0;
                        double S = 0;
                        if (del_Max == 0) {
                            H = 0;
                            S = 0;
                        } else {
                            S = del_Max / var_Max;

                            double del_R = (((var_Max - var_R) / 6) + (del_Max / 2)) / del_Max;
                            double del_G = (((var_Max - var_G) / 6) + (del_Max / 2)) / del_Max;
                            double del_B = (((var_Max - var_B) / 6) + (del_Max / 2)) / del_Max;

                            if (var_R == var_Max) H = del_B - del_G;
                            else if (var_G == var_Max) H = (1.0 / 3.0) + del_R - del_B;
                            else if (var_B == var_Max) H = (2.0 / 3.0) + del_G - del_R;

                            if (H < 0) H = 0;
                            if (H > 1) H -= 1;
                        }
                        if (selectedFilter.equals("TRITANOPES ASSISTANCE"))
                            H = H + 0.1;
                        else if (selectedFilter.equals("DEUTERANOPES ASSISTANCE"))
                            H = H + 0.25;
                        else H = H + 0.3;
                        if (H < 0) H += 1;
                        if (H > 1) H -= 1;


                        if (S == 0) {

                            currentRedFactor = (int) (V * 255);
                            currentGreenFactor = (int) (V * 255);
                            currentBlueFactor = (int) (V * 255);

                        } else {
                            double var_h = H * 6;
                            if (var_h == 6) var_h = 0;
                            int var_i = (int) var_h;
                            double var_1 = V * (1 - S);
                            double var_2 = V * (1 - S * (var_h - var_i));
                            double var_3 = V * (1 - S * (1 - (var_h - var_i)));
                            double var_r, var_g, var_b;
                            if (var_i == 0) {
                                var_r = V;
                                var_g = var_3;
                                var_b = var_1;
                            } else if (var_i == 1) {
                                var_r = var_2;
                                var_g = V;
                                var_b = var_1;
                            } else if (var_i == 2) {
                                var_r = var_1;
                                var_g = V;
                                var_b = var_3;
                            } else if (var_i == 3) {
                                var_r = var_1;
                                var_g = var_2;
                                var_b = V;
                            } else if (var_i == 4) {
                                var_r = var_3;
                                var_g = var_1;
                                var_b = V;
                            } else {
                                var_r = V;
                                var_g = var_1;
                                var_b = var_2;
                            }

                            currentRedFactor = (int) (var_r * 255);
                            currentGreenFactor = (int) (var_g * 255);
                            currentBlueFactor = (int) (var_b * 255);
                        }
                    }


                    if (currentRedFactor < 0) currentRedFactor = 0;
                    if (currentGreenFactor < 0) currentGreenFactor = 0;
                    if (currentBlueFactor < 0) currentBlueFactor = 0;

                    if (currentRedFactor > 255) currentRedFactor = 255;
                    if (currentGreenFactor > 255) currentGreenFactor = 255;
                    if (currentBlueFactor > 255) currentBlueFactor = 255;
                    pixels[i] = 0xff000000 | currentRedFactor << 16 | currentGreenFactor << 8 | currentBlueFactor;
                }

            });
            thread.start();

            //Waiting for the calculation to finish
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Setting the new bitmap
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());

        ivBitmap.setImageBitmap(bitmap);


    }


    //Opening the gallery
    ActivityResultLauncher<Intent> startGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImage = data.getData();
                        try {
                            savedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), selectedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        spinner.setSelection(0);
                        saveButton.setVisibility(VISIBLE);
                        ivBitmap.setImageBitmap(savedBitmap);

                    }
                }


            });


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.saveButton:
                MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "LucidEye" + Calendar.getInstance().getTime(), "");
                Toast.makeText(getActivity(), getString(R.string.saved_to_gallery), Toast.LENGTH_LONG).show();
                break;

            case R.id.gallery:

                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startGallery.launch(galleryIntent);

                break;

            case R.id.backbutton:
                getParentFragmentManager().popBackStack();
                break;
        }
    }


}
