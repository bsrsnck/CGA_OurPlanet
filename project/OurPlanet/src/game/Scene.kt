package game

import camera.Camera
import framework.GLError
import framework.GameDisplay
import framework.ModelLoader.loadModel
import framework.OBJLoader
import geometry.Material
import geometry.Mesh
import geometry.Renderable
import geometry.VertexAttribute
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import shader.ShaderProgram
import texture.Texture2D

class Scene (private val WINDOW: GameDisplay) {

    val staticShader = ShaderProgram()

    //Test2
    val meshHaus : Mesh
    val meshBrucke : Mesh
    val meshBoden : Mesh



    val hausRender : Renderable
    val bruckeRender: Renderable
    val bodenRender : Renderable
    val camera = Camera()

    //Boden
    var texEmitBoden : Texture2D
    var texDiffBoden : Texture2D
    var texSpecBoden : Texture2D

    //Haus
    var texEmitKugel : Texture2D
    var texDiffKugel : Texture2D
    var texSpecKugel : Texture2D

    //blume
    var texEmitBrucke : Texture2D
    var texDiffBrucke: Texture2D
    var texSpecBrucke: Texture2D

    //
    var lastX: Double = WINDOW.mousePos.xpos

    private val baum = loadModel("models/Baum/Baum/Baum.obj", org.joml.Math.toRadians(-90f), org.joml.Math.toRadians(90f), 0f)
    private val blume = loadModel("models/Blume/Blume.obj", org.joml.Math.toRadians(-90f), org.joml.Math.toRadians(90f), 0f)


    init {

        staticShader.shader("shaders/third_vert.glsl", "shaders/third_frag.glsl")
        staticShader.use()

        GL11.glClearColor(0.0f, 1.0f, 1.0f, 1.0f); GLError.checkThrow()
        GL11.glEnable(GL11.GL_CULL_FACE); GLError.checkThrow()
        GL11.glFrontFace(GL11.GL_CCW); GLError.checkThrow()
        GL11.glCullFace(GL11.GL_BACK); GLError.checkThrow()
        GL11.glEnable(GL11.GL_DEPTH_TEST); GLError.checkThrow()
        GL11.glDepthFunc(GL11.GL_LESS); GLError.checkThrow()

        //load OBJ Object
        val attrPos = VertexAttribute(3, GL11.GL_FLOAT, 32, 0)
        val attrTC = VertexAttribute(2, GL11.GL_FLOAT, 32, 12)
        val attrNorm = VertexAttribute(3, GL11.GL_FLOAT, 32, 20)
        val vertexAttributes = arrayOf(attrPos, attrTC, attrNorm)

        /**Haus**/

        val res : OBJLoader.OBJResult = OBJLoader.loadOBJ("models/figur.obj")
        val objMesh: OBJLoader.OBJMesh = res.objects[0].meshes[0]
        //meshKugel = Mesh(objMesh.vertexData, objMesh.indexData, vertexAttributes)


        texEmitKugel = Texture2D("models/textures/smiley.png", true)
        texEmitKugel.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR_MIPMAP_LINEAR, GL12.GL_LINEAR_MIPMAP_LINEAR)
        texDiffKugel = Texture2D("models/textures/CGAskizze.png", false)
        texDiffKugel.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR, GL12.GL_LINEAR)
        texSpecKugel = Texture2D("models/textures/CGAskizze.png", false)
        texSpecKugel.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR, GL12.GL_LINEAR)

        val matKugel = Material(texDiffKugel, texEmitKugel, texSpecKugel, 60.0f, Vector2f(64F, 64F))
        meshHaus = Mesh(objMesh.vertexData, objMesh.indexData, vertexAttributes, matKugel)

        /**Brucke**/
        val resBrucke : OBJLoader.OBJResult = OBJLoader.loadOBJ("models/brucke.obj")
        val objBrucke: OBJLoader.OBJMesh = resBrucke.objects[0].meshes[0]

        texEmitBrucke = Texture2D("models/textures/smiley.png", true)
        texEmitBrucke.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR_MIPMAP_LINEAR, GL12.GL_LINEAR_MIPMAP_LINEAR)
        texDiffBrucke = Texture2D("models/textures/CGAskizze.png", false)
        texDiffBrucke.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR, GL12.GL_LINEAR)
        texSpecBrucke= Texture2D("models/textures/CGAskizze.png", false)
        texSpecBrucke.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR, GL12.GL_LINEAR)

        val matBrucke = Material(texDiffBrucke, texEmitBrucke, texSpecBrucke, 60.0f, Vector2f(64F, 64F))
        meshBrucke = Mesh(objBrucke.vertexData, objBrucke.indexData, vertexAttributes, matBrucke)

        /**Boden**/
        val resBoden : OBJLoader.OBJResult = OBJLoader.loadOBJ("models/ground.obj")
        val objMeshBoden: OBJLoader.OBJMesh = resBoden.objects[0].meshes[0]

        texEmitBoden = Texture2D("models/textures/boden_textur.png", true)
        texEmitBoden.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR_MIPMAP_LINEAR, GL12.GL_LINEAR_MIPMAP_LINEAR)
        texDiffBoden = Texture2D("models/textures/CGAskizze.png", false)
        texDiffBoden.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR, GL12.GL_LINEAR)
        texSpecBoden = Texture2D("models/textures/CGAskizze.png", false)
        texSpecBoden.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL12.GL_LINEAR, GL12.GL_LINEAR)

        val matBoden = Material(texDiffBoden, texEmitBoden, texSpecBoden, 60.0f, Vector2f(64F, 64F))
        meshBoden = Mesh(objMeshBoden.vertexData, objMeshBoden.indexData, vertexAttributes, matBoden)

        /**Render**/
        hausRender = Renderable(mutableListOf(meshHaus))
        bruckeRender = Renderable(mutableListOf(meshBrucke))
        bodenRender = Renderable(mutableListOf(meshBoden))

        //Camera
        camera.rotate(Math.toRadians(-20.0).toFloat(), 0.0f, 0.0f)
        camera.translate(Vector3f(0.0f, 0.0f, 4.0f))
        camera.parent = hausRender


    }

    fun render(dt: Float, t: Float) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

        baum?.render(staticShader)
        blume?.render(staticShader)

        hausRender.render(staticShader)
        bruckeRender.render(staticShader)
        bodenRender.render(staticShader)

        camera.bind(staticShader)
    }

    fun update(dt: Float, t: Float) {
       if (WINDOW.getKeyState(GLFW.GLFW_KEY_W)) hausRender.translate(Vector3f(0.0f, 0.0f, -100.0f*dt))
       if (WINDOW.getKeyState(GLFW.GLFW_KEY_S)) hausRender.translate(Vector3f(0.0f, 0.0f, 100.0f*dt))
       if (WINDOW.getKeyState(GLFW.GLFW_KEY_A)) hausRender.translate(Vector3f(-50.0f*dt, 0.0f, 0.0f))
        if (WINDOW.getKeyState(GLFW.GLFW_KEY_D)) hausRender.translate(Vector3f(50.0f*dt, 0.0f, 0.0f))

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
       camera.rotateAroundPoint(0f, (lastX - xpos).toFloat() * 0.002f, 0f, Vector3f(0f, 0f, 0f))
       lastX = xpos



        
    }

    fun cleanup() {}

}