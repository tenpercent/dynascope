package com.mobileproj.dynascope

import android.opengl.GLES20
import android.opengl.Matrix
import android.opengl.GLSurfaceView
import android.os.SystemClock.uptimeMillis
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AppGLRenderer: GLSurfaceView.Renderer {
    private lateinit var mTriangle: Triangle

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private var startTime: Long = 0

    private var fpsCounterTime: Long = uptimeMillis()
    private var nFrames: Long = 0

    private val rotationSpeedAnglesMs = -0.36f

    override fun onDrawFrame(unused: GL10?) {

        val currentTime: Long = uptimeMillis()

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        val scratch = FloatArray(16)
        val progressTime: Long = currentTime - startTime
        val angle = rotationSpeedAnglesMs * progressTime

        Matrix.setRotateM(rotationMatrix, 0, angle, 0.0f, 0.0f, -1.0f)
        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        // Draw shape
        mTriangle.draw(scratch)

        nFrames += 1

        if (currentTime - fpsCounterTime > 1000) {
            Log.d("rendering", "${1000.0f / nFrames} ms/frame")
            nFrames = 0
            fpsCounterTime = currentTime
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        gl?.glGetString(GL10.GL_VERSION).also { version ->
            Log.d(this.toString(), "Version: $version")
        }
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 0.5f)
        mTriangle = Triangle()
        startTime = uptimeMillis()
    }
}
