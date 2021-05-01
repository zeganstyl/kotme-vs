package com.kotme

import app.thelema.action.IAction
import app.thelema.ecs.ECS
import app.thelema.ui.*

class VisualScriptPanel(): Table() {
    constructor(block: VisualScriptPanel.() -> Unit): this() { block(this) }

    val templates = VBox()

    val diagram = ScriptDiagramPanel(this)

    val entityTreeWindow = EntityTreeWindow()

    init {
        background = SKIN.background
        align = Align.topLeft

        add(VBox {
            align = Align.top
            add(Label("Блоки", SKIN.label)).pad(5f)
            add(templates).growX()
        }).growY()

        add(VBox {
            add(Label("Скрипт", SKIN.label)).pad(5f)
            add(diagram).grow()
        }).grow()

        templates.defaults().pad(5f)
        templates.add(ComponentBlockTemplate { StepForwardActionBlock() })
        templates.add(ComponentBlockTemplate { RotateLeftActionBlock() })
        templates.add(ComponentBlockTemplate { RotateRightActionBlock() })
    }

    fun execute() {
        diagram.rootAction?.getComponentOrNullTyped<IAction>(ECS.Action)?.restart()
    }
}