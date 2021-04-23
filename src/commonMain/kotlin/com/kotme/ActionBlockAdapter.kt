package com.kotme

import app.thelema.action.IAction
import app.thelema.ecs.IEntity
import app.thelema.ecs.IEntityComponent
import app.thelema.ui.*

abstract class ActionBlockAdapter: Table(), IComponentBlock {
    val title = Label("")

    abstract val action: IAction

    override val entity: IEntity
        get() = action.getOrCreateEntity()

    override val component: IEntityComponent
        get() = action

    override val selfActor: Actor
        get() = this

    var visualScriptPanelOrNull: VisualScriptPanel? = null
    override var visualScriptPanel: VisualScriptPanel
        get() = visualScriptPanelOrNull!!
        set(value) { visualScriptPanelOrNull = value }

    init {
        align = Align.topLeft
    }
}