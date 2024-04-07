package com.example.sw0b_001;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentDAO;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.Models.RabbitMQ;
import com.example.sw0b_001.Models.RecentsRecyclerAdapter;
import com.example.sw0b_001.Models.RecentsViewModel;
import com.example.sw0b_001.Models.ThreadExecutorPool;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Security.SecurityHelpers;
import com.example.sw0b_001.Security.SecurityRSA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.spec.MGF1ParameterSpec;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TODO: Security checks
 * - Checks if username is present - if valid username, continue with user in the app
 * - what if username gets spoofed (security keys won't match tho)
 */

public class HomepageActivity extends AppCompactActivityCustomized {

    FragmentManager fragmentManager = getSupportFragmentManager();

    RecentsViewModel recentsViewModel;
    SecurityHandler securityHandler;

    final String RECENTS_FRAGMENT_TAG = "RECENTS_FRAGMENT_TAG";
    final String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG";

    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code

    RabbitMQ rabbitMQ;

    private void securityChecks() {
        try {
            SecurityHandler securityHandler = new SecurityHandler(getBaseContext());
            if(securityHandler.requiresSyncing()) {
                securityHandler.removeSharedKey();
                startActivity(new Intent(this, SplashActivity.class));
                finish();
                return;
            }

            if(SecurityHandler.phoneCredentialsPossible(getApplicationContext()) ) {
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                if (!securityHandler.seenBiometricCheckAlwaysOn()) {
                    startActivity(new Intent(this, AppLockBiometricActivity.class));
                    finish();
                }

                else if (!securityHandler.seenBiometricCheckDecryption()) {
                    startActivity(new Intent(this, MessageLockBiometricsActivity.class));
                    finish();
                }
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void configureRecyclerHandlers() throws GeneralSecurityException, IOException {
        RecentsRecyclerAdapter recentsRecyclerAdapter = new RecentsRecyclerAdapter(securityHandler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        RecyclerView recentsRecyclerView = findViewById(R.id.recents_recycler_view);
        recentsRecyclerView.setLayoutManager(linearLayoutManager);
        recentsRecyclerView.setAdapter(recentsRecyclerAdapter);

        recentsViewModel = new ViewModelProvider(this).get( RecentsViewModel.class );

        EncryptedContentDAO encryptedContentDAO = Datastore.getDatastore(getApplicationContext())
                .encryptedContentDAO();
        recentsViewModel.getMessages(encryptedContentDAO).observe(this, new Observer<List<EncryptedContent>>() {
            @Override
            public void onChanged(List<EncryptedContent> encryptedContents) {
                TextView noRecentMessagesText = findViewById(R.id.no_recent_messages);

                if(!encryptedContents.isEmpty()) noRecentMessagesText.setVisibility(View.INVISIBLE);
                else noRecentMessagesText.setVisibility(View.VISIBLE);

                recentsRecyclerAdapter.submitList(encryptedContents);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        try {
            securityHandler = new SecurityHandler(getApplicationContext());
            configureRecyclerHandlers();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        // TODO: for verification
//        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
//        registerReceiver(smsVerificationReceiver, intentFilter);
    }

    public String getEncryptionDigest() {
        if(SecurityHandler.defaultEncryptionDigest.equals(MGF1ParameterSpec.SHA256))
            return "sha256";
        return "sha1";
    }

    private void checkAccountSynchronization() throws InterruptedException, GeneralSecurityException, IOException, JSONException {
        // TODO: should become a WorkManager if fails
        SecurityRSA securityRSA = new SecurityRSA(getApplicationContext());
        SecurityHandler securityHandler = new SecurityHandler(getApplicationContext());

        List<GatewayServer> gatewayServerList = GatewayServersHandler.getAllGatewayServers(getApplicationContext());

        String keystoreAlias = GatewayServersHandler.buildKeyStoreAlias(gatewayServerList.get(0).getUrl());

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        String msisdn = securityHandler.getMSISDN();

        byte[] msisdnSigned = securityRSA.sign(msisdn.getBytes(StandardCharsets.UTF_8), keystoreAlias);
        String msisdnSignedEncoded = Base64.encodeToString(msisdnSigned, Base64.DEFAULT);

        String gatewayServerPublicKey = gatewayServerList.get(0).getPublicKey();
        byte[] encryptedMsisdn = securityRSA.encrypt(msisdn.getBytes(StandardCharsets.UTF_8),
                SecurityRSA.getPublicKeyFromBase64String(gatewayServerPublicKey));
        msisdn = Base64.encodeToString(encryptedMsisdn, Base64.DEFAULT);

        JSONObject jsonBody = new JSONObject( "{\"msisdn\": \"" + msisdn + "\", " +
                "\"msisdn_signature\": \"" + msisdnSignedEncoded + "\", " +
                "\"mgf1ParameterSpec\": \"" + getEncryptionDigest() +"\"}");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                gatewayServerList.get(0).composeFullURL()
                        + "/v2/sync/users/verification",
                jsonBody, future, future);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy( 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        try {
            requestQueue.add(jsonObjectRequest);

            JSONObject response = future.get(15, TimeUnit.SECONDS);
            if(response.has("shared_key")) {
                try {
                    String rxSharedKey = (String) response.get("shared_key");
                    byte[] decryptedSharedKey = SecurityHelpers.getDecryptedSharedKey(getApplicationContext());
                    byte[] decodedRxSharedKey = Base64.decode(rxSharedKey, Base64.DEFAULT);

                    if(!new String(securityRSA.decrypt(decodedRxSharedKey, keystoreAlias)).equals(
                            new String(decryptedSharedKey, StandardCharsets.UTF_8))) {
                        securityHandler.removeSharedKey();
                        startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                        finish();
                    }
                } catch(Throwable e ) {
                    e.printStackTrace();
                }
            }
        } catch(TimeoutException | InterruptedException e) {
            e.printStackTrace();
        } catch(ExecutionException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause instanceof VolleyError) {
                VolleyError error = (VolleyError) cause;
                NetworkResponse networkResponse = error.networkResponse;
                if(networkResponse != null && networkResponse.statusCode == 403) {
                    securityHandler.removeSharedKey();
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                    finish();
                }
                else if(networkResponse != null && networkResponse.statusCode == 404) {
                    if(BuildConfig.DEBUG)
                        Log.d(getLocalClassName(), "Verification code said it's 404...");
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThreadExecutorPool.executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    checkAccountSynchronization();
                } catch (InterruptedException | JSONException | IOException |
                         GeneralSecurityException e) {
                    Log.e(getLocalClassName(), "Exception", e);
                }
            }
        });
    }

    //    public void onComposePlatformClick(View view) {
//        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
//                        AvailablePlatformsFragment.class, null)
//                .setReorderingAllowed(true)
//                .addToBackStack(null)
//                .setCustomAnimations(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right,
//                        android.R.anim.fade_in,
//                        android.R.anim.fade_out)
//                .commit();
//    }

    private static final int CREDENTIAL_PICKER_REQUEST = 1;  // Set to an unused request code
    private static final int RESOLVE_HINT = 2;  // Set to an unused request code

//    public void startSMSVerificationListener(View view) {
//        // Start listening for SMS User Consent broadcasts from senderPhoneNumber
//        // The Task<Void> will be successful if SmsRetriever was able to start
//        // SMS User Consent, and will error if there was an error starting.
//        String senderPhoneNumber = "SWOB ONLINE";
//        Task<Void> task = SmsRetriever.getClient(getApplicationContext())
//                .startSmsUserConsent(senderPhoneNumber /* or null */);
//        task.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Log.d(getLocalClassName(), "+ OTP task success");
//            }
//        });
//
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(getLocalClassName(), "+ OTP task failure");
//                e.printStackTrace();
//            }
//        });
//
//    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SMS_CONSENT_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                // Get SMS message content
//                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
//                // Extract one-time code from the message and complete verification
//                // `sms` contains the entire text of the SMS message, so you will need
//                // to parse the string.
//                Log.d(getLocalClassName(), "+ OTP code: " + message);
//                // send one time code to the server
//            }
//        }
//    }


//    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
//                Bundle extras = intent.getExtras();
//                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
//
//                switch (smsRetrieverStatus.getStatusCode()) {
//                    case CommonStatusCodes.SUCCESS:
//                        // Get consent intent
//                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
//                        try {
//                            // Start activity to show consent dialog to user, activity must be started in
//                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
//                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
//                        } catch (ActivityNotFoundException e) {
//                            // Handle the exception ...
//                        }
//                        break;
//                    case CommonStatusCodes.TIMEOUT:
//                        // Time out occurred, handle the error.
//                        break;
//                }
//            }
//        }
//    };

}