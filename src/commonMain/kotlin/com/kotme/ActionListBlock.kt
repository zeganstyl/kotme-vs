package com.kotme

import app.thelema.action.ActionList
import app.thelema.action.IAction
import app.thelema.ui.*

class ActionListBlock: ActionBlockAdapter() {
    var currentSocket = ActionBlockSocket()

    var actionList: ActionList = ActionList()

    override val action: IAction
        get() = actionList

    val content = Table()

    val contextChooseButton = TextButton("...")

    val parallelButton = TextButton("|")

    val blocks = ArrayList<Actor>()

    init {
        background = SKIN.listBlockBackground
        content.align = Align.topLeft

        title.textProvider = { actionList.context?.name ?: "Action List" }

        currentSocket.onDropped = {
            blocks.add(it)
            val block = it as IComponentBlock
            block.visualScriptPanel = visualScriptPanel
            entity.addEntityWithCorrectedName(block.entity)
            if (actionList.isParallel) {
                content.add(it).pad(5f)
            } else {
                content.add(it).growX().padTop(5f).padBottom(5f).newRow()
            }
        }

        contextChooseButton.addAction {
            visualScriptPanel.entityTreeWindow.apply {
                onAccept = {
                    actionList.context = (tree.selectedNode as EntityTreeNode?)?.entity
                }
            }
            visualScriptPanel.entityTreeWindow.show(stage!!)
        }

        parallelButton.text = if (actionList.isParallel) "|||" else "|"
        parallelButton.addAction {
            actionList.isParallel = !actionList.isParallel
            parallelButton.text = if (actionList.isParallel) "|||" else "|"
            if (actionList.isParallel) {
                content.clearChildren()
                blocks.forEach {
                    content.add(it).pad(5f)
                }
            } else {
                content.clearChildren()
                blocks.forEach {
                    content.add(it).growX().padTop(5f).padBottom(5f).newRow()
                }
            }
        }

        add(HBox {
            add(parallelButton).width(20f).padRight(5f)
            add(title).growX().padRight(5f)
            add(contextChooseButton).width(20f)
        }).growX().pad(5f).newRow()

        add(content).growX().setFillY().padLeft(20f).newRow()
        add(currentSocket).grow().newRow()
    }
}