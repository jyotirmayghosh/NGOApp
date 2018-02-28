package com.example.jyotirmayghosh.ngo;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.jyotirmayghosh.ngo.fragment.LocationFragment;
import com.example.jyotirmayghosh.ngo.fragment.MessageFragment;

public class MainActivity extends AppCompatActivity {

    public static final String NAME="NAME";
    public static final String PHONE="PHONE";

    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new MessageFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled}, // enabled
                    new int[] {-android.R.attr.state_enabled}, // disabled
            };

            int[] colors = new int[] {
                    Color.BLACK,
                    Color.WHITE,
            };

            ColorStateList myList = new ColorStateList(states, colors);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setEnabled(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MessageFragment()).commit();
                    return true;
                case R.id.navigation_location:
                    item.setEnabled(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new LocationFragment()).commit();
                    return true;
            }
            return false;
        }
    };

}
