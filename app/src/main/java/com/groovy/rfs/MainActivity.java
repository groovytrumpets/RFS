package com.groovy.rfs;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.HashMap;
import java.util.Map;

import com.cloudinary.android.MediaManager;
import com.groovy.rfs.databinding.ActivityMainBinding;
import androidx.core.splashscreen.SplashScreen;
public class MainActivity extends AppCompatActivity {
    private static boolean cloudaryIsCreated = false;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set trang home la mac dinh
        replaceFragment(new HomeFragment());

        //cloudary
if (!cloudaryIsCreated){

            Map config = new HashMap();
            config.put("cloud_name", "dlevje6nq"); // <-- Dán Cloud Name vào đây
            config.put("api_key", "592377633532273");
            config.put("api_secret", "FBTKYhNfNykcSC_9olyP6UoN2Hw");
            MediaManager.init(this, config);
            cloudaryIsCreated=true;
}


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home_navbar) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.search_navbar) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.account_navbar) {
                replaceFragment(new AccountFragment());
            }

            return true;
        });
        EdgeToEdge.enable(this);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
//        fragmentTransaction.commit();

        //sliding animation logic
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        //
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}