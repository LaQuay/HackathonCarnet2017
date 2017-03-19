package com.jfem.hackathoncarnet.carnethackathon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final int SECTION_MAIN_FRAGMENT = 1;
    public static final int SECTION_MICROCITY_FRAGMENT = 2;
    public static final int SECTION_VENUE_FRAGMENT = 3;
    public static final int SECTION_DISCOUNTS_FRAGMENT = 4;
    public static final int SECTION_MICROCITY_INFO_FRAGMENT = 5;
    private DrawerLayout drawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Check first item
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        onSectionAttached(SECTION_MAIN_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        String fragmentTAG = null;
        if (id == R.id.nav_around_me) {
            fragment = MainFragmentActivity.newInstance(SECTION_MAIN_FRAGMENT);
            fragmentTAG = MainFragmentActivity.TAG;
        } else if (id == R.id.nav_by_microcity) {
            fragment = MicroCityFragment.newInstance(SECTION_MICROCITY_FRAGMENT);
            fragmentTAG = MicroCityFragment.TAG;
        } else if (id == R.id.nav_by_filter) {
            fragment = VenueFragment.newInstance(SECTION_VENUE_FRAGMENT);
            fragmentTAG = VenueFragment.TAG;
        } else if (id == R.id.nav_by_discounts) {
            fragment = DiscountsFragment.newInstance(SECTION_DISCOUNTS_FRAGMENT);
            fragmentTAG = DiscountsFragment.TAG;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_container, fragment, fragmentTAG);
            if (id != R.id.nav_around_me) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onSectionAttached(int number) {
        String mTitle;
        switch (number) {
            case SECTION_MAIN_FRAGMENT:
                //mTitle = getString(R.string.title_forecast);
                mTitle = "Around Me";
                break;

            case SECTION_MICROCITY_FRAGMENT:
                mTitle = "Search Microcity";
                break;

            case SECTION_VENUE_FRAGMENT:
                mTitle = "Search Services";
                break;

            case SECTION_DISCOUNTS_FRAGMENT:
                mTitle = "Search Discounts";
                break;

            case SECTION_MICROCITY_INFO_FRAGMENT:
                mTitle = "Microcity information";
                break;

            default:
                mTitle = getString(R.string.app_name);
        }

        if (toolbar != null) {
            toolbar.setTitle(mTitle);
        }
    }
}
