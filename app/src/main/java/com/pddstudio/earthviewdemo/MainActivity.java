package com.pddstudio.earthviewdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.pddstudio.earthview.EarthView;
import com.pddstudio.earthview.EarthViewCallback;
import com.pddstudio.earthview.EarthWallpaper;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements EarthViewCallback, EarthViewAdapter.OnItemClickListener {

    Button loadBtn;
    RecyclerView recyclerView;
    int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        loadBtn = (Button) findViewById(R.id.load_wall_btn);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EarthView.withGoogle().getAllEarthWallpapers(MainActivity.this);
            }
        });

        //creating the recycler view instance
        recyclerView = (RecyclerView) findViewById(R.id.recycler_demo);
        recyclerView.setHasFixedSize(true);

        //we're using a grid layout here with two columns
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        EarthViewAdapter earthViewAdapter = new EarthViewAdapter(null, this);
        recyclerView.setAdapter(earthViewAdapter);

        Snackbar.make(findViewById(R.id.root_layout), "Please be sure you're on WiFi before loading the walls!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Okay", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                })
                .show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartedLoading() {
        if(loadBtn != null) {
            loadBtn.setEnabled(false);
            loadBtn.setText("Loaded Items: " + itemCount);
        }
    }

    @Override
    public void onItemLoaded(EarthWallpaper earthWallpaper) {
        //just to be sure the result isn't null
        if(earthWallpaper != null) {
            if(loadBtn != null) {
                itemCount++;
                loadBtn.setText("Loaded Items: " + itemCount);
            }
            if(!((EarthViewAdapter) recyclerView.getAdapter()).hasOnItemClickListener()) {
                ((EarthViewAdapter) recyclerView.getAdapter()).setOnItemClickListener(this);
            }
            ((EarthViewAdapter) recyclerView.getAdapter()).addItem(earthWallpaper);
            recyclerView.getAdapter().notifyItemInserted(recyclerView.getAdapter().getItemCount());
        }
    }

    @Override
    public void onFinishedLoading(Collection<EarthWallpaper> earthWallpapers) {

    }

    @Override
    public void onItemClick(View view, int position) {
        EarthWallpaper earthWallpaper = ((EarthViewAdapter) recyclerView.getAdapter()).getItemAtPosition(position);
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(earthWallpaper.getWallpaperUrl()));
        startActivity(browse);
    }
}
