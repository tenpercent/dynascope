package com.mobileproj.dynascope.unused

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobileproj.dynascope.unused.AppGLSurfaceView

class DynaScopeMainActivity : AppCompatActivity() {

    private lateinit var glView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glView = AppGLSurfaceView(this)
        setContentView(glView)
    }
}
