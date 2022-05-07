package com.example.sw0b_001;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Helpers.CustomHelpers;
import com.example.sw0b_001.Models.GatewayServers.GatewayServers;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.Models.Platforms.PlatformDao;
import com.example.sw0b_001.Models.Platforms.Platforms;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayDao;
import com.example.sw0b_001.Security.SecurityHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EmailComposeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    long emailId;
    private List<GatewayClient> phonenumbers = new ArrayList<>();
    private Platforms platforms;

    Datastore databaseConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_compose);

        Toolbar composeToolbar = (Toolbar) findViewById(R.id.compose_toolbar);
        setSupportActionBar(composeToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        this.databaseConnection = Room.databaseBuilder(getApplicationContext(),
                Datastore.class, Datastore.DatabaseName).build();


        configureForThreading();
    }

    private void configureForThreading() {
        TextView emailTo = findViewById(R.id.email_cc);
        TextView emailSubject = findViewById(R.id.email_subject);
        if(getIntent().hasExtra("recipient") ) {
            emailTo.setText(getIntent().getStringExtra("recipient"));
        }
        if(getIntent().hasExtra("subject") ) {
            emailSubject.setText(getIntent().getStringExtra("subject"));
        }
    }

    private Platforms fetchPlatform(long platformID) throws Throwable {
        final Platforms[] platforms = new Platforms[1];
        Thread fetchPlatformThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PlatformDao platformDao = databaseConnection.platformDao();
                platforms[0] = platformDao.get(platformID);
            }
        });

        try {
            fetchPlatformThread.start();
            fetchPlatformThread.join();
        } catch (InterruptedException e) {
            throw e.fillInStackTrace();
        }

        return platforms[0];
    }

    private List<GatewayClient> fetchGatewayClients() throws Throwable {
        final List<GatewayClient>[] gatewayClients = new List[]{new ArrayList<>()};

        Thread fetchGatewayClientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                GatewayDao gatewayDao = databaseConnection.gatewayDao();
                gatewayClients[0] = gatewayDao.getAll();
            }
        });

        try {
            fetchGatewayClientThread.start();
            fetchGatewayClientThread.join();
        } catch (InterruptedException e) {
            throw e.fillInStackTrace();
        }

        return gatewayClients[0];
    }

    private boolean isFromEmailThread(String subject) {
        long threadId = getIntent().getLongExtra("thread_id", -1);
        // TODO compare the subjects
        if(threadId != -1 )
            return true;

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_compose_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private long createEmailThread(String to, String subject) {
        long threadId[] = {-1};
        Thread storeEmailThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long platformId = getIntent().getLongExtra("platform_id", -1);
                EmailThreads emailThread = new EmailThreads();
                emailThread .setRecipient(to);
                emailThread.setSubject(subject);
                emailThread.setPlatformId(platformId);

                EmailThreadsDao platformsDao = databaseConnection.emailThreadDao();
                threadId[0] = platformsDao.insert(emailThread);
            }
        });

        storeEmailThread.start();
        try {
            storeEmailThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return threadId[0];
    }

    public void storeEmailMessage(String to, String cc, String bcc, String subject, String body, long threadId) {
        Thread storeEmailMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                EmailMessage emailMessage = new EmailMessage();
                emailMessage.setTo(to);
                emailMessage.setCC(cc);
                emailMessage.setBCC(bcc);
                emailMessage.setSubject(subject);
                emailMessage.setBody(body);
                emailMessage.setThreadId(threadId);
                emailMessage.setDatetime(CustomHelpers.getDateTime());

                EmailMessageDao platformsDao = databaseConnection.emailDao();
                emailId = platformsDao.insertAll(emailMessage);
            }
        });

        try {
            storeEmailMessage.start();
            storeEmailMessage.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private GatewayClient getGatewayClientMSISDN() throws Throwable {

        GatewayClient defaultGatewayClient = new GatewayClient();

        List<GatewayClient> gatewayClients = this.fetchGatewayClients();
        for(GatewayClient gatewayClient : gatewayClients) {
            if(gatewayClient.isDefault()) {
                defaultGatewayClient = gatewayClient;
                break;
            }
        }

        return defaultGatewayClient;
    }

    private Platforms getPlatform() {
        Platforms platform = new Platforms();
        try {
            long platformId = getIntent().getLongExtra("platform_id", -1);
            platform = fetchPlatform(platformId);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return platform;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText toEditText = findViewById(R.id.email_to);
        EditText ccEditText = findViewById(R.id.email_cc);
        EditText bccEditText = findViewById(R.id.email_bcc);
        EditText subjectEditText = findViewById(R.id.email_subject);
        EditText bodyEditText = findViewById(R.id.email_body);

        switch (item.getItemId()) {
            case R.id.action_send:
                String to = toEditText.getText().toString();
                String cc = ccEditText.getText().toString();
                String bcc = bccEditText.getText().toString();
                String body = bodyEditText.getText().toString();
                String subject = subjectEditText.getText().toString();

                if(to.isEmpty()) {
                    toEditText.setError("Recipient cannot be empty!");
                    return false;
                }
                if(body.isEmpty()) {
                    bodyEditText.setError("Body should not be empty!");
                    return false;
                }

                long threadId = getIntent().getLongExtra("platform_id", -1);

                if(!this.isFromEmailThread(subject)) {
                    threadId = this.createEmailThread(to, subject);
                }
                this.storeEmailMessage(to, cc, bcc, subject, body, threadId);

                try {

                    Platforms platform = getPlatform();

                    String encryptedContent = processEmailForEncryption(platform.getLetter(), to, cc, bcc, subject, body);
                    Log.d(getLocalClassName(), "[*] size utf8: " + encryptedContent.length());
                    Log.d(getLocalClassName(), "[*] data utf8: " + encryptedContent);

                    String encryptedContentBase64 = Base64.encodeToString(encryptedContent.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                    Log.d(getLocalClassName(), "[*] size base64: " + encryptedContentBase64.length());
                    Log.d(getLocalClassName(), "[*] data base64: " + encryptedContentBase64);

                    GatewayClient gatewayClient = getGatewayClientMSISDN();

                    if(gatewayClient.getMSISDN() == null || gatewayClient.getMSISDN().isEmpty()) {
                        // TODO should have fallback GatewayClients that can be used in the code

                        String defaultSeedFallbackGatewayClientMSISDN = "+237672451860";
                        gatewayClient.setMSISDN(defaultSeedFallbackGatewayClientMSISDN);
                    }

                    Intent defaultSMSAppIntent = transferToDefaultSMSApp(gatewayClient, encryptedContentBase64);

                    if(defaultSMSAppIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(defaultSMSAppIntent);
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }

                    return true;

                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnrecoverableEntryException e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return false;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private List<GatewayServers> getGatewayServers() throws Throwable {
        final List<GatewayServers>[] gatewayServers = new List[]{new ArrayList<>()};
        Thread fetchGatewayClientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                GatewayServersDAO gatewayServerDao = databaseConnection.gatewayServersDAO();
                gatewayServers[0] = gatewayServerDao.getAll();
            }
        });

        try {
            fetchGatewayClientThread.start();
            fetchGatewayClientThread.join();
        } catch (InterruptedException e) {
            throw e.fillInStackTrace();
        }

        return gatewayServers[0];
    }

    private String[] getEncryptEmailContent(String emailContent) throws Throwable {
        SecurityHandler securityHandler = new SecurityHandler(getApplicationContext());
        String randomStringForIv = securityHandler.generateRandom(16);

        GatewayServers gatewayServer = getGatewayServers().get(0);
        String keystoreAlias = GatewayServersHandler.buildKeyStoreAlias(gatewayServer.getUrl() );

        try {
            byte[] encryptedEmailContent = securityHandler.encryptWithSharedKeyAES(randomStringForIv.getBytes(), emailContent.getBytes(StandardCharsets.UTF_8), keystoreAlias);

            return new String[]{randomStringForIv, Base64.encodeToString(encryptedEmailContent, Base64.NO_WRAP)};
        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }


    private String processEmailForEncryption(String platformLetter, String to, String cc, String bcc, String subject, String body) throws Throwable {
        String emailContent = platformLetter + ":" + to + ":" + cc + ":" + bcc + ":" + subject + ":" + body;
        try {
            String[] encryptedIVEmailContent = this.getEncryptEmailContent(emailContent);

            String IV = encryptedIVEmailContent[0];
            String encryptedEmailContent = encryptedIVEmailContent[1];

            final String encryptedContent = IV + encryptedEmailContent;

            return encryptedContent;
        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }

    public Intent transferToDefaultSMSApp(GatewayClient gatewayClient, String encryptedContent) {
        String MSISDN = gatewayClient.getMSISDN();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + MSISDN));
        intent.putExtra("sms_body", encryptedContent);

        return intent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
