package com.example.jyotirmayghosh.ngo;

import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new MessageFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MessageFragment()).commit();
                    return true;
                case R.id.navigation_location:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new LocationFragment()).commit();
                    return true;
            }
            return false;
        }
    };

}
