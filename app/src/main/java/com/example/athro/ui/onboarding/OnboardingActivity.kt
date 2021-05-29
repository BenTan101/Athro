package com.example.athro.ui.onboarding

import StepFiveFragment
import StepFourFragment
import StepOneFragment
import StepSixFragment
import StepThreeFragment
import StepTwoFragment
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.athro.R
import com.google.android.material.tabs.TabLayout

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val viewPager: ViewPager = findViewById(R.id.viewPagerOnBoarding)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        val fragments = listOf(
            StepOneFragment(),
            StepTwoFragment(),
            StepThreeFragment(),
            StepFourFragment(),
            StepFiveFragment(),
            StepSixFragment(),
        )

        for (fragment in fragments) viewPagerAdapter.addFragment(fragment)

        viewPager.adapter = viewPagerAdapter

        val tabLayout: TabLayout = findViewById(R.id.tabLayoutIndicator)
        tabLayout.setupWithViewPager(viewPager)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    internal class ViewPagerAdapter(supportFragmentManager: FragmentManager) :
        FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mList: MutableList<Fragment> = ArrayList()
        override fun getItem(i: Int): Fragment {
            return mList[i]
        }

        override fun getCount(): Int {
            return mList.size
        }

        fun addFragment(fragment: Fragment) {
            mList.add(fragment)
        }
    }

    fun close(view: View) {
        onBackPressed()
    }
}