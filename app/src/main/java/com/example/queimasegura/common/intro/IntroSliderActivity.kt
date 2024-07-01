package com.example.queimasegura.common.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.queimasegura.R
import com.example.queimasegura.common.login.LoginActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class IntroSliderActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var introAdapter: IntroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        introAdapter = IntroAdapter()
        viewPager.adapter = introAdapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var currentPosition = 0

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                if (position == introAdapter.itemCount - 1) {
                    findViewById<View>(R.id.lastSlide).setOnClickListener {
                        startActivity(Intent(this@IntroSliderActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        })
    }
}
