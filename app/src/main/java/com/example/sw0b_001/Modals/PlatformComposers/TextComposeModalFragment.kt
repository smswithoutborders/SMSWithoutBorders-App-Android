package com.example.sw0b_001.Modals.PlatformComposers

import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.Messages.EncryptedContent
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextComposeModalFragment(val platform: StoredPlatformsEntity,
                               val message: EncryptedContent? = null,
                               private val onSuccessCallback: Runnable? = null)
    : BottomSheetDialogFragment(R.layout.fragment_modal_text_compose) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.text_compose_btn)
                .setOnClickListener {
                    processPost(view)
                }

        view.findViewById<EditText>(R.id.tweet_compose_text).apply {
            requestFocus()
            dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0))
            dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0))
        }

        val bottomSheet = view.findViewById<View>(R.id.text_compose_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        message?.let {
            it.encryptedContent.split(":").let {
                view.findViewById<EditText>(R.id.tweet_compose_text).apply {
                    setText(it.subList(1, it.size).joinToString())
                }
            }
        }
    }

    private fun processPost(view: View) {
        val textComposeTextEdit = view.findViewById<EditText>(R.id.tweet_compose_text)
        if(textComposeTextEdit.text.isNullOrEmpty()) {
            textComposeTextEdit.error =
                    getString(R.string.text_compose_you_need_some_content_for_posting)
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            val availablePlatforms = Datastore.getDatastore(requireContext())
                .availablePlatformsDao().fetch(platform.name!!)
            val formattedString =
                processTextForEncryption(textComposeTextEdit.text.toString())

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

    private fun processTextForEncryption(body: String): String {
        return "${platform.account}:$body"
    }
}