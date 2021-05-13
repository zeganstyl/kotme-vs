package com.kotme

import app.thelema.action.ActionList
import app.thelema.anim.AnimationPlayer
import app.thelema.app.APP
import app.thelema.audio.AL
import app.thelema.audio.ISound
import app.thelema.ecs.ECS
import app.thelema.ecs.IEntity
import app.thelema.ecs.component
import app.thelema.ecs.getComponentOrNull
import app.thelema.fs.FS
import app.thelema.g3d.cam.ActiveCamera
import app.thelema.g3d.node.TransformNode
import app.thelema.gltf.GLTF
import app.thelema.gltf.GLTFAnimation
import app.thelema.img.Texture2D
import app.thelema.input.KB
import app.thelema.input.MOUSE
import app.thelema.math.Vec3
import app.thelema.phys.PhysicsContext
import app.thelema.res.RES
import app.thelema.ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

object Common: CoroutineScope {
    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    var timer = 20f

    const val KATE_NAME = "Кейт"

    var winSound: ISound? = null

    private var dragItemTouchableState: Touchable = Touchable.ChildrenOnly
    private var dragItem: Actor? = null
        set(value) {
            val oldValue = field
            if (oldValue != null) {
                stage.removeActor(oldValue)
            }
            field = value
            if (value != null) {
                dragItemTouchableState = value.touchable
                stage.addActor(value)
            }
        }

    val timerLabel = Label("00:00", LabelStyle { font = SKIN.monoFont })

    lateinit var stage: Stage

    var currentScene: IEntity? = null
        set(value) {
            field = value
            if (value != null) VisualScriptPanel.setScene(value)
        }

    val backgroundImage = UIImage {
        scaling = Scaling.fillX
    }

    val hud = VBox {
        // title panel
        add(HBox {
            align = Align.left

            add(Label("KOTme", SKIN.kotmeTitleLabel)).pad(10f)

            add(TextButton("Запустить") {
                addAction { VisualScriptPanel.execute() }
            }).pad(10f)

            add(Widget()).growX()

            add(Button(SKIN.gearButton) {
                addAction {
                    println("menu")
                }
            }).pad(10f)
        }).growX()

        add(VisualScriptPanel.panel).grow()

        // bottom panel
        add(HBox {
            //add(timerLabel).pad(10f)

            add(Widget()).grow()

            add(Button(SKIN.chatButton) {
                addAction { Talk.show() }
            }).pad(10f)
        }).growX()
    }

    lateinit var sky: Sky

    fun initCharacter(sceneEntity: IEntity) {
        RES.loadTyped<GLTF>("kate.glb") {
            onLoaded {
                scene?.also { scene ->
                    sceneEntity.addEntity(scene.getOrCreateEntity().copyDeep().apply {
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
                            val stepSound = AL.newSound(FS.internal("step.ogg"))

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
                            position.set(Scene1.CELL_SIZE_HALF, 0f, Scene1.CELL_SIZE_HALF)
                        }

                        sceneEntity.component<ActionList> { this.customContext = kate }
                    })
                }
            }
        }
    }

    val loadIcon = UIImage {
        scaling = Scaling.none
    }

    val loadProgress = ProgressBar(0f, 1f, 0.01f, style = SKIN.loadProgress).apply {
        setAnimateDuration(1f)
    }

    val loadingScreen = Table {
        background = SKIN.background
        align = Align.center
        add(loadIcon).newRow()
        add(loadProgress).width(200f).height(20f).padTop(50f)
    }

    val rootStack = Stack {
        fillParent = true

        add(backgroundImage)
        add(hud)
        add(Talk.panel)
    }

    fun showLoading() {
        rootStack.addActor(loadingScreen)
    }

    fun hideLoading() {
        rootStack.removeActor(loadingScreen)
    }

    fun init() {
        sky = Sky()

        ECS.descriptor({ CharacterContext() }) {}

        stage = Stage()
        SKIN.init()

        KB.addListener(stage)
        MOUSE.addListener(stage)

        stage.addListener(object : InputListener {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val target = event.target
                if (target is IDragItemProvider) {
                    dragItem = target.provideDraggableItem()
                    val dragItem = dragItem
                    if (dragItem != null) {
                        dragItem.setPosition(x, y)
                        dragItem.touchable = Touchable.Disabled
                    }
                }
                return true
            }

            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                dragItem?.setPosition(x, y)
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                val receiver = stage.hit(x, y, true)
                if (receiver is IDragItemReceiver) {
                    val dragItem = dragItem
                    if (dragItem != null) {
                        dragItem.color.a = 1f
                        dragItem.touchable = dragItemTouchableState
                        receiver.receiveDraggableItem(dragItem)
                    }
                }
                dragItem = null
            }
        })

        winSound = AL.newSound(FS.internal("win.ogg"))

        Texture2D().load("load-icon.png") {
            loadIcon.drawable = TextureRegionDrawable(this)
            loadIcon.originX = this.width * 0.5f
            loadIcon.originY = this.height * 0.5f
        }

        stage.addActor(rootStack)

        val cameraDistance = 4f
        val cameraOffset = Vec3(-cameraDistance, cameraDistance, -cameraDistance)

        ActiveCamera {
            lookAt(cameraOffset, Vec3(0f, 1.5f, 0f))
            near = 0.1f
            far = 100f
            updateCamera()
        }

        APP.onUpdate = { delta ->
            if (loadingScreen.parent != null) loadIcon.rotateBy(-delta * 2f)

            if (currentScene != null) ECS.update(currentScene!!, delta)

            if (timer > 0) {
                timer -= delta
                if (timer < 0f) timer = 0f
                val minutes = ((timer / 60).toInt() % 59).toString().padStart(2, '0')
                val seconds = (timer.toInt() % 59).toString().padStart(2, '0')
                timerLabel.text = "$minutes:$seconds"
            }

            currentScene?.getEntityByName(KATE_NAME)?.getComponentOrNull<TransformNode>()?.worldMatrix?.getTranslation(
                ActiveCamera.position)?.also {
                it += cameraOffset
            }

            currentScene?.component<ActionList>()?.also {
                if (GameState.isStarted()) {
                    GameState.current = if (it.isRunning) GameState.RunningScript else GameState.Playing
                }
            }

            ActiveCamera.updateCamera()

            stage.update(delta)
        }

        APP.onRender = {
            sky.render()
            if (currentScene != null) ECS.render(currentScene!!)

            stage.render()
        }
    }

    fun playWin() {
        winSound?.play()
    }
}