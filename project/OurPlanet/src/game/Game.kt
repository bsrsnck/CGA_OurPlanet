package game

import camera.Camera
import framework.GameDisplay
import org.lwjgl.glfw.GLFW
import java.nio.file.Files.move

class Game(width: Int,
           height: Int,
           fullscreen: Boolean = false,
           vsync: Boolean = false,
           title: String = "My Little World",
           GLVersionMajor: Int = 3,
           GLVersionMinor: Int = 3) : GameDisplay(width, height, fullscreen, vsync, GLVersionMajor, GLVersionMinor, title, 4, 120.0f) {

    private val scene: Scene
    init {
        setCursorVisible(false)
        scene = Scene(this)
    }


    override fun shutdown() = scene.cleanup()

    override fun update(dt: Float, t: Float) = scene.update(dt, t)

    override fun render(dt: Float, t: Float) = scene.render(dt, t)

    override fun onMouseMove(xpos: Double, ypos: Double) = scene.onMouseMove(xpos, ypos)

    override fun onKey(key: Int, scancode: Int, action: Int, mode: Int) = scene.onKey(key, scancode, action, mode)


}