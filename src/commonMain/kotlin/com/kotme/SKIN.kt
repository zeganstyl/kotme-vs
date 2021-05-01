package com.kotme

import app.thelema.data.DATA
import app.thelema.fs.FS
import app.thelema.font.BitmapFont
import app.thelema.g2d.NinePatch
import app.thelema.g2d.Sprite
import app.thelema.g2d.TextureRegion
import app.thelema.gl.*
import app.thelema.img.Texture2D
import app.thelema.math.Vec4
import app.thelema.res.RES
import app.thelema.ui.*
import app.thelema.utils.Color

object SKIN {
    val kotmeTitleFont = RES.loadTyped<BitmapFont>("pink/5.fnt")

    val plainFont = RES.loadTyped<BitmapFont>("pink/5.fnt")

    val whiteTexture = Texture2D(0)
    val windowTexture = Texture2D(0)
    val listBlockTexture = Texture2D(0)
    val textFieldTexture = Texture2D(0)
    val focusedTextFieldTexture = Texture2D(0)

    val overColor = Vec4(0f, 0.5f, 0.25f, 1f)
    val selectedColor = Vec4(0f, 1f, 0.5f, 1f)
    val checkedColor = Vec4(1f, 1f, 0f, 1f)
    val downColor = Vec4(1f, 0f, 0.5f, 1f)
    val splitColor = Vec4(0.2f, 0.2f, 0.2f, 0.8f)
    val lineColor = Vec4(0.3f, 0.3f, 0.3f, 1f)

    val bg = Texture2D(0)

    val label = LabelStyle(plainFont)

    val kotmeTitleLabel = LabelStyle(kotmeTitleFont)

    val plainButton = TextButtonStyle().apply {
        font = plainFont
        downFontColor = Color.CYAN
    }

    val cell = Texture2D(0)

    init {
        setGreenTheme()
    }

    private fun setupWindowImage() {
        setupTexture5x5(
            windowTexture,
            corner = 0xAA000000.toInt(),
            gradient = 0x00FF00FF.toInt(),
            vEdge = 0xEE000000.toInt(),
            middle = 0xFFFFFFFF.toInt(),
            field = 0xEE0000FF.toInt()
        )
    }

    private fun setupListBlockImage() {
        setupTexture5x52(
            listBlockTexture,
            corner = 0xAA000000.toInt(),
            gradient = 0x55FF8800.toInt(),
            vEdge = 0x00000000,
            middle = Color.rgba8888(Color.WHITE),
            field = 0x00000000.toInt()
        )
    }

    private fun setupTextFieldTexture() {
        val border = Color.argb8888(lineColor)
        val field = 0x00000000

        val bytes = DATA.bytes(24).apply {
            putInts(
                field, field, field,
                border, border, border
            )
            rewind()
        }

        textFieldTexture.load(3, 2, bytes, 0, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE)

        bytes.destroy()
    }

    private fun setupTextFieldTextureLeft() {
    }

    private fun setupFocusedTextFieldTexture() {
        val gradient = Color.rgba8888(selectedColor.r, selectedColor.g, selectedColor.b, 0.5f)
        val field = 0x00000000
        val middle = 0xFFFFFFFF.toInt()

        val bytes = DATA.bytes(24).apply {
            putInts(
                field, field, field,
                gradient, middle, gradient
            )
            rewind()
        }

        focusedTextFieldTexture.load(3, 2, bytes, 0, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE)

        bytes.destroy()
    }

    private fun setupFocusedTextFieldTextureLeft() {
    }

    private fun setupTexture5x5(
        texture2D: Texture2D,
        corner: Int,
        gradient: Int,
        vEdge: Int,
        middle: Int,
        field: Int
    ) {
        val bytes = DATA.bytes(100).apply {
            putInts(
                corner, gradient, middle, gradient, corner,
                vEdge, field, field, field, vEdge,
                vEdge, field, field, field, vEdge,
                vEdge, field, field, field, vEdge,
                corner, gradient, middle, gradient, corner
            )
            rewind()
        }

        texture2D.load(5, 5, bytes, 0, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE)

        bytes.destroy()
    }

    private fun setupTexture5x52(
        texture2D: Texture2D,
        corner: Int,
        gradient: Int,
        vEdge: Int,
        middle: Int,
        field: Int
    ) {
        val bytes = DATA.bytes(64).apply {
            putInts(
                middle, field, field, field,
                gradient, field, field, field,
                gradient, field, field, field,
                middle, vEdge, vEdge, vEdge
            )
            rewind()
        }

        texture2D.load(4, 4, bytes, 0, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE)

        bytes.destroy()
    }

    fun setGreenTheme() {
        overColor.set(0f, 0.5f, 0.25f, 1f)
        selectedColor.set(0f, 1f, 0.5f, 1f)
        checkedColor.set(1f, 1f, 0f, 1f)
        downColor.set(1f, 0f, 0.5f, 1f)
        splitColor.set(0.2f, 0.2f, 0.2f, 0.8f)
        setupWindowImage()
        setupListBlockImage()
        setupTextFieldTexture()
        setupFocusedTextFieldTexture()
        setupTextFieldTextureLeft()
        setupFocusedTextFieldTextureLeft()
    }

    fun setRedTheme() {
        overColor.set(0.5f, 0f, 0f, 1f)
        selectedColor.set(1f, 0f, 0f, 1f)
        checkedColor.set(1f, 1f, 0f, 1f)
        downColor.set(0f, 1f, 0f, 1f)
        splitColor.set(0.2f, 0.2f, 0.2f, 0.8f)
        setupWindowImage()
        setupListBlockImage()
        setupTextFieldTexture()
        setupFocusedTextFieldTexture()
        setupTextFieldTextureLeft()
        setupFocusedTextFieldTextureLeft()
    }

    fun setBlueTheme() {
        overColor.set(0f, 0.2f, 0.8f, 1f)
        selectedColor.set(0f, 0.5f, 1f, 1f)
        checkedColor.set(1f, 1f, 0f, 1f)
        downColor.set(0f, 1f, 0f, 1f)
        splitColor.set(0.2f, 0.2f, 0.2f, 0.8f)
        setupWindowImage()
        setupListBlockImage()
        setupTextFieldTexture()
        setupFocusedTextFieldTexture()
        setupTextFieldTextureLeft()
        setupFocusedTextFieldTextureLeft()
    }

    val progressBar = ProgressBarStyle().apply {
        knobBefore = SpriteDrawable(Sprite(whiteTexture).apply { color = selectedColor })
    }

    val textButton = TextButtonStyle().apply {
        fontColor = Color.WHITE
        overFontColor = selectedColor
        downFontColor = downColor
        font = BitmapFont.default()
    }

    val greyUnderline = NinePatchDrawable(NinePatch(textFieldTexture, 0, 0, 0, 1))
    val brightUnderline = NinePatchDrawable(NinePatch(focusedTextFieldTexture, 0, 0, 0, 1))

    val textField = TextFieldStyle().apply {
        fontColor = Color.WHITE
        background = greyUnderline
        focusedBackground = brightUnderline
        font = BitmapFont.default()
    }

    val errorsButton = TextButtonStyle().apply {
        fontColor = Vec4(1f, 0f, 0f, 1f)
        overFontColor = overColor
        downFontColor = downColor
        font = BitmapFont.default()
    }

    val checkTextButton = TextButtonStyle().apply {
        fontColor = Color.WHITE
        overFontColor = overColor
        downFontColor = downColor
        checkedFontColor = checkedColor
        font = BitmapFont.default()
    }

    val windowBackground = NinePatchDrawable(NinePatch(windowTexture, 0, 0, 1, 1))

    val listBlockBackground = NinePatchDrawable(NinePatch(listBlockTexture, 2, 0, 0, 0))

    val background = SpriteDrawable(Sprite(whiteTexture)).apply {
        sprite.color.set(0f, 0f, 0f, 0.8f)
        minWidth = 0f
        minHeight = 0f
    }

    val titleBackground = SpriteDrawable(Sprite(whiteTexture)).apply {
        sprite.color.set(overColor).scl(0.8f)
        minWidth = 0f
        minHeight = 0f
    }

    val listOver = SpriteDrawable()
    val listSelected = SpriteDrawable()

    val tree = TreeStyle().apply {
        over = listOver
        selection = listSelected

        RES.loadTyped<BitmapFont>("arial-15.fnt") {
            onLoaded {
                GL.call {
                    getGlyph('+')?.apply {
                        plus = TextureRegionDrawable(TextureRegion(regions[page].texture, u, v, u2, v2))
                    }
                    getGlyph('-')?.apply {
                        minus = TextureRegionDrawable(TextureRegion(regions[page].texture, u, v, u2, v2))
                    }
                }
            }
        }
    }

    val checkBox = CheckBoxStyle().apply {
        RES.loadTyped<BitmapFont>("arial-15.fnt") {
            onLoaded {
                GL.call {
                    getGlyph('v')?.apply {
                        checkboxOn = TextureRegionDrawable(TextureRegion(regions[page].texture, u, v, u2, v2))
                    }
                }
            }
        }
    }

    val hLine = SpriteDrawable(Sprite(whiteTexture).apply { color = lineColor }).apply { minHeight = 4f }
    val vLine = SpriteDrawable(Sprite(whiteTexture).apply { color = lineColor }).apply { minWidth = 4f }

    val vSplitPane = SplitPane.Style().apply {
        handle = vLine
    }
    val hSplitPane = SplitPane.Style().apply {
        handle = hLine
    }

    val scroll = ScrollPaneStyle().apply {
        background = SKIN.background
    }

    val transparentScroll = ScrollPaneStyle().apply {
        background = null
    }

    val window = WindowStyle().apply {
        this.background = windowBackground
    }

    val bgImage = SpriteDrawable(Sprite(bg))

    fun init() {
        val bg2 = TextureRegion(whiteTexture)
        listOver.sprite.setRegion(bg2)
        listOver.sprite.color = Vec4(overColor).apply { w = 0.25f }
        listSelected.sprite.setRegion(bg2)
        listSelected.sprite.color = Vec4(selectedColor).apply { w = 0.5f }

        whiteTexture.initOnePixelTexture(1f, 1f, 1f, 1f)

        bg.load("bg.jpg")
        cell.load("cell.png", GL_LINEAR, GL_LINEAR_MIPMAP_LINEAR)
    }
}
