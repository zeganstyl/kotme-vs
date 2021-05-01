package com.kotme

import app.thelema.action.IAction
import app.thelema.action.RotateYAction
import app.thelema.math.MATH

class RotateLeftActionBlock: ActionBlockAdapter() {
    var rotateAction = RotateYAction().apply { this.angleLength = LEFT_RAD }

    override val action: IAction
        get() = rotateAction

    init {
        add(title).padRight(10f)
        title.text = "Поворот влево"
    }
}