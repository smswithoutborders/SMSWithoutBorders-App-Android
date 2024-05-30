package com.example.sw0b_001

import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar

class TextViewActivity : AppCompactActivityCustomized() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_view)

        val myToolbar = findViewById<MaterialToolbar>(R.id.layout_text_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar?.apply {
            this.title = intent.getStringExtra("platform_name")
            setDisplayHomeAsUpEnabled(true)
        }


    }
}