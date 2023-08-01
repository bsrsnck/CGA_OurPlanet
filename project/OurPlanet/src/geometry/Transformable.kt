package geometry

import org.joml.Matrix4f
import org.joml.Vector3f

open class Transformable (private var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {

    fun getModelMatrix(): Matrix4f {
        val copy= Matrix4f()
        modelMatrix.get(copy)
        return copy
    }

    fun getWorldModelMatrix(): Matrix4f {
        if(parent!=null){
            return (parent!!.getWorldModelMatrix()).mul(getModelMatrix())
        }else{
            return getModelMatrix()
        }
    }

    fun rotate(pitch: Float, yaw: Float, roll: Float) {
        val m = Matrix4f()
        m.rotateXYZ(pitch,yaw,roll)
        m.mul(modelMatrix,modelMatrix)
    }

    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val m = Matrix4f()
        m.translate(altMidpoint)
        m.rotateXYZ(pitch,yaw,roll)
        m.translate(altMidpoint.negate())
        m.mul(modelMatrix,modelMatrix)
    }

    fun translate(deltaPos: Vector3f) {
        val m = Matrix4f()
        m.translate(deltaPos)
        modelMatrix.mul(m,modelMatrix)
    }

    fun preTranslate(deltaPos: Vector3f) {
        val m=Matrix4f()
        m.translate(deltaPos)
        m.mul(getWorldModelMatrix(),modelMatrix)
    }

    fun scale(scale: Vector3f) {
        modelMatrix.scale(scale)
    }

    fun getPosition(): Vector3f {
        return getModelMatrix().getColumn(3, Vector3f())
    }

    fun getWorldPosition(): Vector3f {
        return getWorldModelMatrix().getColumn(3, Vector3f())
    }

    fun getXAxis(): Vector3f {
        val pos= Vector3f()
        getModelMatrix().getColumn(0,pos)
        return pos.normalize()
    }

    fun getYAxis(): Vector3f {
        val pos= Vector3f()
        getModelMatrix().getColumn(1,pos)
        return pos.normalize()
    }

    fun getZAxis(): Vector3f {
        val pos= Vector3f()
        getModelMatrix().getColumn(2,pos)
        return pos.normalize()
    }

    fun getWorldXAxis(): Vector3f {
        val pos= Vector3f()
        getWorldModelMatrix().getColumn(0,pos)
        return pos.normalize()
    }

    fun getWorldYAxis(): Vector3f {
        val pos= Vector3f()
        getWorldModelMatrix().getColumn(1,pos)
        return pos.normalize()
    }

    fun getWorldZAxis(): Vector3f {
        val pos= Vector3f()
        getWorldModelMatrix().getColumn(2,pos)
        return pos.normalize()
    }

}
