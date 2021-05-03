package com.kotme

import app.thelema.ui.*

class ComponentBlockTemplate(val vsPanel: VisualScriptPanel, val build: () -> IComponentBlock): Table(), IDragItemProvider {
    init {
        touchable = Touchable.Enabled
        add(build().selfActor.apply { touchable = Touchable.Disabled }).growX().pad(5f)

        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (tapCount == 2) {
                    tapCount = 0

                    val receiver = vsPanel.diagram.rootBlock
                    if (receiver is ActionListBlock) {
                        receiver.currentSocket.receiveDraggableItem(build() as Actor)
                    }
                }
            }
        })
    }

    override fun provideDraggableItem(): Actor = build().selfActor.apply {
        color.a = 0.5f
    }
}