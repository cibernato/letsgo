package com.example.letsgo.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

/**
 * Created by a_man on 24-01-2018.
 */
class ViewPagerStateAdapter(manager: FragmentManager?) : FragmentStatePagerAdapter(manager!!) {
    private val fragmentsList: MutableList<Fragment> = ArrayList()
    private val fragmentsTitleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return fragmentsList[position]
    }

    override fun getCount(): Int {
        return fragmentsList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentsTitleList[position]
    }

    fun addFrag(fragment: Fragment, title: String) {
        fragmentsList.add(fragment)
        fragmentsTitleList.add(title)
    }
}