package com.kotme

import app.thelema.ui.*

class ActionBlockSocket: Container<Actor>(), IDragItemReceiver {
    var onDropped: (actor: Actor) -> Unit = {}

    init {
        touchable = Touchable.Enabled

        minWidth(20f)
        minHeight(20f)
    }

    override fun receiveDraggableItem(actor: Actor) {
        onDropped(actor)
    }
}