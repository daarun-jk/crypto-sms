package com.cns.cryptosms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.cns.cryptosms.Adapters.DatabaseAdapter;
import com.cns.cryptosms.Adapters.FragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    DatabaseAdapter db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        db = new DatabaseAdapter(this);

        //Finding ViewPager for swiping between tabs then creating adapter to know
        //which fragment should be shown on each page
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentAdapter adapter = new FragmentAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_slide);
        tabLayout.setupWithViewPager(viewPager);
    }
}