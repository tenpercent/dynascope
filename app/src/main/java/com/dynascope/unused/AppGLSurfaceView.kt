package com.mobileproj.dynascope.unused

import android.content.Context
import android.opengl.GLSurfaceView
import com.mobileproj.dynascope.unused.AppGLRenderer

class AppGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: AppGLRenderer
    private val OPENGL_VERSION = 2

    init {
        setEGLContextClientVersion(OPENGL_VERSION)
        renderer = AppGLRenderer()
        setRenderer(renderer)
    }
}
