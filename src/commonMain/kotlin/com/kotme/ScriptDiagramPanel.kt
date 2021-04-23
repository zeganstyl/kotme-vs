package com.kotme

import app.thelema.action.ActionList
import app.thelema.ecs.ECS
import app.thelema.ecs.IEntity
import app.thelema.ui.ScrollPane
import app.thelema.ui.Table
import app.thelema.ui.Touchable

class ScriptDiagramPanel(val visualScriptPanel: VisualScriptPanel): Table() {
    val root = Table()
    val scroll = ScrollPane(root)

    var rootAction: IEntity? = null

    var rootBlock: IComponentBlock? = null

    init {
        scroll.style.background = null

        touchable = Touchable.Enabled
        scroll.touchable = Touchable.ChildrenOnly
        add(scroll).grow()
    }

    private fun replaceBlock(list: ActionList?) {
        if (list != null) {
            val block = ActionListBlock()
            block.visualScriptPanelOrNull = visualScriptPanel
            block.actionList = list
            rootBlock = block
            map[list.entity] = block
            root.add(block)
        } else {
            root.clearChildren()
        }
    }

    override fun act(delta: Float) {
        super.act(delta)

        val list = rootAction?.getComponentOrNullTyped<ActionList>(ECS.ActionList)
        if (rootBlock?.component != list) replaceBlock(list)
    }

    companion object {
        val map = HashMap<IEntity, IComponentBlock>()
    }
}