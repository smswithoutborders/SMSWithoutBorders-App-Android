package com.example.sw0b_001

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Modals.PlatformComposers.EmailComposeModalFragment
import com.example.sw0b_001.Modals.PlatformComposers.TextComposeModalFragment
import com.example.sw0b_001.Models.Messages.EncryptedContent
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        configureView()
    }

    private lateinit var message: EncryptedContent
    private fun configureView() {
        ThreadExecutorPool.executorService.execute {
            message = Datastore.getDatastore(applicationContext).encryptedContentDAO()
                .get(intent.getLongExtra("message_id", -1))
            println(message.encryptedContent)
            runOnUiThread {
                message.encryptedContent.split(":").let {
                    findViewById<MaterialTextView>(R.id.layout_text_body).apply {
                        text = it.subList(1, it.size).joinToString()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.compose_view_edit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.compose_view_edit_menu_edit -> {
                CoroutineScope(Dispatchers.Default).launch {
                    val id = intent.getLongExtra("id", -1)
                    if(id > -1) {
                        val platforms = Datastore.getDatastore(applicationContext)
                            .storedPlatformsDao().fetch(id.toInt())
                        runOnUiThread {
                            showPlatformsModal(platforms)
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    private fun showPlatformsModal(platforms: StoredPlatformsEntity) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val textComposeModalFragment = TextComposeModalFragment(platforms, message) {
            finish()
        }
        fragmentTransaction.add(textComposeModalFragment, "text_compose_tag")
        fragmentTransaction.show(textComposeModalFragment)
        fragmentTransaction.commitNow()
    }
}