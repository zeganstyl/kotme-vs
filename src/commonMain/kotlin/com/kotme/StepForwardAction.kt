package com.kotme

import app.thelema.anim.AnimationAction
import app.thelema.anim.AnimationPlayer
import app.thelema.action.ActionAdapter
import app.thelema.action.ActionData
import app.thelema.g3d.node.ITransformNode
import app.thelema.math.IVec3
import app.thelema.math.Vec3
import app.thelema.phys.PhysicsContext
import kotlin.math.abs

class StepForwardAction: ActionAdapter() {
    var passed: Float = 0f

    private val tmp: IVec3 = Vec3()

    override val componentName: String
        get() = "StepForwardAction"

    override var actionData: ActionData = ActionData(0f, null, null)

    var animationAction: AnimationAction? = null

    override fun restart() {
        super.restart()
        passed = 0f
    }

    override fun update(delta: Float): Float {
        if (isRunning) {
            val node = getContextComponent<ITransformNode>()
            val physicsContext = getContextComponent<PhysicsContext>()
            val characterContext = getContextComponent<CharacterContext>()
            if (node != null && physicsContext != null) {
                if (Scene1.isNextCellEmpty()) {
                    node.rotation.rotateVec3(tmp.set(0f, 0f, 1f))

                    val speed = physicsContext.linearVelocity * delta
                    val diff = Scene1.CELL_SIZE - passed
                    if (abs(diff) < speed) {
                        node.position.add(tmp.scl(diff))
                        passed += diff
                        isRunning = false

                        Scene1.setNextCell()
                    } else {
                        node.position.add(tmp.scl(speed))
                        passed += speed
                    }
                    node.requestTransformUpdate(true)

                    val animation = characterContext?.moveAnim
                    val animationAction = animationAction
                    if (animation != null && animationAction == null) {
                        val animationPlayer = getContextComponent<AnimationPlayer>()
                        if (animationPlayer != null) {
                            this.animationAction = animationPlayer.animate(animation, animationTransition, loopCount = -1)
                        }
                    }
                } else {
                    val animation = characterContext?.angryAnim
                    val animationAction = animationAction
                    if (animation != null && animationAction == null) {
                        val animationPlayer = getContextComponent<AnimationPlayer>()
                        if (animationPlayer != null) {
                            this.animationAction = animationPlayer.animate(animation, animationTransition, loopCount = -1)
                        }
                    }
                    if (animationAction != null) {
                        if (animationAction.time > 5f) isRunning = false
                    }
                }
            } else {
                isRunning = false
            }

            if (animationAction != null && !isRunning) {
                animationAction?.end()
                animationAction = null

                getContextComponent<AnimationPlayer>()?.also { player ->
                    characterContext?.idleAnim?.also { player.animate(it, 0.5f, loopCount = -1) }
                }
            }
        }
        return 0f
    }

    companion object {
        const val animationTransition: Float = 0.1f
    }
}