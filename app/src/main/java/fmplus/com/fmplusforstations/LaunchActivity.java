package fmplus.com.fmplusforstations;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

/**
 * Created by uomini on 2/4/2018.
 */

public class LaunchActivity extends AppCompatActivity {
    private static final String COLOR_SET = "0";

    Toolbar toolbar;

    private ImageButton login;
    private ImageButton radio;
    private ImageButton stations;
    private ImageButton favorites;
    private ImageButton extras;
    private ImageButton exit;

    private final String PENDING_ACTION_BUNDLE_KEY = "com.example.hellofacebook:PendingAction";
    private static final String GETRATING = "getRating()";
    private static final int ERROR_TIMEOUT = 20000;

    private PendingAction pendingAction = PendingAction.NONE;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    private Activity activity;

    private AccessTokenTracker accessTokenTracker;

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                String title = getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successfully_posted_post, id);
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };

    private String parms;

    private String user_ID;

    private SharedPreferences sharedPref;

    CallbackManager callbackManager;

    private ShareDialog shareDialog;

    private LoginButton loginButton;

    private ProgressBar spinner;

    VolleyError volleyError;

    RequestQueue queue = null;

    private Timer volleyTimer = null, serverTimer = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_page);

        activity = this;

        // Get a RequestQueue
        queue = setupRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();

        // Build progress spinner
        spinner = findViewById(R.id.progressBar);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    TextView welcome = findViewById(R.id.welcome);
                    welcome.setVisibility(View.INVISIBLE);
                }
            }
        };

        loginButton = findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();

        shareDialog = new ShareDialog(this);
/**
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
**/
        final Intent intent = getIntent();

        final Bundle activityExtras = intent.getExtras();
/**
        if (activityExtras != null) {
            TextView imgLabel = toolbar.findViewById(activityExtras.getInt(COLOR_SET));
            if (imgLabel != null) imgLabel.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSelected));
        }
**/
/**        login = findViewById(R.id.login);
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
        });**/
    }

    @Override
    public void onBackPressed () {
        // Do the right thing.
    }

    @Override
    public void onStart () {
        super.onStart();

        if (Profile.getCurrentProfile() != null) {
            startActivity(new Intent(getApplicationContext(), RadioActivity.class));
            finish();
        }

        // To catch exceptions not caught by logcat.
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(activity));

        shareDialog.registerCallback(callbackManager, shareCallback);
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

        if(AccessToken.getCurrentAccessToken() == null) {
            TextView welcome = findViewById(R.id.welcome);
            welcome.setVisibility(View.INVISIBLE);
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest
                        (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Process the data from Facebook.
                                Log.v("LoginFragment", response.toString());
                                SharedPreferences sharedPref = activity.getSharedPreferences("FMPlusForStations settings", Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPref.edit();
                                try {
                                    String photoUrl = null;

                                    editor.putString("user_ID", object.getString("id"));
                                    user_ID = object.getString("id");

                                    String userName = "";
                                    if (object.isNull("name")) {
                                        if (object.isNull("first_name")) {
                                            userName = getString(R.string.anon_person);
                                        } else {
                                            userName += object.getString("first_name") + " ";
                                            if (!object.isNull("last_name")) userName += object.getString("last_name");
                                        }
                                    } else {
                                        userName = object.getString("name");
                                    }

                                    editor.putString("user_name." + user_ID, userName);
                                    editor.putString("email_addr." + user_ID, object.isNull("email") ? "" : object.getString("email"));
                                    JSONObject photoObj = new JSONObject(object.getString("picture"));
                                    JSONObject data = photoObj.getJSONObject("data");
                                    boolean isSilhouette = data.getBoolean("is_silhouette");
                                    if (isSilhouette) {
                                        photoUrl = "https://www.chiaramail.com/CategoryServer/profile_imgs/q_silhouette.gif";
                                    } else {
                                        photoUrl = data.getString("url");
                                    }
                                    editor.putString("picture_url." + user_ID, photoUrl);
                                    editor.commit();
                                    startActivity(new Intent(getApplicationContext(), RadioActivity.class));
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, timezone, picture, locale, age_range");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                if (pendingAction != PendingAction.NONE) {
                    showAlert();
                    pendingAction = PendingAction.NONE;
                }
            }

            @Override
            public void onError(FacebookException exception) {
                if (pendingAction != PendingAction.NONE
                        && exception instanceof FacebookAuthorizationException) {
                    showAlert();
                    pendingAction = PendingAction.NONE;
                }
            }

            private void showAlert() {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.cancelled)
                        .setMessage(R.string.permission_not_granted)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }
        });
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}


