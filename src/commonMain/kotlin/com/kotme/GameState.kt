package com.kotme

object GameState {
    const val NotStarted = "NotStarted"
    const val Playing = "Playing"
    const val RunningScript = "RunningScript"
    const val Win = "Win"

    var current: String = NotStarted
        set(value) {
            if (field != value) {
                field = value
            }
        }

    fun isStarted() = current == Playing || current == RunningScript

    fun canExecuteScript() = current == NotStarted || current == Playing

    fun isCompleted() = current == Win
}