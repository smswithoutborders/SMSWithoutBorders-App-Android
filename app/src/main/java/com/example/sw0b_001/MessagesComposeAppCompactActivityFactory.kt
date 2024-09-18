package com.example.sw0b_001

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Modals.PlatformComposers.EmailComposeModalFragment
import com.example.sw0b_001.Modals.PlatformComposers.MessageComposeModalFragment
import com.example.sw0b_001.Modals.PlatformComposers.TextComposeModalFragment
import com.example.sw0b_001.Models.Messages.EncryptedContent
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class MessagesComposeAppCompactActivityFactory : AppCompactActivityCustomized() {

    protected lateinit var message: EncryptedContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Default).launch {
            message = Datastore.getDatastore(applicationContext).encryptedContentDAO()
                .get(intent.getLongExtra("message_id", -1))
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
                    val id = intent.getStringExtra("id")
                    id?.let {
                        val platforms = Datastore.getDatastore(applicationContext)
                            .storedPlatformsDao().fetch(id)
                        runOnUiThread {
                            showPlatformsModal(platforms)
                        }
                    }
                }
                return true
            }
            R.id.compose_view_edit_menu_delete -> {
                CoroutineScope(Dispatchers.Default).launch {
                    Datastore.getDatastore(applicationContext).encryptedContentDAO()
                        .delete(intent.getLongExtra("message_id", -1))
                    runOnUiThread {
                        finish()
                    }
                }
                return true
            }
        }
        return false
    }

    private fun showPlatformsModal(platforms: StoredPlatformsEntity) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment: Fragment by lazy {
            when(intent.getStringExtra("type")!!) {
                Platforms.Type.EMAIL.type -> EmailComposeModalFragment(platforms, message) { finish() }
                Platforms.Type.TEXT.type -> TextComposeModalFragment(platforms, message) { finish() }
                Platforms.Type.MESSAGE.type -> MessageComposeModalFragment(platforms, message) {
                    finish() }
                else -> TODO()
            }
        }
        fragmentTransaction.add(fragment, "email_compose_tag")
        fragmentTransaction.show(fragment)
        fragmentTransaction.commitNow()
    }
}