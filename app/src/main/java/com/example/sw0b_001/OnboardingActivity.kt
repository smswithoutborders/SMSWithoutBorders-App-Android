package com.example.sw0b_001

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import com.example.sw0b_001.Onboarding.OnboardingVaultFragment
import com.example.sw0b_001.Onboarding.OnboardingVaultStoreFragment
import com.example.sw0b_001.Onboarding.WelcomeFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class OnboardingActivity : AppCompatActivity() {
    private var fragmentIterator: MutableLiveData<Int> = MutableLiveData<Int>()
    private val fragmentList: Array<Fragment> = arrayOf(WelcomeFragment(), OnboardingVaultFragment(),
            OnboardingVaultStoreFragment())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                val welcomeFragment = WelcomeFragment()

                add(R.id.onboarding_fragment_container, welcomeFragment)
                setReorderingAllowed(true)
                addToBackStack(WelcomeFragment.javaClass.name)
            }
        }
        configureButtonClicks()
    }

    private fun configureButtonClicks() {
        val nextButton = findViewById<MaterialButton>(R.id.onboard_next_button);
        val prevButton = findViewById<MaterialButton>(R.id.onboard_back_button);
        val dotIndicatorLayout = findViewById<LinearLayout>(R.id.onboard_dot_indicator_layout)
        val skipAllBtn = findViewById<MaterialTextView>(R.id.onboard_skip_all)

        skipAllBtn.setOnClickListener {
            TODO("[Localization] Add this to string.xml for translation")
            TODO("[Implementation] Skip all to get started")
        }

        fragmentIterator.observe(this) {
            dotIndicatorLayout.removeAllViews()
            val dots: Array<MaterialTextView> = Array(fragmentList.size) {
                val materialTextView = MaterialTextView(applicationContext)
                materialTextView.text = Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY)
                materialTextView.textSize = 35F
                materialTextView.setTextColor(resources.getColor(R.color.pending_gray, theme))
                dotIndicatorLayout.addView(materialTextView)
                materialTextView
            }
            dots[it].setTextColor(resources.getColor(R.color.default_blue, theme))
            when {
                it >= fragmentList.size - 1 -> {
                    nextButton.text = "Finish"
                }
                else -> {
                    nextButton.text = "Next"
                }
            }
            when(it) {
                0 -> {
                    prevButton.visibility = View.GONE
                    skipAllBtn.visibility = View.GONE
                }
                else -> {
                    prevButton.visibility = View.VISIBLE
                    skipAllBtn.visibility = View.VISIBLE
                    prevButton.text = "Previous"
                }
            }
        }

        fragmentIterator.value = 0

        nextButton.setOnClickListener {
            supportFragmentManager.commit {
                if(fragmentIterator.value!! + 1 < fragmentList.size) {
                    fragmentIterator.value = fragmentIterator.value!! + 1
                    val fragment: Fragment = fragmentList[fragmentIterator.value!!]

                    replace(R.id.onboarding_fragment_container, fragment)
                    setReorderingAllowed(true)
                    addToBackStack(fragment.javaClass.name)
                }
            }
        }

        prevButton.setOnClickListener {
            supportFragmentManager.commit {
                fragmentIterator.value = fragmentIterator.value!! - 1
                val fragment: Fragment = fragmentList[fragmentIterator.value!!]

                replace(R.id.onboarding_fragment_container, fragment)
                setReorderingAllowed(true)
                addToBackStack(fragment.javaClass.name)
            }
        }

    }
}