package com.kotme

import app.thelema.ui.Actor

interface IDragItemProvider {
    fun provideDraggableItem(): Actor?
}