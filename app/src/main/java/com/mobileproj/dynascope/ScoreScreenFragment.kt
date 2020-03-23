package com.mobileproj.dynascope

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.max

class ScoreScreenFragment(private val viewmodel: SensorViewModel): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.score_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<Button>(R.id.ns)?.apply {
            setOnClickListener { viewmodel.resetSessionProgress(); visibility = INVISIBLE }
            performClick()
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
                    progress = viewmodel.progress
                    if (progress >= 100) {
                        view.findViewById<Button>(R.id.ns).visibility = VISIBLE
                    }
                }
            }
        }
    }
}

