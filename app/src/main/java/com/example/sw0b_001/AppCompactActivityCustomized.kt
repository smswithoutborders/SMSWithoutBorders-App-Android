package com.example.sw0b_001

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class AppCompactActivityCustomized : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: check if shared key is available else kill
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(view: View?) {
        implementViewSecurities(view)
        super.setContentView(view)
    }

    private fun implementViewSecurities(view: View?) {
        view!!.filterTouchesWhenObscured = true
    }

}
