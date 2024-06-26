package com.example.sw0b_001;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.Messages.EncryptedContentHandler;
import com.example.sw0b_001.Models.Platforms.Platforms;
import com.example.sw0b_001.Models.Platforms._PlatformsHandler;
import com.example.sw0b_001.Models.PublisherHandler;
import com.example.sw0b_001.Models.SMSHandler;
import com.example.sw0b_001.databinding.ActivityMessageComposeBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

public class MessageComposeActivity extends AppCompactActivityCustomized {

    private ActivityMessageComposeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageComposeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar composeToolbar = (Toolbar) findViewById(R.id.message_compose_toolbar);
        setSupportActionBar(composeToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        autoFocusKeyboard(R.id.message_recipient_number_edit_text);
        Intent intent = getIntent();

        if(intent.hasExtra("encrypted_content_id")) {
            populateEncryptedContent();
        }

        TextInputLayout textInputLayout = findViewById(R.id.message_recipient_number_container);
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void populateEncryptedContent() {
        Intent intent = getIntent();
        long encryptedContentId = intent.getLongExtra("encrypted_content_id", -1);
        Datastore databaseConnector = Room.databaseBuilder(getApplicationContext(), Datastore.class,
                Datastore.databaseName).build();

//        final String[] decryptedEmailContent = {""};
    }

    private void populateFields(String decryptedEmailContent) {
        // Parse the input
        String[] decryptedEmailContentComponents = decryptedEmailContent.split(":");
        String to = decryptedEmailContentComponents[1];

        List bodyList = Arrays.asList(decryptedEmailContentComponents).subList(2, decryptedEmailContentComponents.length);
        String body = String.join(":", bodyList);


        EditText toEditText = verifyPhoneNumberFormat(to) ?
                findViewById(R.id.message_recipient_number_edit_text) :
                findViewById(R.id.message_recipient_username_edit_text);

        EditText bodyEditText = findViewById(R.id.message_compose_text);

        toEditText.setText(to);
        bodyEditText.setText(body);
    }

    private void autoFocusKeyboard(int viewId) {
        // Focus
        EditText viewEditText = findViewById(viewId);
        viewEditText.postDelayed(new Runnable() {
            public void run() {
                viewEditText.requestFocus();

                viewEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
                viewEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
            }
        }, 200);
    }

    private String processEmailForEncryption(String platformLetter, String to, String message) {
        return platformLetter + ":" + to + ":" + message;
    }

    private Boolean verifyPhoneNumberFormat(String phonenumber) {
        phonenumber = phonenumber
                .replaceAll("[\\s-]", "");
        return phonenumber.matches("^\\+[1-9]\\d{1,14}$");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText toEditText = findViewById(R.id.message_recipient_number_edit_text);
        EditText groupEditText = findViewById(R.id.message_recipient_username_edit_text);
        EditText messageEditText = findViewById(R.id.message_compose_text);


        switch (item.getItemId()) {
            case R.id.email_compose_menu_action_send:
                String to = new String();

                if(groupEditText.getText().toString().isEmpty()) {
                    to = toEditText.getText().toString();

                    // Till I find a cleaner version
                    if(!verifyPhoneNumberFormat(to)) {
                        toEditText.setError(getString(R.string.message_compose_invalid_number));
                        return false;
                    }
                }
                else
                    to = groupEditText.getText().toString();

                if(to.isEmpty()) {
                    groupEditText.setError(getString(R.string.message_compose_empty_recipient));
                    return false;
                }

                String message = messageEditText.getText().toString();
                if(message.isEmpty()) {
                    messageEditText.setError(getString(R.string.message_compose_empty_body));
                    return false;
                }

                try {
                    long platformId = getIntent().getLongExtra("platform_id", -1);

                    Platforms platforms = _PlatformsHandler.getPlatform(getApplicationContext(), platformId);
                    String formattedContent = processEmailForEncryption(platforms.getLetter(), to, message);
                    String encryptedContentBase64 = PublisherHandler.INSTANCE
                            .formatForPublishing(getApplicationContext(), formattedContent);
//                    String gatewayClientMSISDN = GatewayClientsHandler.getDefaultGatewayClientMSISDN(getApplicationContext());
                    String gatewayClientMSISDN = "";

                    Intent defaultSMSAppIntent = SMSHandler.Companion
                            .transferToDefaultSMSApp(getApplicationContext(), gatewayClientMSISDN,
                                    encryptedContentBase64);
                    if(defaultSMSAppIntent.resolveActivity(getPackageManager()) != null) {
//                        startActivityForResult(defaultSMSAppIntent, RESULT_OK);
                        startActivity(defaultSMSAppIntent);

                        EncryptedContentHandler.store(getApplicationContext(), encryptedContentBase64, gatewayClientMSISDN, platforms.getName());
//                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }

                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_compose_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (1) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor contactCursor = getApplicationContext().getContentResolver().query(contactData, null, null, null, null);
                    if(contactCursor != null) {
                        if (contactCursor.moveToFirst()) {
                            int contactIndexInformation = contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            String number = contactCursor.getString(contactIndexInformation);

                            EditText numberEditText = findViewById(R.id.message_recipient_number_edit_text);
                            numberEditText.setText(number);
                        }
                    }
                }
                break;
        }
    }
}