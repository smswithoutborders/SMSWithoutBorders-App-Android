package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.GatewayServers._GatewayServersHandler
import com.example.sw0b_001.Models.RecentsRecyclerAdapter
import com.example.sw0b_001.Models.RecentsViewModel
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Security.SecurityHandler
import com.example.sw0b_001.Security.SecurityHelpers
import com.example.sw0b_001.Security.SecurityRSA
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.spec.MGF1ParameterSpec
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * TODO: Security checks
 * - Checks if username is present - if valid username, continue with user in the app
 * - what if username gets spoofed (security keys won't match tho)
 */
class HomepageActivity : AppCompactActivityCustomized() {
    var securityHandler: SecurityHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val myToolbar = findViewById<MaterialToolbar>(R.id.homepage_recents_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.title = null

        securityHandler = SecurityHandler(applicationContext)
        configureRecyclerHandlers()

        findViewById<View>(R.id.homepage_compose_new_btn)
                .setOnClickListener { v -> onComposePlatformClick(v) }

        // TODO: for verification
        //        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        //        registerReceiver(smsVerificationReceiver, intentFilter);
    }
    private fun securityChecks() {
        val securityHandler = SecurityHandler(baseContext)
        if (securityHandler.requiresSyncing()) {
            securityHandler.removeSharedKey()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
            return
        }
        if (SecurityHandler.phoneCredentialsPossible(applicationContext)) {
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            if (!securityHandler.seenBiometricCheckAlwaysOn()) {
                startActivity(Intent(this, AppLockBiometricActivity::class.java))
                finish()
            } else if (!securityHandler.seenBiometricCheckDecryption()) {
                startActivity(Intent(this, MessageLockBiometricsActivity::class.java))
                finish()
            }
        }
    }

    private fun configureRecyclerHandlers() {
        val recentRecyclerAdapter = RecentsRecyclerAdapter(securityHandler)
        val linearLayoutManager = LinearLayoutManager(applicationContext)

        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        val recentRecyclerView = findViewById<RecyclerView>(R.id.recents_recycler_view)
        recentRecyclerView.layoutManager = linearLayoutManager
        recentRecyclerView.adapter = recentRecyclerAdapter
        val recentsViewModel = ViewModelProvider(this)[RecentsViewModel::class.java]

        val encryptedContentDAO = Datastore.getDatastore(applicationContext)
                .encryptedContentDAO()

        recentsViewModel.getMessages(encryptedContentDAO).observe(this) {
            val noRecentMessagesText = findViewById<TextView>(R.id.no_recent_messages)
            recentRecyclerAdapter.submitList(it)

            if (it.isNullOrEmpty())
                noRecentMessagesText.visibility = View.VISIBLE
            else
                noRecentMessagesText.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.homepage_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.homepage_settings_menu -> {
                Log.d(localClassName, "Settings has been clicked")
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return false
    }

    val encryptionDigest: String
        get() = if (SecurityHandler.defaultEncryptionDigest == MGF1ParameterSpec.SHA256) "sha256" else "sha1"


    private fun checkAccountSynchronization() {
        // TODO: should become a WorkManager if fails
        val securityRSA = SecurityRSA(applicationContext)
        val securityHandler = SecurityHandler(applicationContext)
        val gatewayServerList = _GatewayServersHandler.getAllGatewayServers(applicationContext)
        val keystoreAlias = _GatewayServersHandler.buildKeyStoreAlias(gatewayServerList[0].url)
        val future = RequestFuture.newFuture<JSONObject>()
        var msisdn = securityHandler.getMSISDN()
        val msisdnSigned = securityRSA.sign(msisdn.toByteArray(StandardCharsets.UTF_8), keystoreAlias)
        val msisdnSignedEncoded = Base64.encodeToString(msisdnSigned, Base64.DEFAULT)
        val gatewayServerPublicKey = gatewayServerList[0].publicKey
        val encryptedMsisdn = securityRSA.encrypt(msisdn.toByteArray(StandardCharsets.UTF_8),
                SecurityRSA.getPublicKeyFromBase64String(gatewayServerPublicKey))
        msisdn = Base64.encodeToString(encryptedMsisdn, Base64.DEFAULT)
        val jsonBody = JSONObject("{\"msisdn\": \"" + msisdn + "\", " +
                "\"msisdn_signature\": \"" + msisdnSignedEncoded + "\", " +
                "\"mgf1ParameterSpec\": \"" + encryptionDigest + "\"}")
        val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, gatewayServerList[0].composeFullURL()
                + "/v2/sync/users/verification",
                jsonBody, future, future)
        val requestQueue = Volley.newRequestQueue(applicationContext)
        jsonObjectRequest.setRetryPolicy(DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        try {
            requestQueue.add(jsonObjectRequest)
            val response = future[15, TimeUnit.SECONDS]
            if (response.has("shared_key")) {
                try {
                    val rxSharedKey = response["shared_key"] as String
                    val decryptedSharedKey = SecurityHelpers.getDecryptedSharedKey(applicationContext)
                    val decodedRxSharedKey = Base64.decode(rxSharedKey, Base64.DEFAULT)
                    if (String(securityRSA.decrypt(decodedRxSharedKey, keystoreAlias)) != String(decryptedSharedKey, StandardCharsets.UTF_8)) {
                        securityHandler.removeSharedKey()
                        startActivity(Intent(applicationContext, SplashActivity::class.java))
                        finish()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        } catch (e: TimeoutException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            val cause = e.cause
            if (cause is VolleyError) {
                val networkResponse = cause.networkResponse
                if (networkResponse != null && networkResponse.statusCode == 403) {
                    securityHandler.removeSharedKey()
                    startActivity(Intent(applicationContext, SplashActivity::class.java))
                    finish()
                } else if (networkResponse != null && networkResponse.statusCode == 404) {
                    if (BuildConfig.DEBUG) Log.d(getLocalClassName(), "Verification code said it's 404...")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ThreadExecutorPool.executorService.execute {
            checkAccountSynchronization()
        }
    }

    fun onComposePlatformClick(view: View?) {
        showComposeNewPlatformLayout(R.layout.fragment_modal_sheet_compose_platforms)
    }

    fun showComposeNewPlatformLayout(layout: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val homepageComposeNewFragment = HomepageComposeNewFragment(layout)
        fragmentTransaction.add(homepageComposeNewFragment,
                HomepageComposeNewFragment.TAG)
        fragmentTransaction.show(homepageComposeNewFragment)
        fragmentTransaction.commitNow()
    }

    companion object {
        private const val SMS_CONSENT_REQUEST = 2 // Set to an unused request code
        private const val CREDENTIAL_PICKER_REQUEST = 1 // Set to an unused request code
        private const val RESOLVE_HINT = 2 // Set to an unused request code
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
}