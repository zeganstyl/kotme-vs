package com.kotme

import app.thelema.input.KB
import app.thelema.input.MOUSE
import app.thelema.ui.*

class MainPanel {
    val stage = Stage()

    private var dragItemTouchableState: Touchable = Touchable.ChildrenOnly
    private var dragItem: Actor? = null
        set(value) {
            val oldValue = field
            if (oldValue != null) {
                stage.removeActor(oldValue)
            }
            field = value
            if (value != null) {
                dragItemTouchableState = value.touchable
                stage.addActor(value)
            }
        }

    init {
        KB.addListener(stage)
        MOUSE.addListener(stage)

        stage.addListener(object : InputListener {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val target = event.target
                if (target is IDragItemProvider) {
                    dragItem = target.provideDraggableItem()
                    val dragItem = dragItem
                    if (dragItem != null) {
                        dragItem.setPosition(x, y)
                        dragItem.touchable = Touchable.Disabled
                    }
                }
                return true
            }

            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                dragItem?.setPosition(x, y)
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                val receiver = stage.hit(x, y, true)
                if (receiver is IDragItemReceiver) {
                    val dragItem = dragItem
                    if (dragItem != null) {
                        dragItem.color.a = 1f
                        dragItem.touchable = dragItemTouchableState
                        receiver.receiveDraggableItem(dragItem)
                    }
                }
                dragItem = null
            }
        })
    }
}