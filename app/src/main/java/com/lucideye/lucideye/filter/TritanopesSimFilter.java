package com.lucideye.lucideye.filter;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class TritanopesSimFilter extends BaseFilter {

    private final static String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n"
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
            + "}\n";

    public TritanopesSimFilter() {
    }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}