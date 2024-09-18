package com.example.sw0b_001

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import com.example.sw0b_001.Models.GatewayClients.GatewayClient
import com.example.sw0b_001.Onboarding.OnboardingComponent
import com.example.sw0b_001.Onboarding.OnboardingPublishExampleFragment
import com.example.sw0b_001.Onboarding.OnboardingFinishedFragment
import com.example.sw0b_001.Onboarding.OnboardingVaultFragment
import com.example.sw0b_001.Onboarding.OnboardingVaultStorePlatformFragment
import com.example.sw0b_001.Onboarding.OnboardingWelcomeFragment
import com.google.android.material.button.MaterialButton

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
                println("Adding")
                add(R.id.onboarding_fragment_container, fragment, "homepage_fragment")
                setCustomAnimations(R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out)
            } else {
                println("Replacing")
                replace(R.id.onboarding_fragment_container, fragment, "homepage_fragment")
                setCustomAnimations(R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out)
            }
        }
    }

    private fun configureScreens() {
        fragmentList.add(OnboardingVaultFragment())
        fragmentList.add(OnboardingVaultStorePlatformFragment())
        fragmentList.add(OnboardingPublishExampleFragment())
        fragmentList.add(OnboardingFinishedFragment())
    }

    private fun configureButtonClicks() {

        fragmentIterator.value = 0
        var previousValueFromSkip: Int = -1

        nextButton.setOnClickListener {
            supportFragmentManager.commit {
                if(fragmentIterator.value!! + 1 < fragmentList.size) {
                    fragmentIterator.value = fragmentIterator.value!! + 1
                    if(fragmentIterator.value == fragmentList.size -1)
                        modifyLastFragment()
                    else modifyFragment()

                    val fragment: OnboardingComponent = fragmentList[fragmentIterator.value!!]

                    if(fragmentIterator.value!! > 0) {
                        findViewById<View>(R.id.onboarding_navigation_controller).visibility = View.VISIBLE
                    }

                    replace(R.id.onboarding_fragment_container, fragment)
                    setReorderingAllowed(true)
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
                modifyFragment()
                fragmentIterator.value = if(previousValueFromSkip != -1) previousValueFromSkip
                else fragmentIterator.value!! - 1

                val fragment: OnboardingComponent = fragmentList[fragmentIterator.value!!]
                if(fragmentIterator.value!! < 1) {
                    findViewById<View>(R.id.onboarding_navigation_controller).visibility = View.GONE
                }

                setReorderingAllowed(true)
                supportFragmentManager.popBackStackImmediate()
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
                modifyLastFragment()
                val fragment: OnboardingComponent = fragmentList.last()
                replace(R.id.onboarding_fragment_container, fragment)
                setReorderingAllowed(true)

                supportFragmentManager.popBackStackImmediate()

                setCustomAnimations(R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out)
                iterateButtonText(fragment)
                fragmentIterator.value = fragmentList.size -1
            }
        }
    }

    private fun modifyFragment() {
        findViewById<MaterialButton>(R.id.onboard_next_button).text =
            getString(R.string.onboarding_next)

        findViewById<MaterialButton>(R.id.onboard_skip_all).visibility = View.VISIBLE
    }

    private fun modifyLastFragment() {
        findViewById<MaterialButton>(R.id.onboard_next_button).text =
            getString(R.string.onboarding_finish)

        findViewById<MaterialButton>(R.id.onboard_skip_all).visibility = View.INVISIBLE
    }

    private fun iterateButtonText(fragment: OnboardingComponent) {
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
        GatewayClient.refreshGatewayClients(applicationContext) {
//            Toast.makeText(applicationContext, getString(R.string.failed_to_refresh_gateway_clients),
//                Toast.LENGTH_SHORT).show()
        }
    }
}