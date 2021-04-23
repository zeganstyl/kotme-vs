package com.kotme

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLES30
import android.os.Bundle
import app.thelema.android.AndroidApp
import app.thelema.app.APP
import app.thelema.g3d.cam.ActiveCamera
import app.thelema.g3d.mesh.BoxMesh
import app.thelema.gl.GL
import app.thelema.gl.GL_COLOR_BUFFER_BIT
import app.thelema.math.MATH
import app.thelema.math.Mat4
import app.thelema.math.Vec3
import app.thelema.shader.SimpleShader3D
import app.thelema.utils.Color

class AndroidMain : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = AndroidApp(this)
        setContentView(app.view)

        val box = BoxMesh {
            xSize = 2f
            ySize = 1f
            zSize = 1f
        }

        val shader = SimpleShader3D {
            positionName = box.builder.positionName
            uvName = box.builder.uvName
            renderAttributeName = uvName
            worldMatrix = Mat4()
        }

        ActiveCamera {
            lookAt(Vec3(0f, 3f, -3f), MATH.Zero3)
            near = 0.1f
            far = 100f
            updateCamera()
        }

        GL.isDepthTestEnabled = true
        app.onRender = {
            GL.glClearColor(1f, 0f, 0f, 1f)

            GL.glClear()

//            shader.worldMatrix?.rotate(0f, 1f, 0f, APP.deltaTime)
//
            shader.render(box.mesh)
        }

        app.startLoop()
    }
}