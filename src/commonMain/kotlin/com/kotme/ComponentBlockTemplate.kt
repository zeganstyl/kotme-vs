package com.kotme

import app.thelema.ui.*

class ComponentBlockTemplate(val build: () -> IComponentBlock): Table(), IDragItemProvider {
    init {
        touchable = Touchable.Enabled
        add(build().panel).growX().pad(5f)

        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                val receiver = VisualScriptPanel.diagram.rootBlock
                if (receiver is ActionListBlock) {
                    receiver.currentSocket.receiveDraggableItem(build().panel)
                }
            }
        })
    }

    override fun provideDraggableItem(): Actor = build().panel.apply {
        color.a = 0.5f
    }
}