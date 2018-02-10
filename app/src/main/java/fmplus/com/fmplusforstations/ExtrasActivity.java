package fmplus.com.fmplusforstations;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by uomini on 2/4/2018.
 */

public class ExtrasActivity extends AppCompatActivity {
    private static final String COLOR_SET = "0";

    Toolbar toolbar;

    private ImageButton login;
    private ImageButton radio;
    private ImageButton stations;
    private ImageButton favorites;
    private ImageButton extras;
    private ImageButton exit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extras_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();

        final Bundle activityExtras = intent.getExtras();

        if (activityExtras != null) {
            TextView imgLabel = toolbar.findViewById(activityExtras.getInt(COLOR_SET));
            if (imgLabel != null) imgLabel.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSelected));
        }

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
