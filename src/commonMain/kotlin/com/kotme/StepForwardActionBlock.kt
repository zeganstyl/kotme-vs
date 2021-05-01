package com.kotme

import app.thelema.action.IAction
import app.thelema.action.MoveForwardAction

class StepForwardActionBlock: ActionBlockAdapter() {
    var moveAction = MoveForwardAction().apply { length = CELL_SIZE }

    override val action: IAction
        get() = moveAction

    init {
        title.text = "Шаг вперед"
        add(title).padRight(10f)
    }
}