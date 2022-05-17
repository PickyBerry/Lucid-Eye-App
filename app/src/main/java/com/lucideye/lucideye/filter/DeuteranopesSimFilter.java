package com.lucideye.lucideye.filter;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class DeuteranopesSimFilter extends BaseFilter {

    private final static String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n"
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
            + "}\n";

    public DeuteranopesSimFilter() {
    }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}