package com.example.lucideye3.filter

import com.otaliastudios.cameraview.filter.BaseFilter
import java.util.logging.Filter

class CameraViewFilter(private val type: FilterNames): BaseFilter() {

     fun generateShader(): com.otaliastudios.cameraview.filter.Filter {
        return when (type) {
            FilterNames.DEFAULT -> DefaultFilter()
            FilterNames.PROTANOPES_SIM -> ProtanopesSimFilter()
            FilterNames.DEUTERANOPES_SIM -> DeuteranopesSimFilter()
            FilterNames.TRITANOPES_SIM -> TritanopesSimFilter()
            FilterNames.PROTANOPES_AID -> ProtanopesAidFilter()
            FilterNames.DEUTERANOPES_AID -> DeuteranopesAidFilter()
            FilterNames.TRITANOPES_AID -> TritanopesAidFilter()
        }
    }

    class DefaultFilter: BaseFilter(){
        override fun getFragmentShader() = ""
    }

    class ProtanopesSimFilter: BaseFilter(){
        override fun getFragmentShader() = PROTANOPES_SIM_FILTER
    }

    class DeuteranopesSimFilter: BaseFilter(){
        override fun getFragmentShader() = DEUTERANOPES_SIM_FILTER
    }

    class TritanopesSimFilter: BaseFilter(){
        override fun getFragmentShader() = TRITANOPES_SIM_FILTER
    }

    class ProtanopesAidFilter: BaseFilter(){
        override fun getFragmentShader() = RGB_TO_HSV + PROTANOPES_AID_FILTER + HSV_TO_RGB
    }

    class DeuteranopesAidFilter: BaseFilter(){
        override fun getFragmentShader() = RGB_TO_HSV + DEUTERANOPES_AID_FILTER + HSV_TO_RGB
    }

    class TritanopesAidFilter: BaseFilter(){
        override fun getFragmentShader() = RGB_TO_HSV + TRITANOPES_AID_FILTER + HSV_TO_RGB
    }


    companion object{

    private const val PROTANOPES_SIM_FILTER = ("#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n"
            + "uniform samplerExternalOES sTexture;\n"
            + "void main() {\n"
            + "  vec4 color = texture2D(sTexture, " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
            + " float r=color.r*float(255.0);\n"
            + " float g=color.g*float(255.0);\n"
            + "  float b=color.b*float(255.0);\n"
            + " float currentRedFactor = float(0.1121) * r + float(0.8853) * g - float(0.0005) * b;\n"
            + " float currentGreenFactor = float(0.1127) * r + float(0.8897) * g - float(0.0001) * b;\n"
            + " float currentBlueFactor = float(0.0045) * r + float(0.0) * g + float(1.0019) * b;\n"
            + "  float colorR = currentRedFactor/float(255.0);\n"
            + "  float colorG = currentGreenFactor/float(255.0);\n"
            + "  float colorB = currentBlueFactor/float(255.0);\n"
            + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
            + "}\n")

     private const val DEUTERANOPES_SIM_FILTER = ("#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n"
                + "uniform samplerExternalOES sTexture;\n"
                + "void main() {\n"
                + "  vec4 color = texture2D(sTexture, " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
                + " float r=color.r*float(255.0);\n"
                + " float g=color.g*float(255.0);\n"
                + "  float b=color.b*float(255.0);\n"
                + " float currentRedFactor = float(0.292) * r + float(0.7054) * g - float(0.0003) * b;\n"
                + " float currentGreenFactor = float(0.2934) * r + float(0.7089) * g;\n"
                + " float currentBlueFactor = float(-0.02098) * r + float(0.02559) * g + float(1.0019) * b;\n"
                + "  float colorR = currentRedFactor/float(255.0);\n"
                + "  float colorG = currentGreenFactor/float(255.0);\n"
                + "  float colorB = currentBlueFactor/float(255.0);\n"
                + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
                + "}\n")

        private const val TRITANOPES_SIM_FILTER = ("#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n"
                + "uniform samplerExternalOES sTexture;\n"
                + "void main() {\n"
                + "  vec4 color = texture2D(sTexture, " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
                + " float r=color.r*float(255.0);\n"
                + " float g=color.g*float(255.0);\n"
                + "  float b=color.b*float(255.0);\n"
                + "  float l=float(0.0000946908)*r+float(0.00019291)*g+float(0.00001403)*b;\n"
                + "  float m=float(0.000046877)*r+float(0.00022862)*g+float(0.000026153)*b;\n"
                + "  float s=float(0.00000539278)*r+float(0.0002595562)*g+float(0.00003666445)*b;\n"
                + " float currentRedFactor = float(0.99942)*r+float(0.1277)*g-float(0.1272)*b;\n"
                + " float currentGreenFactor = float(0.0002)*r+float(0.8739)*g+float(0.12567)*b;\n"
                + " float currentBlueFactor = float(0.0002)*r+float(0.8737)*g+float(0.119)*b;\n"
                + "  float colorR = currentRedFactor/float(255.0);\n"
                + "  float colorG = currentGreenFactor/float(255.0);\n"
                + "  float colorB = currentBlueFactor/float(255.0);\n"
                + "                       if (colorR < float(0.0)) colorR = float(0.0);\n" +
                "                        if (colorG < float(0.0)) colorG = float(0.0);\n" +
                "                        if (colorB < float(0.0)) colorB = float(0.0);\n" +
                "                        if (colorR > float(255.0)) colorR = float(255.0);\n" +
                "                        if (colorG > float(255.0)) colorG = float(255.0);\n" +
                "                        if (colorB > float(255.0)) colorB = float(255.0);"
                + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
                + "}\n")

        private const val RGB_TO_HSV =
            ("#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ";\n"
                    + "uniform samplerExternalOES sTexture;\n"
                    + "void main() {\n"
                    + "  vec4 color = texture2D(sTexture, " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
                    + "float var_Min; float var_Max;  if ((color.r>color.g)&&(color.r>color.b)) var_Max=color.r; else if ((color.g>color.r)&&(color.g>color.b)) var_Max=color.g;  else var_Max=color.b;\n"
                    + "if ((color.r<color.g)&&(color.r<color.b)) var_Min=color.r; else if ((color.g<color.r)&&(color.g<color.b)) var_Min=color.g;  else var_Min=color.b;\n"
                    + "                        float del_Max = var_Max - var_Min    ;         //Delta RGB value\n" +
                    "\n" +
                    "                        float V = var_Max;\n" +
                    "                        float H=float(0.0);\n" +
                    "                        float S=float(0.0);\n" +
                    "                        if (del_Max == float(0.0))                     //This is a gray, no chroma...\n" +
                    "                        {\n" +
                    "                            H = float(0.0);\n" +
                    "                            S = float(0.0);\n" +
                    "                        } else                                    //Chromatic data...\n" +
                    "                        {\n" +
                    "                            S = del_Max / var_Max;\n" +
                    "\n" +
                    "                            float del_R = (((var_Max - color.r) / float(6.0)) + (del_Max / float(2.0))) / del_Max;\n" +
                    "                            float del_G = (((var_Max - color.g) / float(6.0)) + (del_Max / float(2.0))) / del_Max;\n" +
                    "                            float del_B = (((var_Max - color.b) / float(6.0)) + (del_Max / float(2.0))) / del_Max;\n" +
                    "\n" +
                    "                            if (color.r == var_Max) H = del_B - del_G;\n" +
                    "                            else if (color.g == var_Max) H = (float(1.0) / float(3.0)) + del_R - del_B;\n" +
                    "                            else if (color.b == var_Max) H = (float(2.0) / float(3.0)) + del_G - del_R;\n" +
                    "\n" +
                    "                            if (H < float(0.0)) H = float(0.0);\n" +
                    "                            if (H > float(1.0)) H -= float(1.0);\n" +
                    "                        }\n")

        private const val PROTANOPES_AID_FILTER = ("H=H+float(0.3); \n")
        private const val DEUTERANOPES_AID_FILTER = ("H=H+float(0.25); \n")
        private const val TRITANOPES_AID_FILTER = ("H=H+float(0.1); \n")

        private const val HSV_TO_RGB =
                    ("                        if (H < float(0.0)) H += float(1.0);\n" +
                    "                        if (H > float(1.0)) H -= float(1.0);\n" +
                    "\n" +
                    "\n" +
                    "                        if ( S == float(0.0) )\n" +
                    "                        {\n" +
                    "\n"
                    + "  gl_FragColor = vec4(V, V, V, color.a);\n"
                    +
                    "                        }\n" +
                    "                        else\n" +
                    "                        {\n" +
                    "                            float var_h = H * float(6.0);\n" +
                    "                            if ( var_h == float(6.0))  var_h = float(0.0);    \n" +
                    "                            float var_i;  if (var_h>float(5.0)) var_i=float(5.0); else if (var_h>float(4.0)) var_i=float(4.0); else if (var_h>float(3.0)) var_i=float(3.0); else if (var_h>float(2.0)) var_i=float(2.0); else if (var_h>float(1.0)) var_i=float(1.0); else var_i=float(0.0);\n" +
                    "                            float  var_1 = V * ( float(1.0) - S );\n" +
                    "                            float var_2 = V * ( float(1.0) - S * ( var_h - var_i ) );\n" +
                    "                            float var_3 = V * ( float(1.0) - S * ( float(1.0) - ( var_h - var_i ) ) );\n" +
                    "                                    float var_r,var_g,var_b;\n" +
                    "                            if      ( var_i == float(0.0) ) { var_r = V     ; var_g = var_3 ; var_b = var_1; }\n" +
                    "                            else if ( var_i == float(1.0) ) { var_r = var_2 ; var_g = V     ; var_b = var_1; }\n" +
                    "                            else if ( var_i == float(2.0) ) { var_r = var_1 ; var_g = V     ; var_b = var_3; }\n" +
                    "                            else if ( var_i == float(3.0) ) { var_r = var_1 ; var_g = var_2 ; var_b = V;     }\n" +
                    "                            else if ( var_i == float(4.0) ) { var_r = var_3 ; var_g = var_1 ; var_b = V;     }\n" +
                    "                            else                   { var_r = V     ; var_g = var_1 ; var_b = var_2; }\n" +
                    "\n"
                    + "  gl_FragColor = vec4(var_r, var_g, var_b, color.a);\n"
                    + "}\n"
                    + "}\n")
    }

    override fun getFragmentShader(): String {
        return ""
    }
}