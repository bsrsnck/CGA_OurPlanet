package framework

import org.lwjgl.BufferUtils
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GLCapabilities
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryUtil

abstract class GameDisplay (var WIDTH : Int,
                            var HEIGHT : Int,
                            fullscreen: Boolean,
                            vsync: Boolean,
                            cvmaj: Int,
                            cvmin: Int,
                            var TITLE : String,
                            msaasamples: Int,
                            updatefrequency: Float) {

    class MousePosition(var xpos: Double, var ypos: Double)

    private var m_WINDOW : Long = 0
    var framebufferWidth : Int
        private set
    var framebufferHeight: Int
        private set

    private val m_fullscreen: Boolean
    private val m_vsync: Boolean
    private val m_title: String
    private val m_msaasamples: Int
    private val m_updatefrequency: Float
    private val m_cvmaj: Int
    private val m_cvmin: Int
    //GLFW callbacks
    private var m_caps: GLCapabilities? = null
    private val m_keyCallback: GLFWKeyCallback? = null
    private val m_cpCallback: GLFWCursorPosCallback? = null
    private val m_mbCallback: GLFWMouseButtonCallback? = null
    private val m_fbCallback: GLFWFramebufferSizeCallback? = null
    private val m_wsCallback: GLFWWindowSizeCallback? = null
    private val m_debugProc: Callback? = null
    private var m_currentTime: Long = 0

    init {
        framebufferWidth = WIDTH
        framebufferHeight = HEIGHT
        m_fullscreen = fullscreen
        m_vsync = vsync
        m_cvmaj = cvmaj
        m_cvmin = cvmin
        m_title = TITLE
        m_msaasamples = msaasamples
        m_updatefrequency = updatefrequency

        if (!glfwInit()) {
            throw RuntimeException("ERROR: GLFW wasn't initialized")
        }

        glfwSetErrorCallback { _, description ->
            val msg = MemoryUtil.memUTF8(description)
            println(msg)
        }

        glfwDefaultWindowHints()
        if (m_msaasamples > 0) glfwWindowHint(GLFW_SAMPLES, m_msaasamples)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT,GLFW_TRUE)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)

        m_WINDOW = glfwCreateWindow(WIDTH, HEIGHT, m_title, if (m_fullscreen) glfwGetPrimaryMonitor() else 0L, 0)
        check(m_WINDOW != 0L) { "Failed to create window." }

        initializeCallbacks()

        glfwMakeContextCurrent(m_WINDOW)
        glfwSwapInterval(if (m_vsync) 1 else 0)
        glfwShowWindow(m_WINDOW)

        m_caps = GL.createCapabilities(true)
        if (m_msaasamples > 0) glEnable(GL13.GL_MULTISAMPLE)

    }

    private fun initializeCallbacks() {
        glfwSetErrorCallback { _, description ->
            println("OpenGL Error: " + GLFWErrorCallback.getDescription(description))
        }

        glfwSetKeyCallback(m_WINDOW) { _, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) quit()
            onKey(key, scancode, action, mods)
        }

        glfwSetMouseButtonCallback(m_WINDOW) { _, button, action, mods ->
            onMouseButton(button, action, mods)
        }

        glfwSetCursorPosCallback(m_WINDOW) { _, xpos, ypos ->
            onMouseMove(xpos, ypos)
        }

        glfwSetScrollCallback(m_WINDOW) { _, xoffset, yoffset ->
            onMouseScroll(xoffset, yoffset)
        }

        glfwSetFramebufferSizeCallback(m_WINDOW) { _, width, height ->
            framebufferWidth = width
            framebufferHeight = height
            glViewport(0, 0, width, height)
            onFrameBufferSize(width, height)
        }

        glfwSetWindowSizeCallback(m_WINDOW) { _, width, height ->
            WIDTH = width
            HEIGHT = height
            onWindowSize(width, height)
        }
    }

    protected fun quit() {
        glfwSetWindowShouldClose(m_WINDOW, true)
    }

    val mousePos: MousePosition
        get() {
            val x = BufferUtils.createDoubleBuffer(1)
            val y = BufferUtils.createDoubleBuffer(1)
            glfwGetCursorPos(m_WINDOW, x, y)
            return MousePosition(x[0], y[0])
        }

    fun getKeyState(key: Int): Boolean {
        return glfwGetKey(m_WINDOW, key) == GLFW_PRESS
    }

    fun setCursorVisible(visible: Boolean) {
        glfwSetInputMode(m_WINDOW, GLFW_CURSOR, if (visible) GLFW_CURSOR_NORMAL else GLFW_CURSOR_DISABLED)
    }

    val currentTime: Float
        get() = (m_currentTime * 1e-9).toFloat()

    protected open fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}
    protected fun onMouseButton(button: Int, action: Int, mode: Int) {}
    protected open fun onMouseMove(xpos: Double, ypos: Double) {}
    protected fun onMouseScroll(xoffset: Double, yoffset: Double) {}
    protected fun onFrameBufferSize(width: Int, height: Int) {
        framebufferWidth = width
        framebufferHeight = height
    }
    protected fun onWindowSize(width: Int, height: Int) {
        WIDTH = width
        HEIGHT = height
    }

    protected open fun start() {}
    protected open fun update(dt: Float, t: Float) {}
    protected open fun render(dt: Float, t: Float) {}
    protected open fun shutdown() {}


    fun run(){
        start()

        val timedelta = (1.0 / m_updatefrequency * 1000000000.0).toLong()
        var currenttime: Long = 0
        var frametime: Long = 0
        var newtime: Long = 0
        var accum: Long = 0
        m_currentTime = 0
        currenttime = System.nanoTime()
        while (!glfwWindowShouldClose(m_WINDOW)) {
            newtime = System.nanoTime()
            frametime = newtime - currenttime
            m_currentTime += frametime
            currenttime = newtime
            accum += frametime
            glfwPollEvents()
            while (accum >= timedelta) {
                update((timedelta.toDouble() * 1e-9).toFloat(), (m_currentTime * 1e-9).toFloat())
                accum -= timedelta
            }
            render((frametime.toDouble() * 1e-9).toFloat(), (m_currentTime * 1e-9).toFloat())
            glfwSwapBuffers(m_WINDOW)
        }

        shutdown()

        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(m_WINDOW)
        glfwDestroyWindow(m_WINDOW)
        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

}