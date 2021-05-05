package com.kotme

import app.thelema.action.ActionList
import app.thelema.anim.AnimationPlayer
import app.thelema.app.APP
import app.thelema.fs.FS
import app.thelema.audio.AL
import app.thelema.audio.ISound
import app.thelema.ecs.*
import app.thelema.g2d.Sprite
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
import app.thelema.img.Texture2D
import app.thelema.math.Vec3
import app.thelema.math.Vec4
import app.thelema.phys.PhysicsContext
import app.thelema.res.RES
import app.thelema.shader.Shader
import app.thelema.shader.SimpleShader3D
import app.thelema.ui.*
import app.thelema.utils.Color
import kotlin.random.Random

lateinit var mainScene: IEntity

const val LEFT_RAD = 3.141592653589793f * 0.5f
const val RIGHT_RAD = -3.141592653589793f * 0.5f

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

var timer = 20f

var state = 0

const val KATE_NAME = "Кейт"

var winSound: ISound? = null

fun checkGoalAchieved() {
    if (currentX == CELLS_NUM - 1 && currentZ == CELLS_NUM - 1 && state == 0) {
        state = 1
        mainScene.component<ActionList>().restart()
        mainScene.component<ActionList>().isRunning = false

        winSound?.play()

        mainScene.entity(KATE_NAME) {
            val player = component<AnimationPlayer>()
            val characterContext = component<CharacterContext>()
            player.animate(characterContext.clappingAnim!!, 0.5f, loopCount = 3)
            player.queue(characterContext.idleAnim!!, 0.5f)
        }
    }
}

fun visualScriptMain() {
    mainScene = Entity("Main") {
        component<Scene>()
        component<ActionList>()
    }

    SKIN.init()

    ECS.descriptor({ CharacterContext() }) {}
    ECS.descriptor({ StepForwardAction() }) {}
    ECS.descriptor({ TurnAction() }) {}

    val cameraDistance = 4f
    val cameraOffset = Vec3(-cameraDistance, cameraDistance, -cameraDistance)

    ActiveCamera {
        lookAt(cameraOffset, Vec3(0f, 1.5f, 0f))
        near = 0.1f
        far = 100f
        updateCamera()
    }

    mainScene.entity("Light") {
        component<TransformNode> {
            rotation.setQuaternionByAxis(1f, 0f, 0f, 0.5f)
            requestTransformUpdate()
        }
        component<DirectionalLight> {
            color.set(1f, 0.7f, 0.3f)
            lightPositionOffset = 50f
            lightIntensity = 3f
            //setupShadowMaps()
            //isShadowEnabled = true
        }
    }

    val stepSound = AL.newSound(FS.internal("step.ogg"))

    winSound = AL.newSound(FS.internal("win.ogg"))

    RES.loadTyped<GLTF>("kate.glb") {
        conf.separateThread = true
        conf.receiveShadows = true
        onLoaded {
            scene?.also { scene ->
                mainScene.addEntity(scene.getOrCreateEntity().copyDeep().apply {
                    name = KATE_NAME

                    val kate = this

                    component<AnimationPlayer> {
                        animate((animations.first { it.name == "idle" } as GLTFAnimation).animation, 0.1f, loopCount = -1)
                    }
                    component<PhysicsContext> {
                        linearVelocity = 2.5f
                        angularVelocity = 2f
                    }
                    component<CharacterContext> {
                        moveAnim = (animations.first { it.name == "walk" } as GLTFAnimation).animation.apply {
                            actionTrack.add(0.4f) {
                                stepSound.play(0.3f)
                            }
                            actionTrack.add(0.9f) {
                                stepSound.play(0.3f)
                            }
                        }
                        idleAnim = (animations.first { it.name == "idle" } as GLTFAnimation).animation
                        turnRAnim = (animations.first { it.name == "turn_r" } as GLTFAnimation).animation
                        angryAnim = (animations.first { it.name == "angry" } as GLTFAnimation).animation
                        turnLAnim = (animations.first { it.name == "turn_l" } as GLTFAnimation).animation
                        clappingAnim = (animations.first { it.name == "clapping" } as GLTFAnimation).animation
                    }
                    component<TransformNode> {
                        position.set(CELL_SIZE_HALF, 0f, CELL_SIZE_HALF)
                    }

                    mainScene.component<ActionList> { this.context = kate }
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

    val vsPanel = VisualScriptPanel {
        entityTreeWindow.rootEntity = mainScene
        diagram.rootAction = mainScene
    }

    val restartButton = PlainButton("Запустить")
    restartButton.addAction {
        vsPanel.execute()
    }

    val timerLabel = Label("00:00", LabelStyle { font = SKIN.monoFont })

    val titlePanel = HBox {
        align = Align.left
        add(Label("KOTme", SKIN.kotmeTitleLabel)).pad(10f)
        add(restartButton).pad(10f)
        add(Widget()).growX()

        add(Button(ButtonStyle().apply {
            Texture2D().load("gear.png") {
                up = TextureRegionDrawable(this)
                over = SpriteDrawable(Sprite(this).apply { color.set(0f, 1f, 0f, 1f) })
                down = SpriteDrawable(Sprite(this).apply { color.set(0f, 1f, 1f, 1f) })
            }
        }) {
            addAction {
                println("menu")
            }
        }).pad(10f)
    }

    val split = MultiSplitPane(false)
    split.touchable = Touchable.ChildrenOnly

    val expandScriptButton = PlainButton("<<")
    expandScriptButton.addAction {
        split.setSplit(0, if (split.splits[0] > 0f) 0f else 0.4f)
        split.invalidateHierarchy()
        expandScriptButton.text = if (split.splits[0] > 0f) "<<" else ">>"
    }

    split.setWidgets(
        vsPanel,
        HBox {
            add(expandScriptButton).growY().width(50f)
            add(Actor()).grow()
        }
    )
    split.setSplit(0, 0.4f)
    split.invalidateHierarchy()

    val dialogLabel = Label("""
Кажется нас немного отбросило от места падения.
Моя встроенная система навигации кажется накрылась.
Ты поможешь мне добраться до коробля?
Соедени блоки в нужном порядке, чтобы можно было дойти до места крушения.
""".trimIndent())
    dialogLabel.style = LabelStyle {
        fontColor.set(0f, 0f, 0f, 1f)
        background = SKIN.dialogBackground
    }
    dialogLabel.setWrap(true)
    dialogLabel.alignH = 0
    dialogLabel.alignV = 0
    dialogLabel.invalidate()

    val dialogAvatar = UIImage().apply {
        Texture2D().load("icon_yellow 1.png") { drawable = TextureRegionDrawable(this) }
    }
    dialogAvatar.scaling = Scaling.fit
    dialogAvatar.touchable = Touchable.Disabled

    val dialogSplit = MultiSplitPane(true)
    dialogSplit.touchable = Touchable.ChildrenOnly
    dialogSplit.setWidgets(
        Actor().apply { touchable = Touchable.Disabled },
        Stack {
            add(UIImage {
                scaling = Scaling.fillX
                Texture2D().load("bg-bottom.png") {
                    drawable = TextureRegionDrawable(this)
                }
                alignV = -1
            })
            add(HBox {
                touchable = Touchable.ChildrenOnly
                add(Stack {
                    add(dialogLabel)
                    add(Table {
                        align = Align.bottomRight
                        add(TextButton(">> Далее >>") {
                            addAction { dialogSplit.setSplit(0, 1f) }
                        }).pad(10f)
                    })
                }).grow().pad(10f).height(130f).align(Align.bottom)
                add(dialogAvatar).width(200f).height(200f)
            })
        }
    )
    dialogSplit.setSplit(0, 1f)

    val bottomPanel = HBox {
        add(timerLabel).pad(10f)

        add(Widget()).grow()

        add(Button(ButtonStyle().apply {
            Texture2D().load("chat.png") {
                up = TextureRegionDrawable(this)
                over = SpriteDrawable(Sprite(this).apply { color.set(0f, 1f, 0f, 1f) })
                down = SpriteDrawable(Sprite(this).apply { color.set(0f, 1f, 1f, 1f) })
            }
        }) {
            addAction {
                dialogSplit.setSplit(0, 0f)
            }
        }).pad(10f)
    }

    val hud = VBox {
        add(titlePanel).growX()
        add(split).grow()
        add(bottomPanel).growX()
    }

    val dialogPanel = VBox {
        touchable = Touchable.ChildrenOnly
        add(Actor().apply { touchable = Touchable.Disabled }).grow()
        add(dialogSplit).growX().height(200f)
    }

    val mainStack = Stack {
        fillParent = true
        add(hud)
        add(dialogPanel)
    }

    val mainPanel = MainPanel()
    mainPanel.stage.addActor(mainStack)

    APP.onUpdate = { delta ->
        ECS.update(mainScene, delta)

        if (timer > 0) {
            timer -= delta
            if (timer < 0f) timer = 0f
            val minutes = ((timer / 60).toInt() % 59).toString().padStart(2, '0')
            val seconds = (timer.toInt() % 59).toString().padStart(2, '0')
            timerLabel.text = "$minutes:$seconds"
        }

        mainScene.getEntityByName(KATE_NAME)?.getComponentOrNull<TransformNode>()?.worldMatrix?.getTranslation(ActiveCamera.position)?.also {
            it += cameraOffset
        }

        ActiveCamera.updateCamera()

        mainPanel.stage.update(delta)
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

    APP.onRender = {
        sky.render()
        ECS.render(mainScene)

        mainPanel.stage.render()
    }
}