package com.example.queimasegura.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.example.queimasegura.R
import com.example.queimasegura.user.fragments.home.HomeFragment
import com.example.queimasegura.user.fragments.profile.ProfileFragment
import com.example.queimasegura.user.fragments.fire.FiresFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class UserActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)

        viewPager = findViewById(R.id.viewPager)
        tabs = findViewById(R.id.tabs)

        setUpTabs()
    }

    private fun setUpTabs() {
        val adapter = com.example.queimasegura.user.adapters.ViewPagerAdapter(this)
        adapter.addFragment(FiresFragment(), "Requests")
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(ProfileFragment(), "Profile")
        viewPager.adapter = adapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.icon = when (position) {
                0 -> AppCompatResources.getDrawable(this, R.drawable.bonfire)
                1 -> AppCompatResources.getDrawable(this, R.drawable.home)
                2 -> AppCompatResources.getDrawable(this, R.drawable.profile)
                else -> null
            }
            tab.text = null
        }.attach()

        viewPager.currentItem = 1
    }
}
