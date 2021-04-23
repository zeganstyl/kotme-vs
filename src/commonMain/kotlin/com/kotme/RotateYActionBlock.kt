package com.kotme

import app.thelema.action.IAction
import app.thelema.action.RotateYAction
import app.thelema.math.MATH

class RotateYActionBlock: ActionBlockAdapter() {
    var rotateAction = RotateYAction()

    override val action: IAction
        get() = rotateAction

    val angularLength = FloatField()

    init {
        angularLength.get = { rotateAction.angleLength * MATH.radDeg }
        angularLength.set = { rotateAction.angleLength = it * MATH.degRad }

        title.text = "Rotate Y:"
        add(title).padRight(10f)
        add(angularLength).growX()
    }
}