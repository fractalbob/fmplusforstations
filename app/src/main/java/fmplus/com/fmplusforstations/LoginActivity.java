package fmplus.com.fmplusforstations;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.Manifest;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by uomini on 2/4/2018.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String COLOR_SET = "0";

    Toolbar toolbar;

    private ImageButton login;
    private ImageButton radio;
    private ImageButton stations;
    private ImageButton favorites;
    private ImageButton extras;
    private ImageButton exit;

    public static final int MAX_NAME_LEN = 18;
    private static final int REQUEST_WRITE_PERMISSION = 1;
    private final String PENDING_ACTION_BUNDLE_KEY = "com.example.hellofacebook:PendingAction";
    private static final String GETRATING = "getRating()";
    private static final String CANCEL_VOLLEY = "CANCEL";

    private PendingAction pendingAction = PendingAction.NONE;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    private Activity activity = this;

    private AccessTokenTracker accessTokenTracker;

    private TextView welcome;

    private Timer volleyTimer = null, serverTimer = null;

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

    private String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    VolleyError volleyError;

    RequestQueue queue = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_page);

        // Get a RequestQueue
        queue = setupRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();

        // Build progress spinner
        spinner = findViewById(R.id.progressBar);

        sharedPref = activity.getSharedPreferences("FMPlusForStations settings", Context.MODE_PRIVATE);
        user_ID = sharedPref.getString("user_ID", "");

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

        welcome = findViewById(R.id.welcome);

        loginButton = findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            if (name != null) pendingAction = PendingAction.valueOf(name);
        }
    }

    @Override
    public void onBackPressed () {
        // Do the right thing.
    }

    @Override
    public void onStart () {
        super.onStart();

        if (Profile.getCurrentProfile() != null) {
            String userName = sharedPref.getString("user_name." + user_ID, "");
            if (userName.length() > MAX_NAME_LEN) userName = userName.substring(0, MAX_NAME_LEN) + "...";
            welcome.setText(getString(R.string.welcome));
            String textLabel = welcome.getText().toString() + " " + userName;
            welcome.setText(textLabel);
            welcome.setVisibility(View.VISIBLE);
        } else {
            welcome.setVisibility(View.INVISIBLE);
        }
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

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker profileTracker;

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
                //    parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, timezone, picture, locale, age_range, user_education_history, user_hometown, user_relationships, user_relationship_details, user_religion_politics");
                //     parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, timezone, picture, locale, age_range, user_education_history, user_hometown, user_relationships, user_relationship_details, user_religion_politics, user_work_history");
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


