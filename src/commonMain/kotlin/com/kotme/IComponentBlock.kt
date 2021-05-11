package com.kotme

import app.thelema.ecs.IEntity
import app.thelema.ecs.IEntityComponent
import app.thelema.ui.Actor

interface IComponentBlock {
    val entity: IEntity
        get() = component.getOrCreateEntity()

    val component: IEntityComponent

    val panel: Actor
        get() = this as Actor
}