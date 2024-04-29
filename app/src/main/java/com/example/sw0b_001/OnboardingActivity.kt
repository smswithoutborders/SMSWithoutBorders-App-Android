package com.example.sw0b_001

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import com.example.sw0b_001.Database.Datastore
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

class OnboardingActivity : AppCompatActivity(), OnboardingComponent.ManageComponentsListing {
    private var fragmentIterator: MutableLiveData<Int> = MutableLiveData<Int>()
    var fragmentList: ArrayList<OnboardingComponent> = ArrayList<OnboardingComponent>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        configureScreens()
        configureButtonClicks()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                val startFragment = fragmentList[0]

                add(R.id.onboarding_fragment_container, startFragment)
                setReorderingAllowed(true)
            }
        }
    }

    private fun configureScreens() {
        val onboardingVaultStorePlatformFragment =
                OnboardingVaultStorePlatformFragment(applicationContext)
        fragmentList = arrayListOf(OnboardingWelcomeFragment(applicationContext))

        if(!UserArtifactsHandler.isCredentials(applicationContext)) {
            fragmentList.add(OnboardingVaultFragment(applicationContext))
        }
        fragmentList.add(OnboardingFinishedFragment(applicationContext))
    }

    private fun configureButtonClicks() {
        val nextButton = findViewById<MaterialButton>(R.id.onboard_next_button);
        val prevButton = findViewById<MaterialButton>(R.id.onboard_back_button);
        val dotIndicatorLayout = findViewById<LinearLayout>(R.id.onboard_dot_indicator_layout)
        val skipAllBtn = findViewById<MaterialTextView>(R.id.onboard_skip_all)

        fragmentIterator.value = 0
        var previousValueFromSkip: Int = -1

        fragmentIterator.observe(this) {
//            dotIndicatorLayout.removeAllViews()
//            val dots: Array<MaterialTextView> = Array(fragmentList.size) {
//                val materialTextView = MaterialTextView(applicationContext)
//                materialTextView.text = Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY)
//                materialTextView.textSize = 35F
//                materialTextView.setTextColor(resources.getColor(R.color.pending_gray, theme))
//                dotIndicatorLayout.addView(materialTextView)
//                materialTextView
//            }
//            dots[it].setTextColor(resources.getColor(R.color.default_blue, theme))

            nextButton.text = fragmentList[it].nextButtonText
            prevButton.text = fragmentList[it].previousButtonText
            skipAllBtn.text = fragmentList[it].skipButtonText

            if(nextButton.text.isNullOrEmpty())
                nextButton.visibility = View.INVISIBLE
            else nextButton.visibility = View.VISIBLE

            if(prevButton.text.isNullOrEmpty())
                prevButton.visibility = View.INVISIBLE
            else prevButton.visibility = View.VISIBLE
        }


        nextButton.setOnClickListener {
            supportFragmentManager.commit {
                if(fragmentIterator.value!! + 1 < fragmentList.size) {
                    fragmentIterator.value = fragmentIterator.value!! + 1
                    val fragment: Fragment = fragmentList[fragmentIterator.value!!]

                    replace(R.id.onboarding_fragment_container, fragment)
                    setReorderingAllowed(false)
                    addToBackStack(fragment.javaClass.name)
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

                val fragment: Fragment = fragmentList[fragmentIterator.value!!]

                replace(R.id.onboarding_fragment_container, fragment)
                setReorderingAllowed(true)
//                addToBackStack(fragment.javaClass.name)
            }
        }

        skipAllBtn.setOnClickListener {
            previousValueFromSkip = fragmentIterator.value!!
            supportFragmentManager.commit {
                val fragment: OnboardingComponent? =
                        fragmentList[fragmentIterator.value!!].skipOnboardingFragment
                replace(R.id.onboarding_fragment_container, fragment!!)
                setReorderingAllowed(true)
//                addToBackStack(fragment.javaClass.name)

                nextButton.text = fragment.nextButtonText
                prevButton.text = fragment.previousButtonText
                skipAllBtn.text = fragment.skipButtonText
            }
        }
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
}