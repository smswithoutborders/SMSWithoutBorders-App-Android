package com.example.sw0b_001.Models.PlatformComposers

import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class TextComposeModalFragment(val platforms: Platforms)
    : BottomSheetDialogFragment(R.layout.fragment_modal_text_compose) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.text_compose_btn)
                .setOnClickListener {
                    processPost(it)
                }

        view.findViewById<EditText>(R.id.tweet_compose_text).apply {
            requestFocus()
            dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0))
            dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0))
        }
    }

    private fun processPost(view: View) {
        val textComposeTextEdit = view.findViewById<EditText>(R.id.tweet_compose_text)
        if(textComposeTextEdit.text.isNullOrEmpty()) {
            textComposeTextEdit.error =
                    getString(R.string.text_compose_you_need_some_content_for_posting)
            return
        }

        val formattedString =
                processTextForEncryption(platforms.letter, textComposeTextEdit.text.toString())
        ComposeHandlers.compose(view.context, formattedString, platforms) {
            dismiss()
        }

    }

    private fun processTextForEncryption(platformLetter: String, body: String): String {
        return "$platformLetter:$body"
    }
}