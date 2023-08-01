package skybox

import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glGenTextures
import texture.Texture2D
import java.nio.ByteBuffer

class Skybox(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean) : Texture2D(imageData, width, height,
    genMipMaps
) {

    val texID: Int = glGenTextures()

    init {

        glBindTexture(GL_TEXTURE_CUBE_MAP, texID)
    }

}