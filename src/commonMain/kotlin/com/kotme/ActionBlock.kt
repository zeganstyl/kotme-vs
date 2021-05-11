package com.kotme

import app.thelema.action.IAction
import app.thelema.ecs.IEntity
import app.thelema.ecs.IEntityComponent
import app.thelema.ui.*

open class ActionBlock<A: IAction>(
    title: String,
    titleSkin: TextButtonStyle,
    var action: A
): TextButton(title, titleSkin), IComponentBlock {
    override val entity: IEntity
        get() = action.getOrCreateEntity()

    override val component: IEntityComponent
        get() = action

    val closeButton = TextButton("X", SKIN.closeButton) {
        addAction {
            component.entityOrNull?.parentEntity?.also {
                if (it.children.contains(component.entity)) it.removeEntity(component.entity)
                this@ActionBlock.remove()
            }
        }
    }

    init {
        clearChildren()

        add(HBox {
            add(label).pad(5f).growX()
            add(closeButton).pad(5f)
        }).growX()
    }
}