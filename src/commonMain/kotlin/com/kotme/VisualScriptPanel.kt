package com.kotme

import app.thelema.action.IAction
import app.thelema.ecs.ECS
import app.thelema.ecs.IEntity
import app.thelema.ecs.getComponentOrNull
import app.thelema.ui.*
import kotlin.math.PI

object VisualScriptPanel {
    val templates = VBox {
        defaults().pad(5f)
        add(ComponentBlockTemplate { ActionBlock("Шаг вперед", SKIN.greenActionButton, StepForwardAction()) })
        add(ComponentBlockTemplate { ActionBlock("Поворот влево", SKIN.yellowActionButton, TurnAction(PI.toFloat() * 0.5f)) })
        add(ComponentBlockTemplate { ActionBlock("Поворот вправо", SKIN.darkYellowActionButton, TurnAction(-PI.toFloat() * 0.5f)) })
    }

    val diagram = ScriptDiagramPanel()

    val entityTreeWindow = EntityTreeWindow()

    val panel = MultiSplitPane(false) {
        touchable = Touchable.ChildrenOnly

        setWidgets(
            Table {
                background = SKIN.background
                align = Align.topLeft

                add(VBox {
                    align = Align.top
                    add(Label("Блоки")).pad(5f)
                    add(templates).growX()
                }).growY().padRight(5f)

                add(VBox {
                    background = SKIN.listBlockBackground
                    add(Label("Скрипт")).pad(5f)
                    add(diagram).grow().padRight(10f).padLeft(10f)
                }).grow()
            },

            HBox {
                add(TextButton("<<") {
                    addAction {
                        setSplit(0, if (splits[0] > 0f) 0f else 0.4f)
                        invalidateHierarchy()
                        this.text = if (splits[0] > 0f) "<<" else ">>"
                    }
                }).growY().width(50f)

                add(Actor()).grow()
            }
        )

        setSplit(0, 0.4f)
        invalidateHierarchy()
    }

    fun setScene(entity: IEntity) {
        entityTreeWindow.rootEntity = entity
        diagram.rootAction = entity
    }

    fun execute() {
        if (GameState.canExecuteScript()) {
            GameState.current = GameState.RunningScript
            diagram.rootAction?.getComponentOrNull<IAction>()?.restart()
        }
    }
}