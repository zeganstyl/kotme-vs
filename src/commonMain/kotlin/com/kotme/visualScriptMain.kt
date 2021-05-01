package com.kotme

import app.thelema.action.ActionList
import app.thelema.anim.AnimationPlayer
import app.thelema.app.APP
import app.thelema.fs.FS
import app.thelema.audio.AL
import app.thelema.ecs.ECS
import app.thelema.ecs.Entity
import app.thelema.ecs.component
import app.thelema.ecs.getComponentOrNull
import app.thelema.g3d.Blending
import app.thelema.g3d.Material
import app.thelema.g3d.Object3D
import app.thelema.g3d.Scene
import app.thelema.g3d.cam.ActiveCamera
import app.thelema.g3d.light.DirectionalLight
import app.thelema.g3d.mesh.PlaneMesh
import app.thelema.g3d.node.TransformNode
import app.thelema.gl.GL
import app.thelema.gltf.GLTF
import app.thelema.gltf.GLTFAnimation
import app.thelema.math.Vec3
import app.thelema.phys.PhysicsContext
import app.thelema.res.RES
import app.thelema.shader.Shader
import app.thelema.ui.*
import app.thelema.utils.Color

const val LEFT_RAD = 3.141592653589793f * 0.5f
const val RIGHT_RAD = -3.141592653589793f * 0.5f

const val CELL_SIZE = 5f
const val CELL_SIZE_INV = 1f / CELL_SIZE
const val CELL_SIZE_HALF = CELL_SIZE * 0.5f
const val CELLS_NUM = 5
const val GRID_SIZE = CELL_SIZE * CELLS_NUM
const val GRID_SIZE_HALF = CELL_SIZE_HALF * CELLS_NUM

fun visualScriptMain() {
    SKIN.init()

    val cameraDistance = 4f
    val cameraOffset = Vec3(-cameraDistance, cameraDistance, -cameraDistance)

    ActiveCamera {
        lookAt(cameraOffset, Vec3(0f, 1.5f, 0f))
        near = 0.1f
        far = 100f
        updateCamera()
    }

    val mainScene = Entity("Main") {
        component<Scene>()
        component<ActionList>()

        entity("Light").apply {
            component<TransformNode> {
                rotation.setQuaternionByAxis(1f, 0f, 0f, 0.5f)
                requestTransformUpdate()
            }
            component<DirectionalLight> {
                color.set(1f, 0.7f, 0.3f)
                lightPositionOffset = 50f
                lightIntensity = 3f
                setupShadowMaps()
                isShadowEnabled = true
            }
        }
    }

    val stepSound = AL.newSound(FS.internal("step.ogg"))

    RES.loadTyped<GLTF>("a_y.glb") {
        conf.separateThread = true
        conf.receiveShadows = true
        onLoaded {
            scene?.also { scene ->
                mainScene.addEntity(scene.getOrCreateEntity().copyDeep().apply {
                    name = "Кейт"

                    val kate = this

                    component<AnimationPlayer> {
                        animate((animations.first { it.name == "idle" } as GLTFAnimation).animation, 0.1f, loopCount = -1)
                    }

                    component<PhysicsContext> {
                        linearVelocity = 2.5f
                        angularVelocity = 2f
                        moveAnim = (animations.first { it.name == "walk" } as GLTFAnimation).animation.apply {
                            actionTrack.add(0.4f) {
                                stepSound.play(0.3f)
                            }
                            actionTrack.add(0.9f) {
                                stepSound.play(0.3f)
                            }
                        }
                        idleAnim = (animations.first { it.name == "idle" } as GLTFAnimation).animation
                        rotateAnim = (animations.first { it.name == "turn_r" } as GLTFAnimation).animation
                    }

                    mainScene.component<ActionList> { this.context = kate }

                    component<TransformNode> {
                        position.set(CELL_SIZE_HALF, 0f, CELL_SIZE_HALF)
                    }
                })
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
                    component<TransformNode> {
                        position.set(GRID_SIZE + 10f, 0f, GRID_SIZE + 10f)
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
        conf.receiveShadows = true
        onLoaded {
            scene?.also { scene ->
                mainScene.addEntity(scene.getOrCreateEntity().copyDeep().apply {
                    name = "rocks"

                    component<TransformNode> {
                        scale.set(0.25f, 0.25f, 0.25f)
                        position.set(CELL_SIZE_HALF + CELL_SIZE * 3, 0f, CELL_SIZE_HALF + CELL_SIZE * 2)
                        requestTransformUpdate()
                    }
                })
            }
        }
    }

    val vsPanel = VisualScriptPanel {
        entityTreeWindow.rootEntity = mainScene
        diagram.rootAction = mainScene
    }

    val restartButton = PlainButton("Запустить")
    restartButton.addAction {
        vsPanel.execute()
    }

    val titlePanel = HBox {
        align = Align.left
        add(Label("KOTme", SKIN.kotmeTitleLabel)).pad(10f)
        add(restartButton).pad(10f)
    }

    val dialogPanel = HBox {

    }

    val split = MultiSplitPane(false)
    split.touchable = Touchable.ChildrenOnly

    val expandScriptButton = PlainButton("<<")
    expandScriptButton.addAction {
        split.setSplit(0, if (split.splits[0] > 0f) 0f else 0.4f)
        split.invalidateHierarchy()
        expandScriptButton.text = if (expandScriptButton.text == ">>") "<<" else ">>"
    }

    split.setWidgets(
        vsPanel,
        HBox {
            add(expandScriptButton).growY().width(50f)
            add(Actor()).grow()
        }
    )
    split.setSplit(0, 0.4f)

    val mainPanel = MainPanel()
    val root = Table().apply {
        fillParent = true

        add(VBox {
            add(Stack {
                add(UIImage(SKIN.bgImage, scaling = Scaling.fill).apply { fillParent = true })
                add(titlePanel)
            }).growX()
            add(split).grow()
            add(dialogPanel).growX()
        }).grow()
    }
    mainPanel.stage.addActor(root)

    APP.onUpdate = { delta ->
        ECS.update(mainScene, delta)

        mainScene.getEntityByName("Кейт")?.getComponentOrNull<TransformNode>()?.worldMatrix?.getTranslation(ActiveCamera.position)?.also {
            it += cameraOffset
        }

        ActiveCamera.updateCamera()

        mainPanel.stage.act(delta)
    }

    val sky = Sky()

    val gridShader = Shader(
        vertCode = """
attribute vec3 POSITION;
varying vec2 uv;
uniform mat4 viewProj;

void main() {
    uv = POSITION.xz * $CELL_SIZE_INV;
    gl_Position = viewProj * vec4(POSITION, 1.0);
}
""",
        fragCode = """
uniform sampler2D tex;
varying vec2 uv;

void main() {
    gl_FragColor = texture2D(tex, uv);
}
"""
    )

    gridShader["tex"] = 0
    gridShader.onMeshDraw = {
        SKIN.cell.bind(0)
        this["viewProj"] = ActiveCamera.viewProjectionMatrix
        GL.setSimpleAlphaBlending()
        GL.isBlendingEnabled = true
    }

    mainScene.entity("grid").apply {
        component<Material> {
            shader = gridShader
            alphaMode = Blending.BLEND
        }
        component<Object3D> {}
        component<PlaneMesh> {
            width = GRID_SIZE
            height = GRID_SIZE
            updateMesh()

            builder.position.set(GRID_SIZE_HALF, 0.05f, GRID_SIZE_HALF)
            builder.applyTransform()
        }
        component<Object3D>()
    }

    APP.onRender = {
        sky.render()
        ECS.render(mainScene)

        mainPanel.stage.render()
    }
}