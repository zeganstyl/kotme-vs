package com.kotme

import app.thelema.action.IAction
import app.thelema.action.RotateYAction
import app.thelema.math.MATH

class RotateLeftActionBlock: ActionBlockAdapter() {
    var rotateAction = TurnAction().apply { this.angleLength = LEFT_RAD }

    override val action: IAction
        get() = rotateAction

    init {
        background = SKIN.yellowBackground
        add(title).pad(5f).padRight(10f)
        title.text = "Поворот влево"
    }
}