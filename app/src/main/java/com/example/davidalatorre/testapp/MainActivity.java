package com.example.davidalatorre.testapp;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.content.Intent;
import android.provider.MediaStore;


public class MainActivity extends FragmentActivity implements ItemFragment.OnHeadlineSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Create the item fragment
            ItemFragment firstFragment = new ItemFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    public void onArticleSelected(int position) {

        if(position == 3) {
            // Open the Camera
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        } else {
            // select item
            InfoFragment articleFrag = (InfoFragment) getSupportFragmentManager().findFragmentById(R.id.info_fragment);

            if (articleFrag != null) {
                // big screen
                articleFrag.updateArticleView(position);

            } else {
                // If the frag is not available, we're in the one-pane layout and must swap frags...

                // Create fragment and give it an argument for the selected article
                InfoFragment newFragment = new InfoFragment();
                Bundle args = new Bundle();
                args.putInt(InfoFragment.ARG_POSITION, position);
                newFragment.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        }
    }

}
