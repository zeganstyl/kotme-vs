package com.kotme

import app.thelema.g3d.mesh.BoxMesh
import app.thelema.g3d.light.DirectionalLight
import app.thelema.math.Vec3
import app.thelema.shader.Shader
import app.thelema.g3d.cam.ActiveCamera
import app.thelema.gl.*

class Sky {
    val light = DirectionalLight().apply {
        node.rotation.setQuaternionByAxis(1f, 0f, 0f, 0.5f)
        node.updateTransform()
        color.set(1f, 0.9f, 0.7f)
    }
    
    val sunPos = Vec3()

    val skybox = BoxMesh.skybox()

    val skyShader = Shader(
        vertCode = """
attribute vec3 POSITION;
varying vec3 vPosition;

uniform mat4 viewProj;
uniform vec3 camPos;
uniform float camFar;

void main () {
    vPosition = POSITION;
    gl_Position = viewProj * vec4(POSITION * camFar + camPos, 1.0);
}""",
        fragCode = """
varying vec3 vPosition;
uniform samplerCube texture;

uniform vec3 uSunColor;
uniform vec3 uSunPosition;
const float uSunDiskSize = 0.05;
const float uSunStrength = 2.0;

void main () {
    vec3 norm = normalize(vPosition);
    
    vec3 up = vec3(0.6, 0.5, 0.1);
    vec3 bottom = vec3(0.8, 0.3, 0.1);
    vec3 skyColor = mix(bottom, up, clamp(norm.y, 0.0, 1.0));
    
    float dist = length(norm - uSunPosition);
	float sunMix = clamp(uSunDiskSize/dist, 0.0, uSunStrength);

	vec3 skySun = mix(skyColor, uSunColor, sunMix);
    //vec3 skySun = skyColor + uSunColor * sunMix;
    
    gl_FragColor = vec4(skySun, 1.0);
}""")

    init {
        light.lightPositionOffset = 100f
        light.setupShadowMaps(1048, 1048)
        light.isShadowEnabled = true
    }

    fun render() {
        skyShader.bind()
        skyShader["viewProj"] = ActiveCamera.viewProjectionMatrix
        skyShader["camFar"] = ActiveCamera.far
        skyShader["camPos"] = ActiveCamera.position

        sunPos.set(light.direction).scl(-1f)
        skyShader["uSunPosition"] = sunPos
        skyShader["uSunColor"] = light.color

        skybox.mesh.render(skyShader)
        GL.glClear(GL_DEPTH_BUFFER_BIT)
    }
}