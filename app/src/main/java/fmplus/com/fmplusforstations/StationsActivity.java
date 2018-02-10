package fmplus.com.fmplusforstations;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by uomini on 2/4/2018.
 */

public class StationsActivity extends AppCompatActivity {
    private static final String COLOR_SET = "0";

    Toolbar toolbar;

    private ImageButton login;
    private ImageButton radio;
    private ImageButton stations;
    private ImageButton favorites;
    private ImageButton extras;
    private ImageButton exit;

    StationListAdapter  dataAdapter;

    ListView stationList;

    private Stations[] stationDataObj;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stations_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();

        final Bundle activityExtras = intent.getExtras();

        if (activityExtras != null) {
            TextView imgLabel = toolbar.findViewById(activityExtras.getInt(COLOR_SET));
            if (imgLabel != null) imgLabel.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSelected));
        }

        stationList = findViewById(R.id.stationList);
        stationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
               ImageView favorite = view.findViewById(R.id.favorite);
               Integer tag = (Integer)stationDataObj[pos].favorite.getTag();
               if (tag.intValue() == R.drawable.star) {
                   favorite.setImageResource(R.drawable.star_outline);
                   stationDataObj[pos].favorite.setTag(R.drawable.star_outline);
               } else {
                   favorite.setImageResource( R.drawable.star);
                   stationDataObj[pos].favorite.setTag(R.drawable.star);
               }
           }
        });

        stationDataObj = loadStationData();

        ArrayList<Stations> list = new ArrayList<Stations>();

        for (int i = 0; i < stationDataObj.length; i++) {
            list.add(new Stations(stationDataObj[i], stationList));
        }
        dataAdapter = new StationListAdapter(this, stationDataObj);
        stationList.setAdapter(dataAdapter);

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(LoginActivity.class, R.id.login_text);
            }
        });

        radio = findViewById(R.id.radio);
        radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(RadioActivity.class, R.id.radio_text);
            }
        });

        stations = findViewById(R.id.stations);
        stations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(StationsActivity.class, R.id.stations_text);
            }
        });

        favorites = findViewById(R.id.favorites);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(FavoritesActivity.class, R.id.favorites_text);
            }
        });

        extras = findViewById(R.id.extras);
        extras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(ExtrasActivity.class, R.id.extras_text);
            }
        });

        exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //       TextView stations_text = toolbar.findViewById(R.id.stations_text);
 //       stations_text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSelected));

    }

    private Stations[] loadStationData() {
        // Return a list of stations for this location.

        Stations[] stations = new Stations[5];
        stations[0] = new Stations(stations[0], stationList);
        stations[1] = new Stations(stations[1], stationList);
        stations[2] = new Stations(stations[2], stationList);
        stations[3] = new Stations(stations[3], stationList);
        stations[4] = new Stations(stations[4], stationList);
        stations[0].name = "KQED - 88.5";
        stations[0].favorite = new ImageView(this);
        stations[0].favorite.setImageResource(R.drawable.star);
        stations[0].favorite.setTag(R.drawable.star);
        stations[0].logo = new ImageView(this);
        stations[0].logo.setImageResource(R.drawable.fmpluslogo);
        stations[0].description = "KQED San Francisco";
        stations[1].name = "88.7 FM";
        stations[1].favorite = new ImageView(this);
        stations[1].favorite.setImageResource(R.drawable.star_outline);
        stations[1].favorite.setTag(R.drawable.star_outline);
        stations[1].logo = new ImageView(this);
        stations[1].logo.setImageResource(R.drawable.fmpluslogo);
        stations[1].description = "88.7 FM station description";
        stations[2].name = "92.3 Amp Radio";
        stations[2].favorite = new ImageView(this);
        stations[2].favorite.setImageResource(R.drawable.star_outline);
        stations[2].favorite.setTag(R.drawable.star_outline);
        stations[2].logo = new ImageView(this);
        stations[2].logo.setImageResource(R.drawable.amplogo);
        stations[2].description = "92.3 FM - Contemporary Hit Radio";
        stations[3].name = "102.1 FM";
        stations[3].favorite = new ImageView(this);
        stations[3].favorite.setImageResource(R.drawable.star_outline);
        stations[3].favorite.setTag(R.drawable.star_outline);
        stations[3].logo = new ImageView(this);
        stations[3].logo.setImageResource(R.drawable.fmpluslogo);
        stations[3].description = "102.1 station description";
        stations[4].name = "93.1 AMOR WPAT HD1";
        stations[4].favorite = new ImageView(this);
        stations[4].favorite.setImageResource(R.drawable.star_outline);
        stations[4].favorite.setTag(R.drawable.star_outline);
        stations[4].logo = new ImageView(this);
        stations[4].logo.setImageResource(R.drawable.amorlogo);
        stations[4].description = "93.1 FM - Mas Musica y Menos Ads";

        return stations;
    }

    @Override
    public void onBackPressed () {
        // Do the right thing.
    }

    @Override
    public void onStart () {
        super.onStart();
    }

    @Override
    public void onStop () {
        super.onStop();
    }

    @Override
    public void onPause () {
        super.onPause();
    }

    @Override
    public void onResume () {
        super.onResume();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        finish();
    }

    private void launchActivity(Class actClass, int textID) {
        //   Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        Intent intent = new Intent(getApplicationContext(), actClass);
        intent.putExtra(COLOR_SET, textID);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
