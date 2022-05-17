package com.lucideye.lucideye.fragment;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import androidx.annotation.NonNull;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;


import android.view.ContentInfo;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lucideye.R;
import com.lucideye.lucideye.filter.AssistanceFilter;
import com.lucideye.lucideye.filter.DeuteranopesSimFilter;
import com.lucideye.lucideye.filter.ProtanopesSimFilter;
import com.lucideye.lucideye.filter.TritanopesSimFilter;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.filter.NoFilter;


import java.net.ConnectException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class CameraFragment extends Fragment implements View.OnClickListener {

    private CameraView camera;
    private CameraListener cameraListener;

    private ImageView ivBitmap;
    private ImageView crosshair;
    private Spinner spinner;
    private Button colorPickerButton;
    private Button galleryButton;
    private Button btnCapture, btnOk, btnCancel;
    private Bitmap bitmap;
    private Bitmap savedBitmap;
    private TextView colorText;
    private View RootView;
    private float l, m, s;
    private String selectedFilter = "NORMAL VISION";
    private int currentRedFactor = 0;
    private int currentGreenFactor = 0;
    private int currentBlueFactor = 0;
    private int color;

    private boolean takingPhoto = false;
    private boolean pickingColors = false;
    private Timer timer;


    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_camera, container, false);
        btnCapture = RootView.findViewById(R.id.btnCapture);
        btnOk = RootView.findViewById(R.id.btn_accept);
        btnCancel = RootView.findViewById(R.id.btn_reject);
        colorText = RootView.findViewById(R.id.colorName);
        crosshair = RootView.findViewById(R.id.crosshair);
        galleryButton = RootView.findViewById(R.id.gallery);
        ivBitmap = RootView.findViewById(R.id.ivBitmap);
        colorPickerButton = RootView.findViewById(R.id.colorPicker);
        spinner = RootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.filterNames, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        camera = RootView.findViewById(R.id.camera);
        camera.setLifecycleOwner(getViewLifecycleOwner());


        btnCapture.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        colorPickerButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);

        ivBitmap.setVisibility(VISIBLE);
        colorText.setVisibility(GONE);
        crosshair.setVisibility(GONE);


        //Initializing the filters
        ProtanopesSimFilter protanopesSimFilter = new ProtanopesSimFilter();
        DeuteranopesSimFilter deuteranopesSimFilter = new DeuteranopesSimFilter();
        TritanopesSimFilter tritanopesSimFilter = new TritanopesSimFilter();
        AssistanceFilter protanopesAssistanceFilter = new AssistanceFilter("PROTANOPES");
        AssistanceFilter deuteranopesAssistanceFilter = new AssistanceFilter("DEUTERANOPES");
        AssistanceFilter tritanopesAssistanceFilter = new AssistanceFilter("TRITANOPES");


        //Spinner adapter for selecting filters
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.filterNames);
                selectedFilter = choose[selectedItemPosition];

                if (selectedFilter.equals("PROTANOPES (RED-BLIND)"))
                    camera.setFilter(protanopesSimFilter);
                else if (selectedFilter.equals("DEUTERANOPES (GREEN-BLIND)"))
                    camera.setFilter(deuteranopesSimFilter);
                else if (selectedFilter.equals("TRITANOPES (BLUE-BLIND)"))
                    camera.setFilter(tritanopesSimFilter);
                else if (selectedFilter.equals("PROTANOPES ASSISTANCE"))
                    camera.setFilter(protanopesAssistanceFilter);
                else if (selectedFilter.equals("DEUTERANOPES ASSISTANCE"))
                    camera.setFilter(deuteranopesAssistanceFilter);
                else if (selectedFilter.equals("TRITANOPES ASSISTANCE"))
                    camera.setFilter(tritanopesAssistanceFilter);
                else {
                    camera.setFilter(new NoFilter());
                if (!takingPhoto)  colorPickerButton.setVisibility(VISIBLE);
                }

                if (takingPhoto) applyFilter(selectedFilter);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Camera events listener
        cameraListener = new CameraListener() {

            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                byte[] arr = result.getData();
                savedBitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                //if picture was taken by capturing photo...
                if (takingPhoto) {
                    applyFilter(selectedFilter);
                    camera.setVisibility(GONE);
                    ivBitmap.setImageBitmap(bitmap);
                    ivBitmap.setVisibility(VISIBLE);
                    colorPickerButton.setVisibility(GONE);
                    galleryButton.setVisibility(GONE);
                    showAcceptedRejectedButton(true);


                }
                //or if it was captured for colorpicker...
                else if (pickingColors) {
                    int centerColor = savedBitmap.getPixel(savedBitmap.getWidth() / 2, savedBitmap.getHeight() / 2);
                    String colorName = "";
                    int mainColor = 0;
                    int redC = centerColor >> 16 & 0xff;
                    int greenC = centerColor >> 8 & 0xff;
                    int blueC = centerColor & 0xff;

                    //color identifier
                    if ((redC / 64 > greenC / 64) && (redC / 64 > blueC / 64)) {
                        mainColor = redC;
                        colorName = "RED";
                    } else if ((greenC / 64 > redC / 64) && (greenC / 64 > blueC / 64)) {
                        mainColor = greenC;
                        colorName = "GREEN";
                    } else if ((blueC / 64 > redC / 64) && (blueC / 64 > greenC / 64)) {
                        mainColor = blueC;
                        colorName = "BLUE";
                    } else if ((redC / 64 == greenC / 64) && (redC / 64 == blueC / 64)) {
                        mainColor = redC;
                        if (mainColor / 64 < 1) colorName = "BLACK";
                        else if (mainColor / 64 < 2) colorName = "GREY";
                        else colorName = "WHITE";
                    } else if ((redC / 64 == greenC / 64)) {
                        mainColor = redC;
                        colorName = "YELLOW";
                    } else if ((redC / 64 == blueC / 64)) {
                        mainColor = redC;
                        colorName = "PURPLE";
                    } else if ((greenC / 64 == blueC / 64)) {
                        mainColor = greenC;
                        colorName = "TURQUOISE";
                    }
                    if (!((redC / 64 == greenC / 64) && (redC / 64 == blueC / 64))) {
                        if (mainColor / 64 == 1) colorName = "DARK ".concat(colorName);
                    }
                    colorText.setText(colorName);
                }
            }

        };
        camera.addCameraListener(cameraListener);

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


    //setting the buttons display when capturing the image
    private void showAcceptedRejectedButton(boolean acceptedRejected) {
        if (acceptedRejected) {
            btnOk.setVisibility(VISIBLE);
            btnCancel.setVisibility(VISIBLE);
            btnCapture.setVisibility(GONE);
            colorPickerButton.setVisibility(GONE);

        } else {
            takingPhoto=false;
            btnCapture.setVisibility(VISIBLE);
            btnOk.setVisibility(GONE);
            btnCancel.setVisibility(GONE);
            galleryButton.setVisibility(VISIBLE);
            camera.setVisibility(VISIBLE);
            ivBitmap.setVisibility(INVISIBLE);
            colorPickerButton.setVisibility(VISIBLE);
        }

    }

    //Color picker logic
    void colorPicker(boolean pickingColors) {
        if (timer != null) timer.cancel();
        timer = new Timer();

        if (!pickingColors) {
            timer.cancel();
            colorText.setVisibility(GONE);
            crosshair.setVisibility(GONE);
        } else {
            colorText.setVisibility(VISIBLE);
            crosshair.setVisibility(VISIBLE);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!takingPhoto) {
                        if (selectedFilter.equals("NORMAL VISION")) camera.takePictureSnapshot();
                        else camera.takePicture();
                    }
                }
            }, 0, 1500);
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCapture:
                if (pickingColors) {
                    pickingColors = false;
                    colorPicker(pickingColors);
                }
                takingPhoto = true;
                camera.takePicture();
                break;
            case R.id.colorPicker:
                pickingColors = !pickingColors;
                colorPicker(pickingColors);
                break;
            case R.id.btn_reject:
                showAcceptedRejectedButton(false);
                break;

            case R.id.btn_accept:

                MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "Vibrant" + Calendar.getInstance().getTime(), "");
                Toast.makeText(getActivity(), getString(R.string.saved_to_gallery), Toast.LENGTH_LONG).show();
                showAcceptedRejectedButton(false);
                break;

            case R.id.gallery:
                if (pickingColors) {
                    pickingColors = false;
                    colorPicker(pickingColors);
                }

                Fragment galleryFragment = new GalleryFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_view, galleryFragment, null);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

        }
    }

}
