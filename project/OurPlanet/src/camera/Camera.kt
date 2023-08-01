package camera


import geometry.Transformable
import org.joml.Math
import org.joml.Matrix4f
import shader.ShaderProgram

class Camera (
    var fieldofview :Float= Math.toRadians(90F),
    var aspectratio:Float = 16F/9F,
    var nearplane:Float = 0.1F,
    var farplane:Float = 100F) : ICamera, Transformable() {

    override fun getCalculateViewMatrix(): Matrix4f {
        return Matrix4f().lookAt(getWorldPosition(),getWorldPosition().sub(getWorldZAxis()),getWorldYAxis())
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fieldofview,aspectratio,nearplane,farplane)
    }

    override fun bind(shader: ShaderProgram) {
        shader.use()

        shader.setUniform("projection_matrix", getCalculateProjectionMatrix(), false)
        shader.setUniform("view_matrix", getCalculateViewMatrix(), false)
    }


}
