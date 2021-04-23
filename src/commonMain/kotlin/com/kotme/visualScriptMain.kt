package com.kotme

import app.thelema.action.ActionList
import app.thelema.app.APP
import app.thelema.ecs.ECS
import app.thelema.ecs.Entity
import app.thelema.g3d.Scene
import app.thelema.g3d.cam.ActiveCamera
import app.thelema.g3d.cam.OrbitCameraControl
import app.thelema.g3d.light.DirectionalLight
import app.thelema.g3d.node.TransformNode
import app.thelema.gl.GL
import app.thelema.gltf.GLTF
import app.thelema.math.MATH
import app.thelema.math.Vec3
import app.thelema.phys.PhysicsContext
import app.thelema.res.RES
import app.thelema.utils.Color

fun visualScriptMain() {
    SKIN.init()

    ActiveCamera {
        lookAt(Vec3(0f, 3f, -3f), MATH.Zero3)
        near = 0.1f
        far = 100f
        updateCamera()
    }

    val control = OrbitCameraControl()
    control.listenToMouse()

    val mainScene = Entity("Main") {
        component<Scene>()
        component<ActionList>()

        addEntity(Entity("Light") {
            component<TransformNode> {
                rotation.setQuaternionByAxis(1f, 0f, 0f, 0.5f)
                requestTransformUpdate()
            }
            component<DirectionalLight> {
                color.set(1f, 1f, 1f)
                lightIntensity = 2f
                setupShadowMaps()
                isShadowEnabled = true
            }
        })
    }

    RES.loadTyped<GLTF>("andriod_yellow_2.glb") {
        conf.separateThread = true
        conf.receiveShadows = true
        onLoaded {
            scene?.also { scene ->
                mainScene.addEntity(scene.getOrCreateEntity().copyDeep().apply { name = "Kat" })

                val kat = mainScene.entity("Kat")
                kat.component<PhysicsContext> {
                    linearVelocity = 4f
                    angularVelocity = 1f
                    //runAnim = (animations.first { it.name == "run" } as GLTFAnimation).animation
                }
            }
        }
    }

    RES.loadTyped<GLTF>("ship.glb") {
        conf.separateThread = true
        conf.receiveShadows = true
        onLoaded {
            scene?.also { scene ->
                mainScene.addEntity(scene.getOrCreateEntity().copyDeep().apply {
                    name = "ship"
                    componentTyped<TransformNode>(ECS.TransformNode) {
                        position.set(50f, 0f, 0f)
                        requestTransformUpdate()
                    }
                })
            }
        }
    }

    RES.loadTyped<GLTF>("mars-plane.glb") {
        conf.separateThread = true
        conf.receiveShadows = true
        onLoaded {
            scene?.also { scene ->
                mainScene.addEntity(scene.getOrCreateEntity().copyDeep().apply {
                    name = "ground"
                })
            }
        }
    }

    val mainPanel = MainPanel()
    val vs = VisualScriptPanel()
    vs.fillParent = true
    mainPanel.stage.addActor(vs)

    vs.entityTreeWindow.rootEntity = mainScene

    vs.diagram.rootAction = mainScene

    APP.onUpdate = { delta ->
        ECS.update(mainScene, delta)

        control.update(delta)
        ActiveCamera.updateCamera()

        mainPanel.stage.act(delta)
    }

    GL.isDepthTestEnabled = true
    GL.glClearColor(Color.GRAY)
    APP.onRender = {
        GL.glClear()

        ECS.render(mainScene)

        mainPanel.stage.render()
    }
}

