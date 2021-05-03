package com.kotme

import app.thelema.action.IAction
import app.thelema.action.RotateYAction
import app.thelema.math.MATH

class RotateRightActionBlock: ActionBlockAdapter() {
    var rotateAction = TurnAction().apply { this.angleLength = RIGHT_RAD }

    override val action: IAction
        get() = rotateAction

    init {
        background = SKIN.darkYellowBackground
        title.text = "Поворот вправо"
        add(title).pad(5f).padRight(10f)
    }
}