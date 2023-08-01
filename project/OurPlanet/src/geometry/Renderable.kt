package geometry

import shader.ShaderProgram

class Renderable (var MeshList : MutableList<Mesh>) : IRenderable, Transformable() {

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", getWorldModelMatrix(), false)

        MeshList.forEach{
            it.render(shaderProgram)
        }
    }

}