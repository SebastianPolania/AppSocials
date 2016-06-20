package org.appsocial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    
    private AdView banner;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this);
        mCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        final TextView name = (TextView)findViewById(R.id.nameProfile);
        final TextView welcome = (TextView)findViewById(R.id.welcome);
        loginButton = (LoginButton)findViewById(R.id.loginFacebook);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String id = object.getString("id");
                                    String firstName = object.getString("first_name");
                                    String lastName = object.getString("last_name");
                                    String nom = firstName+" "+lastName;
                                    welcome.setVisibility(View.VISIBLE);
                                    name.setVisibility(View.VISIBLE);
                                    name.setText(nom);
                                } catch (JSONException e) {
                                    Log.v("Json",e.toString());
                                    Toast.makeText(getApplicationContext(),
                                            "Error "+e.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                                Log.v("LoginActivity", response.toString());
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,first_name,last_name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
                Toast.makeText(MainActivity.this, "¡Inicio de sesión exitoso!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "¡Inicio de sesión cancelado!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "¡Inicio de sesión NO exitoso!", Toast.LENGTH_LONG).show();
            }
        });

        banner = (AdView) findViewById(R.id.adsView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

        banner.loadAd(adRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onPause() {
        if (banner!=null){
            banner.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (banner!=null){
            banner.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (banner!=null){
            banner.resume();
        }
        super.onResume();
    }
}
