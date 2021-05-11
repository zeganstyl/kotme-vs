package com.kotme

import app.thelema.action.ActionList
import app.thelema.ecs.ECS
import app.thelema.ecs.IEntity
import app.thelema.ecs.getComponentOrNull
import app.thelema.ui.Align
import app.thelema.ui.ScrollPane
import app.thelema.ui.Table
import app.thelema.ui.Touchable

class ScriptDiagramPanel: Table() {
    val root = Table {
        align = Align.top
    }

    val scroll = ScrollPane(root).apply {
        style.background = null
        touchable = Touchable.ChildrenOnly
    }

    var rootAction: IEntity? = null

    var rootBlock: IComponentBlock? = null

    val map = HashMap<IEntity, IComponentBlock>()

    init {
        touchable = Touchable.Enabled
        add(scroll).grow()
    }

    private fun replaceBlock(list: ActionList?) {
        if (list != null) {
            val block = ActionListBlock()
            block.action = list
            rootBlock = block
            map[list.entity] = block
            root.add(block.panel).growX()
        } else {
            root.clearChildren()
        }
    }

    override fun act(delta: Float) {
        super.act(delta)

        val list = rootAction?.getComponentOrNull<ActionList>()
        if (rootBlock?.component != list) replaceBlock(list)
    }
}