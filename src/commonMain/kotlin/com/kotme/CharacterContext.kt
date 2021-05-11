package com.kotme

import app.thelema.anim.IAnimation
import app.thelema.ecs.ECS
import app.thelema.ecs.IEntity
import app.thelema.ecs.IEntityComponent

class CharacterContext: IEntityComponent {
    override val componentName: String
        get() = "CharacterContext"

    override var entityOrNull: IEntity? = null

    var moveAnim: IAnimation? = null
    var idleAnim: IAnimation? = null
    var turnRAnim: IAnimation? = null
    var turnLAnim: IAnimation? = null
    var angryAnim: IAnimation? = null
    var clappingAnim: IAnimation? = null
}