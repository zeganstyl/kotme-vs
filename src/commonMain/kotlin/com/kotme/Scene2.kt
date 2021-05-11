package com.kotme

import app.thelema.action.ActionList
import app.thelema.audio.AL
import app.thelema.ecs.ECS
import app.thelema.ecs.Entity
import app.thelema.ecs.IEntity
import app.thelema.ecs.component
import app.thelema.fs.FS
import app.thelema.g3d.Scene
import app.thelema.g3d.light.DirectionalLight
import app.thelema.g3d.node.TransformNode
import app.thelema.gltf.GLTF
import app.thelema.res.RES
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

object Scene2: CoroutineScope {
    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    private lateinit var mainScene: IEntity

    fun init() {
        mainScene = Entity("Main") {
            component<Scene>()
            component<ActionList>()
        }

        Common.initCharacter(mainScene)

        Common.currentScene = mainScene

        ECS.descriptor({ IfAction() }) {}
        ECS.descriptor({ ScanAction() }) {}
        ECS.descriptor({ ForEachAction() }) {}
        ECS.descriptor({ MoveTo() }) {}

        mainScene.entity("Light") {
            component<TransformNode> {
                rotation.setQuaternionByAxis(1f, 0f, 0f, 0.5f)
                requestTransformUpdate()
            }
            component<DirectionalLight> {
                color.set(1f, 0.7f, 0.3f)
                lightPositionOffset = 50f
                lightIntensity = 3f
            }
        }

        async {
            RES.loadTyped<GLTF>("ship.glb") {
                conf.separateThread = true
                onLoaded {
                    scene?.also { scene ->
                        mainScene.addEntity(scene.getOrCreateEntity().copyDeep().apply {
                            name = "ship"
                            component<TransformNode> {
                                position.set(Scene1.GRID_SIZE + 10f, 0f, Scene1.GRID_SIZE + 10f)
                                requestTransformUpdate()
                            }
                        })
                    }
                }
            }

            RES.loadTyped<GLTF>("mars-plane.glb") {
                conf.separateThread = true
                onLoaded {
                    scene?.also { scene ->
                        mainScene.addEntity(scene.getOrCreateEntity().copyDeep().apply {
                            component<TransformNode> {
                                scale.set(2f, 1f, 2f)
                                requestTransformUpdate()
                            }
                            name = "ground"
                        })
                    }
                }
            }

            AL.newMusic(FS.internal("bg2.ogg")).apply {
                volume = 0.3f
                play()
            }

            RES.loadTyped<GLTF>("rocks.glb") {
                conf.separateThread = true
                onLoaded {
                    scene?.also { scene ->
                        val rand = Random(7)
                        for (i in 0 until 10) {
                            val x = rand.nextInt(0, Scene1.CELLS_NUM)
                            val z = rand.nextInt(0, Scene1.CELLS_NUM)
                            if (Scene1.grid[x][z] != '#') {
                                Scene1.grid[x][z] = '#'

                                mainScene.addEntityWithCorrectedName(scene.getOrCreateEntity().copyDeep().apply {
                                    name = "rocks"

                                    component<TransformNode> {
                                        scale.set(0.25f, 0.25f, 0.25f)
                                        position.set(Scene1.CELL_SIZE_HALF + Scene1.CELL_SIZE * x, 0f, Scene1.CELL_SIZE_HALF + Scene1.CELL_SIZE * z)
                                        requestTransformUpdate()
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}