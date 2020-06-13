package com.example.sesafoods.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorJoiner;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sesafoods.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.IdToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;


//import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;


import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button customerButton, driverButton;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPref;
    private Button buttonLogin, signin;
    private TextView accessTokenG;
    private GoogleApiClient googleApiClient;
    private static final int SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        accessTokenG = findViewById(R.id.textview);

        String serverClientId = getString(R.string.server_client_id);
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

//        Handle google signin button
        signin = (Button) findViewById(R.id.sign_in_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN);
//                handleResults(GoogleSignInAccount(Result.getIDToken)));
//                String result = gso.getServerClientId();
////
//                ColorDrawable customerButtonColor = (ColorDrawable) customerButton.getBackground();
//                if (customerButtonColor.getColor() == getResources().getColor(R.color.colorAccent)) {
//                    loginToServer2(result, "customer");
//                } else {
//                    loginToServer2(result, "driver");
//                }
                signin.setText("LOADING....");
                signin.setClickable(false);
                signin.setBackgroundColor(getResources().getColor(R.color.colorLightGray));


            }

            private void handleResults(GoogleSignInAccount Result) {
                String account = Result.getIdToken();
                ColorDrawable customerButtonColor = (ColorDrawable) customerButton.getBackground();
                if (customerButtonColor.getColor() == getResources().getColor(R.color.colorAccent)) {
                    loginToServer2(account, "customer");
                }else{
                    loginToServer2(account, "driver");
                }
            }

        });



        customerButton = (Button)findViewById(R.id.button_customer);
        driverButton = (Button)findViewById(R.id.button_driver);

//        Handle Customer Button
        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                customerButton.setTextColor(getResources().getColor(android.R.color.white));

                driverButton.setBackgroundColor(getResources().getColor(android.R.color.white));
                driverButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

//         Handle Driver Button
        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                driverButton.setTextColor(getResources().getColor(android.R.color.white));

                customerButton.setBackgroundColor(getResources().getColor(android.R.color.white));
                customerButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

//
//

//        Handle Login Button
        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
                } else {
                    ColorDrawable customerButtonColor = (ColorDrawable) customerButton.getBackground();
                    if (customerButtonColor.getColor() == getResources().getColor(R.color.colorAccent)) {
                        loginToServer(AccessToken.getCurrentAccessToken().getToken(), "customer");
                    }else{
                        loginToServer(AccessToken.getCurrentAccessToken().getToken(), "driver");
                    }
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();
        sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
        final Button buttonLogout = (Button) findViewById(R.id.button_logout);
        buttonLogout.setVisibility(View.GONE);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("FACEBOOK TOKEN", loginResult.getAccessToken().getToken());
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        Log.d("FACEBOOK DETAILS", object.toString());

                                        SharedPreferences.Editor editor = sharedPref.edit();

                                        try {
                                            editor.putString("name", object.getString("name"));
                                            editor.putString("email", object.getString("email"));
                                            editor.putString("avatar", object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                        }catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        editor.commit();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture");
                        request.setParameters(parameters);
                        request.executeAsync();

                        ColorDrawable customerButtonColor = (ColorDrawable) customerButton.getBackground();
                        if (customerButtonColor.getColor() == getResources().getColor(R.color.colorAccent)) {
                            loginToServer(AccessToken.getCurrentAccessToken().getToken(), "customer");
                        }else{
                            loginToServer(AccessToken.getCurrentAccessToken().getToken(), "driver");
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("USER", sharedPref.getAll().toString());
            buttonLogin.setText("Continue as " + sharedPref.getString("name", ""));
            buttonLogout.setVisibility(View.VISIBLE);
        }

        // Handle logout button
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                buttonLogin.setText("Login with Facebook");

                buttonLogout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            GoogleSignInAccount account = result.getSignInAccount();
//            String idTokenString = account.getIdToken();


//            if (result.isSuccess()) {
//                Log.d("GOOGLE TOKEN", result.getID;
//
//            } else {
//                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
//            }
            // The Task returned from this call is always completed, no need to attach
            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void loginToServer(String facebookAccessToken, final String userType) {
        buttonLogin.setText("LOADING....");
        buttonLogin.setClickable(false);
        buttonLogin.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

        String url = getString(R.string.API_URL) + "/social/convert-token";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("grant_type", "convert_token");
            jsonBody.put("client_id", getString(R.string.CLIENT_ID));
            jsonBody.put("client_secret", getString(R.string.CLIENT_SECRET));
            jsonBody.put("backend", "facebook");
            jsonBody.put("token", facebookAccessToken);
            jsonBody.put("user_type", userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Execute code
                        Log.d("LOGIN TO SERVER", response.toString());

                        // Save server token to local database
                        SharedPreferences.Editor editor = sharedPref.edit();
                        try {
                            editor.putString("token", response.getString("access_token"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();

                        // start main activity
                        if (userType.equals("customer")) {
                            Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), DriverMainActivity.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }


    private void loginToServer2(String googleAccessToken, final String userType) {
        signin.setText("LOADING....");
        signin.setClickable(false);
        signin.setBackgroundColor(getResources().getColor(R.color.colorLightGray));


        String url = getString(R.string.API_URL) + "/social/convert-token";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("grant_type", "convert_token");
            jsonBody.put("client_id", getString(R.string.CLIENT_ID));
            jsonBody.put("client_secret", getString(R.string.CLIENT_SECRET));
            jsonBody.put("backend", "google-oauth2");
            jsonBody.put("token", googleAccessToken);
            jsonBody.put("user_type", userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Execute code
                        Log.d("LOGIN TO SERVER", response.toString());

                        // Save server token to local database
                        SharedPreferences.Editor editor = sharedPref.edit();
                        try {
                            editor.putString("token", response.getString("access_token"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();

                        // start main activity
                        if (userType.equals("customer")) {
                            Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), DriverMainActivity.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            final GoogleSignInAccount account = result.getSignInAccount();
            String Token = account.getServerAuthCode();
            String idTokenString = account.getIdToken();
            Log.d("GOOGLE TOKEN", Token);
//            Log.d("GOOGLE ACCONT DETAIL", account.getPhotoUrl().toString());

            SharedPreferences.Editor editor = sharedPref.edit();

            try {
                editor.putString("name", account.getDisplayName());
                editor.putString("email", account.getEmail());
                editor.putString("avatar", account.getPhotoUrl().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            editor.commit();

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormEncodingBuilder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", "773795143377-5mhkcesfff6k1r4tluh69cmomrmeg9ge.apps.googleusercontent.com")
                    .add("client_secret", "ZLNrOycVt_KPlhrd2WksZF8D")
                    .add("redirect_uri","")
                    .add("code", Token)
//                    .add("id_token", idTokenString)
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url("https://www.googleapis.com/oauth2/v4/token")
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        final String message = jsonObject.toString(5);
                        Log.d("ACCESS_TOKEN", message);

                        final String access_token = jsonObject.getString("access_token");
                        Log.d("REAL ACCESS TOKEN", access_token);

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ColorDrawable customerButtonColor = (ColorDrawable) customerButton.getBackground();
                                if (customerButtonColor.getColor() == getResources().getColor(R.color.colorAccent)) {
                                    loginToServer2(access_token, "customer");
                                } else {
                                    loginToServer2(access_token, "driver");
                                }
                            }
                        });


//                        JSONArray myResponse = new JSONArray(response.body());
//                        if(myResponse != null && myResponse.length() > 0){
//                            for (int i = 0; i < myResponse.length(); i++) {
//                                JSONObject object = myResponse.getJSONObject(i);
//
//                                // get your data from jsonobject
//
//                                //accessTokenG.setText(object.getString("access_Token"));
//
//
//                            }
//                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            String acct = account

            // Signed in successfully, show authenticated UI.
//            Intent intent = new Intent(MainActivity.this, CustomerMainActivity.class);
//            startActivity(intent);
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
//        }
    }
}
