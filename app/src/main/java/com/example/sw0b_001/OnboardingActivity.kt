package com.example.sw0b_001

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.fragment.app.findFragment
import androidx.fragment.app.replace
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.KeystoreHelpers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityRSA
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Models.Platforms.PlatformsViewModel
import com.example.sw0b_001.Models.Publisher
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Onboarding.OnboardingComponent
import com.example.sw0b_001.Onboarding.OnboardingPublishExampleFragment
import com.example.sw0b_001.Onboarding.OnboardingFinishedFragment
import com.example.sw0b_001.Onboarding.OnboardingVaultFragment
import com.example.sw0b_001.Onboarding.OnboardingVaultStorePlatformFragment
import com.example.sw0b_001.Onboarding.OnboardingWelcomeFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.net.UnknownHostException
import kotlin.io.encoding.Base64

class OnboardingActivity : AppCompatActivity(), OnboardingComponent.ManageComponentsListing {
    private var fragmentIterator: MutableLiveData<Int> = MutableLiveData<Int>()
    private var fragmentList: ArrayList<OnboardingComponent> = ArrayList<OnboardingComponent>()

    private lateinit var nextButton : MaterialButton
    private lateinit var prevButton : MaterialButton
    private lateinit var dotIndicatorLayout :LinearLayout
    private lateinit var skipAllBtn : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // This decides if to skp the onboarding not to show it
        if(!BuildConfig.IS_ONBOARDING)
            if(OnboardingComponent.getOnboarded(applicationContext)) {
                val intent = Intent(this, HomepageActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
                finish()
            }
        nextButton = findViewById(R.id.onboard_next_button);
        prevButton = findViewById(R.id.onboard_back_button);
        dotIndicatorLayout = findViewById(R.id.onboard_dot_indicator_layout)
        skipAllBtn = findViewById(R.id.onboard_skip_all)

        val onboardingWelcomeFragment = OnboardingWelcomeFragment()
        fragmentList = arrayListOf(onboardingWelcomeFragment)

        configureScreens()
        configureButtonClicks()

        val fragmentIndex = intent.getIntExtra("fragment_index", 0)
        supportFragmentManager.commit {
            findViewById<View>(R.id.onboarding_navigation_controller).visibility = View.GONE
            val fragment = fragmentList[fragmentIndex]
            if(supportFragmentManager.fragments.isNullOrEmpty()) {
                add(R.id.onboarding_fragment_container, fragment, "homepage_fragment")
                setCustomAnimations(R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out)
            } else {
                replace(R.id.onboarding_fragment_container, fragment, "homepage_fragment")
                setCustomAnimations(R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out)
            }
        }
    }

    private fun configureScreens() {
        val viewModel: PlatformsViewModel by viewModels()
        if(BuildConfig.IS_SHOWALL_ONBOARDING) {
            fragmentList.add(OnboardingVaultFragment())
            fragmentList.add(OnboardingVaultStorePlatformFragment())
            fragmentList.add(OnboardingPublishExampleFragment())
        } else {
            if(!UserArtifactsHandler.isCredentials(applicationContext)) {
                fragmentList.add(OnboardingVaultFragment())
            } else {
                val thread = Thread(Runnable {
                    if(viewModel.getSavedCount(applicationContext) < 1) {
                        fragmentList.add(OnboardingVaultStorePlatformFragment())
                    } else {
                        fragmentList.add(OnboardingPublishExampleFragment())
                    }
                })
                thread.start()
                thread.join()
            }
        }
        fragmentList.add(OnboardingFinishedFragment())
    }

    private fun configureButtonClicks() {

        fragmentIterator.value = 0
        var previousValueFromSkip: Int = -1

        nextButton.setOnClickListener {
            supportFragmentManager.commit {
                if(fragmentIterator.value!! + 1 < fragmentList.size) {
                    fragmentIterator.value = fragmentIterator.value!! + 1

                    val fragment: OnboardingComponent = fragmentList[fragmentIterator.value!!]

                    if(fragmentIterator.value!! > 0) {
                        findViewById<View>(R.id.onboarding_navigation_controller).visibility = View.VISIBLE
                    }

                    replace(R.id.onboarding_fragment_container, fragment)
//                    setReorderingAllowed(false)
                    addToBackStack(fragment.javaClass.name)
                    setCustomAnimations(R.anim.slide_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out)

                    iterateButtonText(fragment)
                } else {
                    val intent = Intent(applicationContext, HomepageActivity::class.java)
                    intent.apply {
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }

        prevButton.setOnClickListener {
            supportFragmentManager.commit {
                fragmentIterator.value = if(previousValueFromSkip != -1) previousValueFromSkip
                else fragmentIterator.value!! - 1

                val fragment: OnboardingComponent = fragmentList[fragmentIterator.value!!]
                if(fragmentIterator.value!! < 1) {
                    findViewById<View>(R.id.onboarding_navigation_controller).visibility = View.GONE
                }

//                setReorderingAllowed(true)
                replace(R.id.onboarding_fragment_container, fragment)

                setCustomAnimations(R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out)

                iterateButtonText(fragment)
            }
        }

        skipAllBtn.setOnClickListener {
            previousValueFromSkip = fragmentIterator.value!!
            supportFragmentManager.commit {
                val fragment: OnboardingComponent? =
                        fragmentList[fragmentIterator.value!!].skipOnboardingFragment
                replace(R.id.onboarding_fragment_container, fragment!!)
//                setReorderingAllowed(true)

                setCustomAnimations(R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out)
                iterateButtonText(fragment)
                fragmentIterator.value = fragmentList.size -1
            }
        }
    }

    private fun iterateButtonText(fragment: OnboardingComponent) {
        fragment.getButtonText(applicationContext)
        nextButton.text = fragment.nextButtonText
        prevButton.text = fragment.previousButtonText
        skipAllBtn.text = fragment.skipButtonText

        if(nextButton.text.isNullOrEmpty())
            nextButton.visibility = View.INVISIBLE
        else nextButton.visibility = View.VISIBLE

        if(prevButton.text.isNullOrEmpty())
            prevButton.visibility = View.INVISIBLE
        else prevButton.visibility = View.VISIBLE
    }

    override fun removeComponent(index: Int) {
        fragmentList.removeAt(index)
    }

    override fun removeComponent(component: OnboardingComponent) {
        fragmentList.remove(component)
        fragmentIterator.value = fragmentIterator.value?.minus(1)
    }

    override fun addComponent(component: OnboardingComponent) {
        val lastFragment = fragmentList.removeLast()
        fragmentList.add(component)
        fragmentList.add(lastFragment)
    }

    override fun getFragmentIndex(): Int {
        return fragmentIterator.value!!
    }

    override fun onResume() {
        super.onResume()
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            try {
                Publisher.getAvailablePlatforms(applicationContext, Runnable {
                    runOnUiThread {
                        Toast.makeText(applicationContext,
                            "Failed to refresh available platforms",
                            Toast.LENGTH_SHORT).show()
                    }
                }).let{ json ->
                    json.forEach { it->
                        val url = URL(it.icon_png)
                        it.logo = url.readBytes()
                    }
                    Datastore.getDatastore(applicationContext).availablePlatformsDao()
                        .insertAll(json)
                }
            } catch(e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}