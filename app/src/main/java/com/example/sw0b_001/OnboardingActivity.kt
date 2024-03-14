package com.example.sw0b_001

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.sw0b_001.ui.main.SectionsPagerAdapter
import com.example.sw0b_001.databinding.ActivityOnboardingBinding
import com.make.dots.dotsindicator.DotsIndicator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

//        val tabs: TabLayout = binding.tabs
//        tabs.setupWithViewPager(viewPager)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.onboard_dot_position_view);
        dotsIndicator.setViewPager(viewPager)
        (viewPager.adapter as SectionsPagerAdapter).registerDataSetObserver(dotsIndicator.dataSetObserver)
    }
}