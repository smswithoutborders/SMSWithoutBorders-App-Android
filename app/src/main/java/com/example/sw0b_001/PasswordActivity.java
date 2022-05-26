package com.example.sw0b_001;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Models.User.User;
import com.example.sw0b_001.Models.User.UserHandler;
import com.example.sw0b_001.Security.SecurityHandler;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class PasswordActivity extends AppCompatActivity {
    SecurityHandler securityLayer;
    private static final int REQUEST_CAMERA_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        try {
            securityLayer = new SecurityHandler();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validateUsersCloudPassword(View view) throws GeneralSecurityException, IOException, InterruptedException {

        EditText passwordField = findViewById(R.id.message_recipient_number_edit_text);
        SecurityHandler securityHandler = new SecurityHandler(getApplicationContext());
        GatewayServer gatewayServers[] = {new GatewayServer()};

        if(passwordField.getText().toString().isEmpty()) {
            passwordField.setError("Password cannot be empty!");
            return;
        }

        Button validationButton = findViewById(R.id.password_confirm_btn);
        validationButton.setVisibility(View.INVISIBLE);

        CircularProgressIndicator passwordValidationProgressBar = findViewById(R.id.password_validation_progress_bar);
        passwordValidationProgressBar.setVisibility(View.VISIBLE);

        if(getIntent().hasExtra("gatewayserver_id")) {
            long gatewayServerId = getIntent().getLongExtra("gatewayserver_id", -1);

            // TODO check if has verification url
            String verificationUrl = getIntent().getStringExtra("verification_url");

            if(gatewayServerId == -1) {
                Log.e(getLocalClassName(), "GatewayServer ID is incorrect currently = -1");
            }
            else {
                Thread extractGatewayInformationThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Datastore databaseConnector = Room.databaseBuilder(getApplicationContext(),
                                Datastore.class, Datastore.DatabaseName).build();
                        GatewayServersDAO gatewayServersDAO = databaseConnector.gatewayServersDAO();
                        gatewayServers[0] = gatewayServersDAO.getById(gatewayServerId);

                    }
                });
                extractGatewayInformationThread.start();
                extractGatewayInformationThread.join();

                byte[] passwordEncoded = passwordField.getText().toString().getBytes(StandardCharsets.UTF_8);
                try {
                    GatewayServer gatewayServer = gatewayServers[0];

                    // TODO start a loader here, in case of a slow internet connection
                    UserHandler userHandler = new UserHandler(getApplicationContext());
                    User user = userHandler.getUser();

                    byte[] RSAEncryptedPassword = securityHandler.encryptWithExternalPublicKey(passwordEncoded, gatewayServer.getPublicKey());

                    String passwordBase64 = Base64.encodeToString(RSAEncryptedPassword, Base64.DEFAULT);

                    JSONObject jsonBody = new JSONObject( "{\"password\": \"" + passwordBase64 + "\"}");
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(verificationUrl, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (getIntent().hasExtra("callbackIntent")) {
                                Object callbackObject = getIntent().getExtras().get("callbackIntent");
                                if (callbackObject.getClass() == Intent.class) {
                                    Intent callbackIntent = (Intent) callbackObject;
                                    callbackIntent.putExtra("payload", response.toString());
                                    startActivity(callbackIntent);
                                    finish();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            passwordField.setError("Authentication Failed! Please try again...");
                            error.printStackTrace();

                            passwordValidationProgressBar.setVisibility(View.INVISIBLE);
                            validationButton.setVisibility(View.VISIBLE);
                        }
                    });
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(jsonObjectRequest);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void linkPrivacyPolicy(View view) {
        Uri intentUri = Uri.parse(getResources().getString(R.string.privacy_policy));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }

}