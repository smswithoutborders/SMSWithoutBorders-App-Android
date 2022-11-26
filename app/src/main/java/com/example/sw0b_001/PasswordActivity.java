package com.example.sw0b_001;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Models.AppCompactActivityRtlEnabled;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Security.SecurityHelpers;
import com.example.sw0b_001.Security.SecurityRSA;
import com.example.sw0b_001.databinding.ActivityPasswordBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class PasswordActivity extends AppCompactActivityRtlEnabled {
    private ActivityPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(!validateSession())
            finish();
    }

    private Boolean validateSession() {
        Intent intent = getIntent();
        if(!intent.hasExtra("callbackIntent"))
            return false;
        if(!intent.hasExtra("public_key"))
            return false;
        return true;
    }

    private void transmitPassword(byte[] password, PublicKey gatewayServerPublicKey) throws GeneralSecurityException, IOException, JSONException, ExecutionException, InterruptedException, TimeoutException, VolleyError {
        SecurityRSA securityRSA = new SecurityRSA(this);
        String publicKey = getIntent().getStringExtra("public_key");

        String gatewayServerVerificationUrl = new String();

        if(BuildConfig.DEBUG)
            gatewayServerVerificationUrl = getString(R.string.official_staging_site_verification_url);
        else
            gatewayServerVerificationUrl = getString(R.string.official_site_verification_url);

        byte[] encryptedPassword = securityRSA.encrypt(
                password,
                gatewayServerPublicKey);
        String passwordBase64 = Base64.encodeToString(encryptedPassword, Base64.DEFAULT);
        Log.d(getLocalClassName(), "Enc Password: " + passwordBase64);

        try {
            JSONObject jsonBody = new JSONObject(
                    "{\"public_key\": \"" + publicKey + "\"}" +
                            "{\"mgf1ParameterSpec\": \"" + SecurityHandler.MGF1ParameterSpecValue + "\"}" +
                            "{\"password\": \"" + encryptedPassword + "\"}");
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    gatewayServerVerificationUrl,
                    jsonBody, future, future);
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(jsonObjectRequest);
            JSONObject response = future.get(30, TimeUnit.SECONDS);
        } catch (ExecutionException e){
            throw e;
        } catch(InterruptedException  | TimeoutException e ) {
            e.printStackTrace();
            throw new VolleyError(e);
        } catch(JSONException e ) {
            throw e;
        } catch(Exception e ) {
            throw e;
        }
    }

    public void onClickVerifyPassword(View view) throws GeneralSecurityException, IOException, InterruptedException, JSONException, VolleyError, ExecutionException, TimeoutException {
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

        PublicKey gatewayServerPublicKey = getGatewayServerPublicKey();
        try {
            transmitPassword(passwordEncoded, gatewayServerPublicKey);
        }
        catch(Exception e ) {
            e.printStackTrace();

            // TODO: should handle the issues seperately
            passwordField.setError(getString(R.string.password_failed));
            passwordValidationProgressBar.setVisibility(View.INVISIBLE);
            validationButton.setVisibility(View.VISIBLE);
        }
    }

    private PublicKey getGatewayServerPublicKey() throws IOException {
        String primaryKeySite = new String();
        if(BuildConfig.DEBUG)
            primaryKeySite = getString(R.string.official_staging_site);
        else
            primaryKeySite = getString(R.string.official_site);
        Log.d(getLocalClassName(), primaryKeySite);

        URL url = new URL(primaryKeySite);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        final Certificate[][] certificates = new Certificate[1][1];
        try {
            Thread threading = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        certificates[0] = urlConnection.getServerCertificates();
//                        for(Certificate certificate: certificates[0]) {
//                            PublicKey publicKey = certificate.getPublicKey();
//                            Log.d(getLocalClassName(), "Cert det: " +
//                                    Base64.encodeToString(publicKey.getEncoded(), Base64.NO_PADDING) +
//                                    certificate.getType() );
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            threading.start();
            threading.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        // Returns the certificate
        // return certificates[0][0].getEncoded();

        return certificates[0][0].getPublicKey();
    }

    public void linkPrivacyPolicy(View view) {
        Uri intentUri = Uri.parse(getResources().getString(R.string.privacy_policy));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }

}