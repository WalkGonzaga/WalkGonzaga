package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Main Activity that controls all that goes on in the app
 */
public class MainActivity extends AppCompatActivity  {

    final private String TAG = "MAINACTIVITY: ";

    /**
     * Listener for the bottom navigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /**
         * Sets all of the navigation buttons and loads the fragments for each button
         * @param item
         * @return true or false
         */
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                /**
                 * Home Button
                 */
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;

                /**
                 * Visited Button
                 */
                case R.id.navigation_gallery:
                    fragment = new GalleryFragment();
                    loadFragment(fragment);
                    return true;

                /**
                 * Steps Button
                 */
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;

                /**
                 * Instructions Button
                 */
                case R.id.navigation_instructions:
                    fragment = new InstructionsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    /**
     * Creates actions for this Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Initiates bottom navigation
         */
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);

        Log.d(TAG, "onCreate: " );

        if (savedInstanceState != null){
            Log.d(TAG, "onCreate: " + savedInstanceState.toString());
            Bundle extras = getIntent().getExtras();
            if (extras.getString("dormVisited") == null) {
                Log.d(TAG, "onCreate: " + "dormVisited was null");
                loadFragment(new HomeFragment());
            }
            GalleryFragment galleryFragment = new GalleryFragment();
            galleryFragment.setArguments(extras);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, galleryFragment);
            transaction.commit();
        }
        else {
            loadFragment(new HomeFragment());
        }
        getSupportActionBar().hide();
    }

    /**
     * gets gallery
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ONRESUME");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Log.d(TAG, "onResume: " + bundle.getString("dormVisited"));
            if (bundle.getString("dormVisited") == null){
                loadFragment(new HomeFragment());
                return;
            }
            Log.d(TAG, "bundle: " + bundle.getString("dormVisited"));
            Bundle extras = getIntent().getExtras();
            GalleryFragment galleryFragment = new GalleryFragment();
            galleryFragment.setArguments(extras);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, galleryFragment);
            transaction.commit();
        }
        else {
            loadFragment(new HomeFragment());
        }
    }

    /**
     * gets visited
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        String[] values = new String[]{"Desmet Hall", "Welch Hall", "Crimont Hall", "Madonna Hall",
                "Catherine Monica Hall", "Campion House", "Alliance House", "Robinson", "Rebmann"};
        List<String> vals = Arrays.asList(values);
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        geofencingClient.removeGeofences(vals);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Loads the fragment
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Helps with the bottom navigation
     */
    public static class BottomNavigationViewHelper {

        /**
         * Helps with shifting to another item in the bottom navigation
         * @param view
         */
        @SuppressLint("RestrictedApi")
        public static void removeShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    item.setShiftingMode(false);
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) { } catch (IllegalAccessException e) { }
        }
    }
}
