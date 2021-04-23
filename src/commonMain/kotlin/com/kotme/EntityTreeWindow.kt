package com.kotme

import app.thelema.ecs.IEntity
import app.thelema.ui.*

class EntityTreeWindow: Window("Entity tree") {
    val tree = Tree(SKIN.tree)

    val treeScroll = ScrollPane(tree, style = SKIN.transparentScroll)

    var rootEntity: IEntity? = null
        set(value) {
            if (field != value) {
                field = value
                tree.clearChildren()
                rootNode = null
                if (value != null) {
                    val node = EntityTreeNode(value)
                    tree.add(node)
                    rootNode = node
                }
            }
        }

    var rootNode: EntityTreeNode? = null

    val acceptButton = TextButton("Accept", style = SKIN.textButton) {
        addAction {
            onAccept()
            onAccept = {}
            hide()
        }
    }

    val entityNameSearchField = TextField(style = SKIN.textField) {
        messageText = "Entity name..."
    }
    val componentNameSearchField = TextField(style = SKIN.textField) {
        messageText = "Component1, Component2, ..."
    }
    val searchButton = TextButton("OK", SKIN.textButton) {
        addAction {
            applyFilter()
        }
    }

    var onAccept: () -> Unit = {}

    init {
        content.add(HBox {
            add(VBox {
                add(entityNameSearchField).growX()
                add(componentNameSearchField).growX().padTop(5f)
            }).growX()
            add(searchButton).pad(5f).growY()
        }).growX().padBottom(10f).newRow()
        content.add(treeScroll).grow().newRow()
        content.add(acceptButton)

        isResizable = true
    }

    operator fun invoke(block: EntityTreeWindow.() -> Unit) = block(this)

    fun applyFilter() {
        val componentNameTemplates = componentNameSearchField.text.split(",").mapNotNull {
            val str = it.trim()
            if (str.isEmpty()) null else str
        }
        rootNode?.filter(entityNameSearchField.text, componentNameTemplates, false)
    }
}