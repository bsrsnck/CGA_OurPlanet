package geometry

import shader.ShaderProgram

interface IRenderable {
    fun render(shaderProgram: ShaderProgram)
}