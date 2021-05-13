package com.kotme

import app.thelema.img.Texture2D
import app.thelema.ui.*

object Talk {
    var steps: Array<() -> Unit> = emptyArray()
    var currentStep = 0

    val avatar1 = Texture2D(0)
    val avatar2 = Texture2D(0)
    val bgImageTex = Texture2D(0)

    val dialogLabel = Label().apply {
        style = SKIN.dialogLabel
        setWrap(true)
        alignH = 0
        alignV = 0
        invalidate()
    }

    val dialogAvatar = UIImage().apply {
        scaling = Scaling.fit
        touchable = Touchable.Disabled
    }

    val backgroundImage = UIImage {
        scaling = Scaling.fillX
        alignV = -1
    }

    val blackBackground = UIImage {
        drawable = SKIN.background150H
        scaling = Scaling.stretchX
        alignV = -1
    }

    val skipButton = TextButton("Пропустить") {
        label.alignV = -1
        addAction {
            if (currentStep < steps.size) {
                steps.last()()
            }
            hide()
        }
    }

    val dialogSplit = MultiSplitPane(true).apply {
        touchable = Touchable.ChildrenOnly
        setWidgets(
            Actor().apply { touchable = Touchable.Disabled },
            Stack {
                add(backgroundImage)
                add(blackBackground)
                add(HBox {
                    touchable = Touchable.ChildrenOnly
                    add(skipButton).width(160f).height(160f).pad(20f)
                    add(Stack {
                        add(dialogLabel)
                        add(Table {
                            align = Align.bottomRight
                            add(TextButton(">> Далее >>") {
                                addAction { steps[currentStep]() }
                            }).pad(10f)
                        })
                    }).grow().pad(10f).height(130f).align(Align.bottom)
                    add(dialogAvatar).width(200f).height(200f)
                })
            }
        )
        setSplit(0, 1f)
    }

    val panel = VBox {
        touchable = Touchable.ChildrenOnly
        add(Actor().apply { touchable = Touchable.Disabled }).grow()
        add(dialogSplit).growX().height(200f)
    }

    fun nextStep() {
        if (currentStep < steps.size) currentStep++
    }

    fun show() {
        dialogSplit.setSplit(0, 0f)
    }

    fun hide() {
        dialogSplit.setSplit(0, 1f)
    }
}