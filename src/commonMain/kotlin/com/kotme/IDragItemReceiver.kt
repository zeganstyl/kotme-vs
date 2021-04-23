package com.kotme

import app.thelema.ui.Actor

interface IDragItemReceiver {
    fun receiveDraggableItem(actor: Actor)
}