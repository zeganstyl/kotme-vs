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
            add(Label("Блоки")).pad(5f)
            add(templates).growX()
        }).growY()

        add(VBox {
            background = SKIN.listBlockBackground
            add(Label("Скрипт")).pad(5f)
            add(diagram).grow().padRight(10f).padLeft(10f)
        }).grow()

        templates.defaults().pad(5f)
        templates.add(ComponentBlockTemplate(this) { StepForwardActionBlock() })
        templates.add(ComponentBlockTemplate(this) { RotateLeftActionBlock() })
        templates.add(ComponentBlockTemplate(this) { RotateRightActionBlock() })
    }

    fun execute() {
        diagram.rootAction?.getComponentOrNullTyped<IAction>(ECS.Action)?.restart()
    }
}