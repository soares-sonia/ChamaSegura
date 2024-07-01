package com.example.queimasegura.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.example.queimasegura.R
import com.example.queimasegura.admin.adapters.ViewPagerAdapter
import com.example.queimasegura.admin.fragments.home.HomeFragment
import com.example.queimasegura.admin.fragments.profile.ProfileFragment
import com.example.queimasegura.admin.fragments.users.UsersFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdminActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity)

        viewPager = findViewById(R.id.viewPager)
        tabs = findViewById(R.id.tabs)

        setUpTabs()
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(RequestsFragment(), "Requests")
        adapter.addFragment(RulesFragment(), "Rules")
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(UsersFragment(), "Users")
        adapter.addFragment(ProfileFragment(), "Profile")
        viewPager.adapter = adapter

        // Use TabLayoutMediator to link the TabLayout and the ViewPager2
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.icon = when (position) {
                0 -> AppCompatResources.getDrawable(this, R.drawable.bonfire)
                1 -> AppCompatResources.getDrawable(this, R.drawable.rules)
                2 -> AppCompatResources.getDrawable(this, R.drawable.home)
                3 -> AppCompatResources.getDrawable(this, R.drawable.users)
                4 -> AppCompatResources.getDrawable(this, R.drawable.profile)
                else -> null
            }
            tab.text = null // Remove the text label
        }.attach()

        // Set "Home" tab as the initial tab
        viewPager.currentItem = 2
    }
}
