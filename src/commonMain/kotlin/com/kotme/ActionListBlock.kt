package com.kotme

import app.thelema.action.ActionList
import app.thelema.ui.*

class ActionListBlock: ActionBlock<ActionList>("Список", SKIN.grayActionButton, ActionList()) {
    var currentSocket = ActionBlockSocket()

    val listContent = VerticalGroup()

    val contextChooseButton = TextButton("...")

    val parallelButton = TextButton("|")

    val blocks = ArrayList<Actor>()

    init {
        //listContent.align = Align.topLeft

        currentSocket.onDropped = {
            blocks.add(it)
            val block = it as IComponentBlock
            entity.addEntityWithCorrectedName(block.entity)
            if (action.isParallel) {
                listContent.addActor(it)
            } else {
                listContent.addActor(it)
            }
        }

        contextChooseButton.addAction {
            VisualScriptPanel.entityTreeWindow.apply {
                onAccept = {
                    action.context = (tree.selectedNode as EntityTreeNode?)?.entity
                }
            }
            VisualScriptPanel.entityTreeWindow.show(stage!!)
        }

        listContent.setExpand()
        listContent.setFill()
        listContent.space(10f)

        parallelButton.text = if (action.isParallel) "|||" else "|"
        parallelButton.addAction {
            action.isParallel = !action.isParallel
            parallelButton.text = if (action.isParallel) "|||" else "|"
            if (action.isParallel) {
//                listContent.clearChildren()
//                blocks.forEach {
//                    listContent.addActor(it)
//                }
            } else {
//                listContent.clearChildren()
//                blocks.forEach {
//                    listContent.addActor(it)
//                }
            }
        }

        clearChildren()

        label.textProvider = { action.context?.name ?: "Список" }

        add(HBox {
            if (parallelActionsEnabled) add(parallelButton).width(20f).padRight(5f)
            add(label).growX().padRight(5f)
            if (chooseEnabled) add(contextChooseButton).width(20f)
            add(closeButton).padRight(5f)
        }).growX().pad(5f).newRow()

        add(listContent).growX().setFillY().padLeft(20f).newRow()
        add(currentSocket).grow().newRow()
    }

    companion object {
        var chooseEnabled = false
        var parallelActionsEnabled = false
    }
}