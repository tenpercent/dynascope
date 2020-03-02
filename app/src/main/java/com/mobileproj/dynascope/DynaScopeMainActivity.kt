package com.mobileproj.dynascope

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DynaScopeMainActivity : AppCompatActivity() {

    private lateinit var glView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glView = AppGLSurfaceView(this)
        setContentView(glView)
    }
}
