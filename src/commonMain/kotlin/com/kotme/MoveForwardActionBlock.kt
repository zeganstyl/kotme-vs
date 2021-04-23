package com.kotme

import app.thelema.action.IAction
import app.thelema.action.MoveForwardAction

class MoveForwardActionBlock: ActionBlockAdapter() {
    var moveAction = MoveForwardAction()

    override val action: IAction
        get() = moveAction

    val length = FloatField()

    init {
        length.get = { moveAction.length }
        length.set = { moveAction.length = it }

        title.text = "Move forward:"
        add(title).padRight(10f)
        add(length).growX()
    }
}