package camera

import org.joml.Matrix4f
import shader.ShaderProgram

interface ICamera {

    fun getCalculateViewMatrix(): Matrix4f

    fun getCalculateProjectionMatrix(): Matrix4f

    fun bind(shader: ShaderProgram)

}