package com.kotme

import app.thelema.action.IAction
import app.thelema.action.MoveForwardAction

class StepForwardActionBlock: ActionBlockAdapter() {
    var moveAction = StepForwardAction()

    override val action: IAction
        get() = moveAction

    init {
        background = SKIN.greenBackground
        title.text = "Шаг вперед"
        add(title).pad(5f).padRight(10f)
    }
}