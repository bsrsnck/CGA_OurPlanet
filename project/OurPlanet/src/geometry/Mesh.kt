package geometry

import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import shader.ShaderProgram

class Mesh (vertexData: FloatArray, indexData: IntArray, attributes: Array<VertexAttribute>, private val material: Material? = null) {

    var vao = 0
    var vbo = 0
    var ibo = 0
    var indexCount = 0

    init {
        vao = GL30.glGenVertexArrays()   //create VAO
        GL30.glBindVertexArray(vao)

        vbo = GL30.glGenBuffers()       //create VBO
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)

        ibo = GL30.glGenBuffers()       //setup IBO
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo)

        indexCount = indexData.size

        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexData, GL30.GL_STATIC_DRAW)    //upload Data to GPU
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indexData, GL30.GL_STATIC_DRAW)

        for (i in attributes.indices) {
            GL20.glVertexAttribPointer(
                i,
                attributes[i].n,
                attributes[i].type,
                false,
                attributes[i].stride,
                attributes[i].offset.toLong()
            )
            GL30.glEnableVertexAttribArray(i)
        }

        //Reset
        GL30.glBindVertexArray(0)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0)

    }

    fun render(){
        GL30.glBindVertexArray(vao)
        GL30.glEnableVertexAttribArray(0)
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL30.glDrawElements(GL30.GL_TRIANGLES, indexCount, GL30.GL_UNSIGNED_INT, 0)

        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0)
        GL30.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
    }

    fun render(shaderProgram: ShaderProgram) {
        material?.bind(shaderProgram)
        render()
    }

}