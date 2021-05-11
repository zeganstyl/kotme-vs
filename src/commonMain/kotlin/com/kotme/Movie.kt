package com.kotme

import app.thelema.audio.AL
import app.thelema.fs.FS
import app.thelema.gltf.GLTF
import app.thelema.img.ITexture2D
import app.thelema.img.Texture2D
import app.thelema.res.RES
import app.thelema.ui.DSKIN
import app.thelema.ui.Scaling
import app.thelema.ui.TextureRegionDrawable
import app.thelema.utils.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

object Movie: CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()

    val music = AL.newMusic(FS.internal("synth-58.ogg")).apply {
        isLooping = true
        volume = 0.3f
    }

    val earth = Texture2D(0)
    val kx2000 = Texture2D(0)
    val ship = Texture2D(0)
    val kateInShip = Texture2D(0)
    val kateInShipAlarm = Texture2D(0)

    const val maxLoad = 9
    var loadingProgress = 0
        set(value) {
            field = value
            Common.loadProgress.value = value.toFloat() / maxLoad.toFloat()
            if (value == maxLoad) {
                music.play()
                Talk.dialogLabel.style = DSKIN.label
                Talk.backgroundImage.isVisible = false
                Talk.blackBackground.isVisible = true
                Talk.dialogLabel.text = "Мы освоили Землю"
                Talk.currentStep = 0

                Common.hideLoading()
                Talk.show()
            }
        }

    fun init() {
        Common.hud.isVisible = false
        Common.sky.isEnabled = false

        Common.showLoading()

        earth.load("images/earth.png") { loadingProgress++ }
        kx2000.load("images/1.png") { loadingProgress++ }
        ship.load("images/ship.png") { loadingProgress++ }
        kateInShip.load("images/2.png") { loadingProgress++ }
        kateInShipAlarm.load("images/3.png") { loadingProgress++ }

        async {
            RES.loadTyped<GLTF>("ship.glb") {
                conf.separateThread = true
                onLoaded { loadingProgress++ }
            }

            RES.loadTyped<GLTF>("mars-plane.glb") {
                conf.separateThread = true
                onLoaded { loadingProgress++ }
            }

            RES.loadTyped<GLTF>("rocks.glb") {
                conf.separateThread = true
                onLoaded { loadingProgress++ }
            }

            RES.loadTyped<GLTF>("kate.glb") {
                conf.separateThread = true
                onLoaded { loadingProgress++ }
            }
        }

        Talk.steps = arrayOf({
            Common.backgroundImage.drawable = TextureRegionDrawable(earth)
            Talk.nextStep()
        }, {
            Talk.dialogLabel.text = "Создали роботов"
            Common.backgroundImage.drawable = TextureRegionDrawable(kx2000)
            Talk.nextStep()
        }, {
            Talk.dialogLabel.text = "Я робот модели KX-2000"
            Talk.nextStep()
        }, {
            Common.backgroundImage.drawable = null
            Talk.dialogLabel.text = "Мое имя Кейт"
            Talk.nextStep()
        }, {
            Talk.dialogLabel.text = "Моя миссия исследовать Марс"
            Common.backgroundImage.drawable = TextureRegionDrawable(ship)
            Talk.nextStep()
        }, {
            Talk.dialogLabel.text = "Но посадка оказалось не очень удачной"
            Common.backgroundImage.drawable = TextureRegionDrawable(kateInShip)
            Talk.nextStep()
        }, {
            Talk.dialogLabel.text = "!!!"
            Common.backgroundImage.drawable = TextureRegionDrawable(kateInShipAlarm)
            Talk.nextStep()
        }, {
            Talk.dialogLabel.text = "Кажется, мне требуется твоя помощь"
            Common.backgroundImage.drawable = null
            Talk.nextStep()
        }, {
            earth.destroy()
            kx2000.destroy()
            ship.destroy()
            kateInShip.destroy()
            kateInShipAlarm.destroy()

            music.stop()
            music.destroy()

            Talk.currentStep = 0
            Talk.steps = arrayOf({ Talk.hide() })
            Talk.dialogLabel.text = """
Нас немного отбросило от места падения.
Моя встроенная система навигации кажется накрылась.
Ты поможешь мне добраться до коробля?
Соедени блоки в нужном порядке, чтобы можно было дойти до места крушения.
""".trimIndent()
            Common.sky.isEnabled = true
            Common.hud.isVisible = true
            Talk.skipButton.isDisabled = true
            Talk.skipButton.isVisible = false
            Talk.backgroundImage.isVisible = true
            Talk.blackBackground.isVisible = false
            Common.backgroundImage.drawable = null
            Talk.dialogLabel.style = SKIN.dialogLabel
            Texture2D().load("avatar/pose13.png") { Talk.dialogAvatar.drawable = TextureRegionDrawable(this) }
            Scene1.init()
        }
        )
    }
}