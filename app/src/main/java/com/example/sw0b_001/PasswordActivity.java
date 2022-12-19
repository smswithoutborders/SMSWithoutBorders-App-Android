package com.example.sw0b_001;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Security.SecurityRSA;
import com.example.sw0b_001.databinding.ActivityPasswordBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

public class PasswordActivity extends AppCompactActivityCustomized {
    private ActivityPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(!validateSession())
            finish();
    }

    private Boolean validateSession() {
        Intent intent = getIntent();
        return intent.hasExtra("callbackIntent") &&
                intent.hasExtra("public_key") &&
                intent.hasExtra("user_id") &&
                intent.hasExtra("gateway_server_url") &&
                intent.hasExtra("gateway_server_public_key");

    }

    public String getEncryptionDigest() {
       if(SecurityHandler.defaultEncryptionDigest.equals(MGF1ParameterSpec.SHA256))
           return "sha256";
       return "sha1";
    }

    private void transmitPassword(byte[] password, PublicKey gatewayServerPublicKey, String gatewayServerVerificationUrl) throws GeneralSecurityException, IOException, JSONException, ExecutionException, InterruptedException, TimeoutException, VolleyError {
        SecurityRSA securityRSA = new SecurityRSA(this);
        String publicKey = getIntent().getStringExtra("public_key");

        byte[] encryptedPassword = securityRSA.encrypt(
                password,
                gatewayServerPublicKey);
        String passwordBase64 = Base64.encodeToString(encryptedPassword, Base64.DEFAULT);
        Log.d(getLocalClassName(), "Enc Password: " + passwordBase64);

        try {
            JSONObject jsonBody = new JSONObject(
                    "{\"public_key\": \"" + publicKey + "\"," +
                            "\"mgf1ParameterSpec\": \"" + getEncryptionDigest() + "\"," +
                            "\"password\": \"" + passwordBase64 + "\"}");
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    gatewayServerVerificationUrl,
                    jsonBody, future, future);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(jsonObjectRequest);
            JSONObject response = future.get(10, TimeUnit.SECONDS);

            Object callbackObject = getIntent().getExtras().get("callbackIntent");

            if (callbackObject.getClass() == Intent.class) {
                Intent callbackIntent = (Intent) callbackObject;
                ComponentName name = callbackIntent.resolveActivity(getPackageManager());
                if(name.getPackageName().equals(getPackageName()) &&
                        name.getClassName().equals(SyncHandshakeActivity.class.getName())) {
                    callbackIntent.putExtra("payload", response.toString());
                    startActivity(callbackIntent);
                    finish();
                }
            }
        } catch(InterruptedException  | TimeoutException | ExecutionException e) {
            throw e;
        } catch(JSONException e ) {
            throw e;
        } catch(Exception e ) {
            throw e;
        }
    }

    public void onClickVerifyPassword(View view) throws IOException, InterruptedException {
        EditText passwordField = findViewById(R.id.message_recipient_number_edit_text);

        if(passwordField.getText().toString().isEmpty()) {
            passwordField.setError(getString(R.string.password_empty));
            return;
        }
        Button validationButton = findViewById(R.id.password_confirm_btn);
        validationButton.setVisibility(View.INVISIBLE);

        CircularProgressIndicator passwordValidationProgressBar = findViewById(R.id.password_validation_progress_bar);
        passwordValidationProgressBar.setVisibility(View.VISIBLE);

        byte[] passwordEncoded = passwordField.getText().toString().getBytes(StandardCharsets.UTF_8);

        PublicKey gatewayServerPublicKey = (PublicKey) getIntent().getExtras().get("gateway_server_public_key");
        Log.d(getLocalClassName(), "Pub key: " +
                Base64.encodeToString(gatewayServerPublicKey.getEncoded(), Base64.DEFAULT));

        String gatewayServerUrl = getIntent().getStringExtra("gateway_server_url");
        Thread transmissionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    transmitPassword(passwordEncoded, gatewayServerPublicKey, gatewayServerUrl);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch(InterruptedException  | TimeoutException | ExecutionException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            passwordField.setError(getString(R.string.password_failed));
                            passwordValidationProgressBar.setVisibility(View.INVISIBLE);
                            validationButton.setVisibility(View.VISIBLE);
                        }
                    });
                }
                catch(Exception e ) {
                    e.printStackTrace();
                }
            }
        });

        transmissionThread.start();
    }


    public void linkPrivacyPolicy(View view) {
        Uri intentUri = Uri.parse(getResources().getString(R.string.privacy_policy));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }

}