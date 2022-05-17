package com.lucideye.lucideye.filter;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class AssistanceFilter extends BaseFilter {
    String typeOfAssistance = "";
    private final static String FRAGMENT_SHADER_PROTO_DEUTERA = "#extension GL_OES_EGL_image_external : require\n"
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
            "                        }\n" +
            "                        //0.1 for TRITA  0.3 for PROTO\n" +
            "                        H=H+float(0.3);\n" +
            "                        if (H < float(0.0)) H += float(1.0);\n" +
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
            + "}\n";

    private final static String FRAGMENT_SHADER_TRITA = "#extension GL_OES_EGL_image_external : require\n"
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
            "                        }\n" +
            "                        //0.1 for TRITA  0.3 for PROTO\n" +
            "                        H=H+float(0.1);\n" +
            "                        if (H < float(0.0)) H += float(1.0);\n" +
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
            + "}\n";


    public AssistanceFilter() {
    }

    public AssistanceFilter(String typeOfAssistance) {
        this.typeOfAssistance = typeOfAssistance;
    }

    @NonNull
    @Override
    public String getFragmentShader() {
        if (typeOfAssistance.equals("PROTANOPES") || typeOfAssistance.equals("DEUTERANOPES"))
            return FRAGMENT_SHADER_PROTO_DEUTERA;
        else if (typeOfAssistance.equals("TRITANOPES")) return FRAGMENT_SHADER_TRITA;
        else return "";
    }
}
