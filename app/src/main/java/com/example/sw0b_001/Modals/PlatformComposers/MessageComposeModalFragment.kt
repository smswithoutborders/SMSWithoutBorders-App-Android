package com.example.sw0b_001.Modals.PlatformComposers

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.provider.ContactsContract
import android.text.Editable
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.Messages.EncryptedContent
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.example.sw0b_001.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageComposeModalFragment(val platform: StoredPlatformsEntity,
                                  val message: EncryptedContent? = null,
                                  private val onSuccessCallback: Runnable? = null):
    BottomSheetDialogFragment(R.layout.fragment_modal_message_compose) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeToolbar = view.findViewById<MaterialToolbar>(R.id.message_compose_toolbar)

        val bottomSheet = view.findViewById<View>(R.id.message_compose_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        composeToolbar?.apply {
            title = platform.name
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.email_compose_menu_action_send -> {
                        sendMessage(view)
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }
        }
        configureView(view)
    }

    private fun configureView(view: View) {
        autoFocusKeyboard(view)

        message?.let {
            populateEncryptedContent(view)
        }

        val textInputLayout = view.findViewById<TextInputLayout>(R.id.message_recipient_number_container)
        textInputLayout.setEndIconOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }

    private fun populateEncryptedContent(view: View) {
        message?.encryptedContent?.split(":").let {
            val toEditText = view.findViewById<EditText>(R.id.message_recipient_number_edit_text)
            val messageEditText = view.findViewById<EditText>(R.id.message_compose_text)
            toEditText.text = Editable.Factory.getInstance().newEditable(it!![1])
            messageEditText.text = Editable.Factory.getInstance().newEditable(it[2])
        }
    }

    private fun autoFocusKeyboard(view: View) {
        val viewEditText = view.findViewById<EditText>(R.id.message_recipient_number_edit_text)
        viewEditText.postDelayed({
            viewEditText.requestFocus()

            viewEditText.dispatchTouchEvent(
                MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN,
                    0f,
                    0f,
                    0
                )
            )
            viewEditText.dispatchTouchEvent(
                MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP,
                    0f,
                    0f,
                    0
                )
            )
        }, 200)
    }

    private fun processMessageForEncryption(to: String, message: String ):
            String {
        return "${platform.account}:$to:$message"
    }

    private fun verifyPhoneNumberFormat(phonenumber: String): Boolean {
        val newPhoneNumber = phonenumber
            .replace("[\\s-]".toRegex(), "")
        return newPhoneNumber.matches("^\\+[1-9]\\d{1,14}$".toRegex())
    }

    private fun sendMessage(view: View) {
        val toEditText = view.findViewById<EditText>(R.id.message_recipient_number_edit_text)
        val messageEditText = view.findViewById<EditText>(R.id.message_compose_text)

        if (toEditText.text.isNullOrBlank()) {
            toEditText.error = getString(R.string.message_compose_empty_recipient)
            return
        }

        if (!verifyPhoneNumberFormat(toEditText.text.toString())) {
            toEditText.error = getString(R.string.please_verify_the_phone_number_is_valid)
            return
        }

        if (messageEditText.text.isNullOrEmpty()) {
            messageEditText.error = getString(R.string.message_compose_empty_body)
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            val availablePlatforms = Datastore.getDatastore(requireContext())
                .availablePlatformsDao().fetch(platform.name!!)
            val formattedString =
                processMessageForEncryption(toEditText.text.toString(),
                    messageEditText.text.toString())

            try {
                ComposeHandlers.compose(requireContext(), formattedString, availablePlatforms, platform) {
                    onSuccessCallback?.let { it.run() }
                    dismiss()
                }
            } catch(e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    public override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        when (reqCode) {
            (1) -> if (resultCode == RESULT_OK) {
                val contactData = data!!.data
                val contactCursor = requireContext().contentResolver.query(
                    (contactData)!!, null, null, null, null
                )
                if (contactCursor != null) {
                    if (contactCursor.moveToFirst()) {
                        val contactIndexInformation =
                            contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val number = contactCursor.getString(contactIndexInformation).filter {
                            !it.isWhitespace()
                        }

                        val numberEditText = requireView()
                            .findViewById<EditText>(R.id.message_recipient_number_edit_text)
                        numberEditText.setText(number)
                    }
                }
            }
        }
    }
}