package com.kotme

import app.thelema.ui.*

class ComponentBlockTemplate(val build: () -> IComponentBlock): Table(), IDragItemProvider {
    init {
        touchable = Touchable.Enabled
        add(build().selfActor.apply { touchable = Touchable.Disabled }).growX().pad(5f)
    }

    override fun provideDraggableItem(): Actor = build().selfActor.apply {
        color.a = 0.5f
    }
}