package com.kotme

import app.thelema.anim.AnimationAction
import app.thelema.action.ActionAdapter
import app.thelema.action.ActionData
import app.thelema.anim.AnimationPlayer
import app.thelema.g3d.node.ITransformNode
import app.thelema.math.Vec2
import app.thelema.phys.PhysicsContext
import kotlin.math.abs

open class TurnAction(): ActionAdapter() {
    constructor(angle: Float): this() { angleLength = angle }

    override val componentName: String
        get() = "TurnAction"

    override var actionData: ActionData = ActionData()

    /** In radians */
    var angleLength: Float = 0f

    var passed: Float = 0f

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
                val speed = physicsContext.angularVelocity * (if (angleLength < 0f) -delta else delta)
                val diff = angleLength - passed
                if (abs(diff) < abs(speed)) {
                    node.rotation.setQuaternionByAxis(0f, 1f, 0f, node.rotation.getQuaternionAngleAround(0f, 1f, 0f) + diff)
                    passed += diff
                    isRunning = false

                    Scene1.rotateStepDirection(angleLength)
                } else {
                    node.rotation.setQuaternionByAxis(0f, 1f, 0f, node.rotation.getQuaternionAngleAround(0f, 1f, 0f) + speed)
                    passed += speed
                }
                node.requestTransformUpdate()

                val animation = if (angleLength < 0f) characterContext?.turnRAnim else characterContext?.turnLAnim
                val animationAction = animationAction
                if (animation != null && animationAction == null) {
                    val animationPlayer = getContextComponent<AnimationPlayer>()
                    if (animationPlayer != null) {
                        this.animationAction = animationPlayer.animate(animation, animationTransition, loopCount = -1)
                    }
                }
            } else {
                isRunning = false
            }

            if (animationAction != null && !isRunning) {
                animationAction?.end()
                animationAction = null

                getContextComponent<AnimationPlayer>()?.also { player ->
                    characterContext?.idleAnim?.also { player.animate(it, animationTransition, loopCount = -1) }
                }
            }
        }
        return 0f
    }

    companion object {
        const val animationTransition: Float = 0.1f
    }
}