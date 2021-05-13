package com.kotme

import app.thelema.action.ActionList
import app.thelema.anim.AnimationPlayer
import app.thelema.app.APP
import app.thelema.audio.AL
import app.thelema.ecs.ECS
import app.thelema.ecs.Entity
import app.thelema.ecs.IEntity
import app.thelema.ecs.component
import app.thelema.fs.FS
import app.thelema.g3d.Blending
import app.thelema.g3d.Material
import app.thelema.g3d.Object3D
import app.thelema.g3d.Scene
import app.thelema.g3d.cam.ActiveCamera
import app.thelema.g3d.light.DirectionalLight
import app.thelema.g3d.mesh.PlaneMesh
import app.thelema.g3d.node.TransformNode
import app.thelema.gl.GL
import app.thelema.gl.GL_LINEAR
import app.thelema.gl.GL_LINEAR_MIPMAP_LINEAR
import app.thelema.gltf.GLTF
import app.thelema.img.Texture2D
import app.thelema.math.Vec4
import app.thelema.res.RES
import app.thelema.shader.ComplexPBRShader
import app.thelema.shader.Shader
import app.thelema.shader.SimpleShader3D
import app.thelema.shader.node.GLSLType
import app.thelema.shader.node.OperationNode
import app.thelema.shader.node.ToneMapNode
import app.thelema.ui.TextureRegionDrawable
import app.thelema.utils.Color
import kotlin.random.Random

object Scene1 {
    private lateinit var mainScene: IEntity

    const val CELL_SIZE = 5f
    const val CELL_SIZE_INV = 1f / CELL_SIZE
    const val CELL_SIZE_HALF = CELL_SIZE * 0.5f
    const val CELLS_NUM = 5
    const val GRID_SIZE = CELL_SIZE * CELLS_NUM
    const val GRID_SIZE_HALF = CELL_SIZE_HALF * CELLS_NUM

    val grid = Array(CELLS_NUM) {
        Array(CELLS_NUM) {
            '.'
        }
    }
    var currentX = 0
        set(value) {
            field = value
            checkGoalAchieved()
        }
    var currentZ = 0
        set(value) {
            field = value
            checkGoalAchieved()
        }
    var stepX = 0
    var stepZ = 1

    val groundColor = Texture2D(0)
    val groundNormals = Texture2D(0)
    val groundORM = Texture2D(0)
    lateinit var groundShader: ComplexPBRShader

    fun rotateStepDirection(direction: Float) {
        val x = stepX
        if (direction >= 0) {
            stepX = stepZ
            stepZ = -x
        } else {
            stepX = -stepZ
            stepZ = x
        }
    }

    private fun checkGoalAchieved() {
        if (currentX == CELLS_NUM - 1 && currentZ == CELLS_NUM - 1 && !GameState.isCompleted()) {
            GameState.current = GameState.Win
            mainScene.component<ActionList>().restart()
            mainScene.component<ActionList>().isRunning = false

            Common.playWin()

            mainScene.entity(Common.KATE_NAME) {
                val player = component<AnimationPlayer>()
                val characterContext = component<CharacterContext>()
                player.animate(characterContext.clappingAnim!!, 0.5f, loopCount = 3)
                player.queue(characterContext.idleAnim!!, 0.5f)
            }

            Talk.dialogLabel.text = "Мы справились!"
            Talk.dialogAvatar.drawable = TextureRegionDrawable(Talk.avatar2)
            Talk.show()
        }
    }

    fun isNextCellEmpty(): Boolean = grid.getOrNull(currentX + stepX)?.getOrNull(currentZ + stepZ) == '.'

    fun setNextCell() {
        currentX += stepX
        currentZ += stepZ
    }

    fun init() {
        mainScene = Entity("Main") {
            component<Scene>()
            component<ActionList>()
        }

        Common.initCharacter(mainScene)

        Common.currentScene = mainScene

        ECS.descriptor({ StepForwardAction() }) {}
        ECS.descriptor({ TurnAction() }) {}

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

        RES.loadTyped<GLTF>("ship.glb") {
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

        AL.newMusic(FS.internal("bg2.ogg")).apply {
            isLooping = true
            play()
        }

        RES.loadTyped<GLTF>("rocks.glb") {
            onLoaded {
                scene?.also { scene ->
                    val rand = Random(7)
                    for (i in 0 until 10) {
                        val x = rand.nextInt(0, CELLS_NUM)
                        val z = rand.nextInt(0, CELLS_NUM)
                        if (grid[x][z] != '#') {
                            grid[x][z] = '#'

                            mainScene.addEntityWithCorrectedName(scene.getOrCreateEntity().copyDeep().apply {
                                name = "rocks"

                                component<TransformNode> {
                                    scale.set(0.25f, 0.25f, 0.25f)
                                    position.set(CELL_SIZE_HALF + CELL_SIZE * x, 0f, CELL_SIZE_HALF + CELL_SIZE * z)
                                    requestTransformUpdate()
                                }
                            })
                        }
                    }
                }
            }
        }

        mainScene.entity("ground") {
            component<Object3D>()
            component<Material> {
                GL.call {
                    shader = groundShader
                }
            }
            component<PlaneMesh> {
                setSize(500f)
                updateMesh()
            }
        }

        mainScene.entity("grid").apply {
            component<Material> {
                shader = Shader(
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
                ).apply {
                    this["tex"] = 0
                    this.onMeshDraw = {
                        SKIN.cell.bind(0)
                        this["viewProj"] = ActiveCamera.viewProjectionMatrix
                        GL.setSimpleAlphaBlending()
                        GL.isBlendingEnabled = true
                    }
                }

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

        mainScene.entity("goal").apply {
            component<Material> {
                shader = SimpleShader3D {
                    color = Vec4(Color.GOLD).apply { a = 0.5f }
                }
                alphaMode = Blending.BLEND
            }
            component<Object3D> {}
            component<PlaneMesh> {
                width = CELL_SIZE
                height = CELL_SIZE
                updateMesh()

                builder.position.set(GRID_SIZE - CELL_SIZE_HALF, 0.1f, GRID_SIZE - CELL_SIZE_HALF)
                builder.applyTransform()
            }
            component<Object3D>()
        }
    }

    fun destroy() {
        mainScene.destroy()
    }
}