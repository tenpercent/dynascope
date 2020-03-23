package com.mobileproj.dynascope

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.max

class ScoreScreenFragment(): Fragment() {
//    private val sensorViewModel: SensorViewModel by lazy {
//        ViewModelProvider(this).get(SensorViewModel::class.java)
//    }

    lateinit var viewmodel: SensorViewModel

    constructor(viewmodel: SensorViewModel): this() {
        this.viewmodel = viewmodel
    }

    private lateinit var contentAdapter: ContentAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.score_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contentAdapter = ContentAdapter(this, viewmodel)
//        viewPager = view.findViewById(R.id.pager)
//        viewPager.adapter = contentAdapter

        view.findViewById<Button>(R.id.ns)?.apply {
            setOnClickListener { viewmodel.resetSessionProgress(); visibility = INVISIBLE }
            visibility = INVISIBLE
        }
        viewmodel.apply {
            registerCounterObserver { c: Int ->
                view.findViewById<TextView>(R.id.totalCounter).text = resources.getQuantityString(R.plurals.score, c, c)
            }
            registerIntensityObserver { f: Float ->
                view.findViewById<ProgressBar>(R.id.intensity).progress = max(0F, f * 100).toInt()
            }
            registerSessionCounterObserver {
                view.findViewById<ProgressBar>(R.id.sessionProgress).apply {
                    progress = (it / 30F).toInt()
                    if (progress >= 100) {
                        view.findViewById<Button>(R.id.ns).visibility = VISIBLE
                    }
                }
            }
        }
    }
}

class ContentAdapter constructor(fragment: Fragment, val viewmodel: SensorViewModel): FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
//        return ScoreScreenFragment()
        return when (position) {
            0 -> {
                Log.d("debugfrag", "attempting to create score screen for position $position")
                ScoreScreenFragment(viewmodel)
            }
            else -> {
                Log.d("debugfrag", "attempting to create settings screen for position $position")
                SettingsFragment()
            }
        }
    }
}

class ContentAdapter2 constructor(activity: FragmentActivity, val viewmodel: SensorViewModel): FragmentStateAdapter(activity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
//        return ScoreScreenFragment()
        return when (position) {
            0 -> {
                Log.d("debugfrag", "attempting to create score screen for position $position")
                ScoreScreenFragment(viewmodel)
            }
            else -> {
                Log.d("debugfrag", "attempting to create settings screen for position $position")
                SettingsFragment()
            }
        }
    }
}