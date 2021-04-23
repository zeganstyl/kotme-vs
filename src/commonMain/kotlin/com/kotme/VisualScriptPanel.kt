package com.kotme

import app.thelema.action.IAction
import app.thelema.ecs.ECS
import app.thelema.ui.MultiSplitPane
import app.thelema.ui.Table
import app.thelema.ui.TextButton
import app.thelema.ui.VBox

class VisualScriptPanel: Table() {
    val templates = VBox()

    val diagram = ScriptDiagramPanel(this)

    val split = MultiSplitPane(false) {
        setWidgets(templates, diagram)
        setSplit(0, 0.2f)
    }

    val restartButton = TextButton("Restart")

    val entityTreeWindow = EntityTreeWindow()

    init {
        add(split).grow()

        restartButton.addAction {
            diagram.rootAction?.getComponentOrNullTyped<IAction>(ECS.Action)?.restart()
        }

        templates.defaults().pad(5f)
        templates.add(restartButton)
        templates.add(ComponentBlockTemplate { ActionListBlock() })
        templates.add(ComponentBlockTemplate { RotateYActionBlock() })
        templates.add(ComponentBlockTemplate { MoveForwardActionBlock() })
    }
}