package com.lucideye.lucideye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.lucideye.R;
import com.lucideye.lucideye.fragment.MenuFragment;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 101;
    public static final int GET_FROM_GALLERY = 3;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Fragment menuFragment = new MenuFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right, R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.fragment_container_view, menuFragment, null);
        transaction.commit();


        //Checking for permissions (Camera, READ/WRITE) if not all are provided
        if (!allPermissionsGranted())
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);


    }


    //Checking if all permissions are granted
    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    //Re-asking for permissions if not all are granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permissions_not_granted), Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    //logic for going back from fragments
    @Override
    public void onBackPressed() {


        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }


}