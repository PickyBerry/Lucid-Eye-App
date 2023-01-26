package com.example.lucideye3.filter

import kotlin.math.max
import kotlin.math.min

object FilterCalculator {

    private var initialRed: Int = 0
    private var initialGreen: Int = 0
    private var initialBlue: Int = 0


    private fun preProcess(initialColor: Int){
        initialRed = initialColor shr 16 and 0xff
        initialGreen = initialColor shr 8 and 0xff
        initialBlue = initialColor and 0xff
    }

    private fun postProcess(_redFactor: Int, _greenFactor: Int, _blueFactor: Int):Int{
        var redFactor=_redFactor
        var greenFactor=_greenFactor
        var blueFactor=_blueFactor
        if (redFactor < 0) redFactor = 0
        if (greenFactor < 0) greenFactor = 0
        if (blueFactor < 0) blueFactor = 0
        if (redFactor > 255) redFactor = 255
        if (greenFactor > 255) greenFactor = 255
        if (blueFactor > 255) blueFactor = 255
        return -0x1000000 or (redFactor shl 16) or (greenFactor shl 8) or blueFactor
    }


    fun calculateProtanopesSim(initialColor: Int): Int{
        preProcess(initialColor)
        val red = (0.1121f * initialRed + 0.8853f * initialGreen - 0.0005f * initialBlue).toInt()
        val green = (0.1127f * initialRed + 0.8897f * initialGreen - 0.0001f * initialBlue).toInt()
        val blue = (0.0045f * initialRed + 0.0f * initialGreen + 1.0019f * initialBlue).toInt()
        return postProcess(red,green,blue)
    }

    fun calculateDeuteranopesSim(initialColor: Int): Int{
        preProcess(initialColor)
        val red = (0.292f * initialRed + 0.7054f * initialGreen - 0.0003f * initialBlue).toInt()
        val green = (0.2934f * initialRed + 0.7089f * initialGreen + 0.0000f * initialBlue).toInt()
        val blue = (-0.02098f * initialRed + 0.02559f * initialGreen + 1.0019f * initialBlue).toInt()
        return postProcess(red,green,blue)
    }

    fun calculateTritanopesSim(initialColor: Int): Int{
        preProcess(initialColor)
        val l = 0.0000946908f * initialRed + 0.00019291f * initialGreen + 0.00001403f * initialBlue
        val m = 0.000046877f * initialRed + 0.00022862f * initialGreen + 0.000026153f * initialBlue
        val s = 0.00000539278f * initialRed + 0.0002595562f * initialGreen + 0.00003666445f * initialBlue
        val red = (18140.343f * l - 15387f * m + 562.224f * s).toInt()
        val green = (-3730.038f * l + 7601.859f * m - 556.589f * s).toInt()
        val blue = (98.787f * l - 640.127f * m + 3856.903f * s).toInt()
        return postProcess(red,green,blue)
    }

    fun calculateAid(initialColor: Int, rotationFactor: Double): Int{
        val red:Int
        val green:Int
        val blue:Int
        preProcess(initialColor)
        val varR: Double = (initialRed / 255.0)
        val varG: Double = (initialGreen / 255.0)
        val varB: Double = (initialBlue / 255.0)

        val varMin: Double = min(varR, min(varG, varB))
        val varMax: Double = max(varR, max(varG, varB))
        val delMax: Double = varMax - varMin

        val V: Double = varMax
        var H = 0.0
        var S = 0.0
        if (delMax == 0.0) {
            H = 0.0
            S = 0.0
        } else {
            S = delMax / varMax

            val delR: Double = (((varMax - varR) / 6) + (delMax / 2)) / delMax
            val delG: Double = (((varMax - varG) / 6) + (delMax / 2)) / delMax
            val delB: Double = (((varMax - varB) / 6) + (delMax / 2)) / delMax

            when {
                varR == varMax -> H = delB - delG
                varG == varMax -> H = (1.0 / 3.0) + delR - delB
                varB == varMax -> H = (2.0 / 3.0) + delG - delR
            }

            if (H < 0) H = 0.0
            if (H > 1) H -= 1.0
        }
        H+=rotationFactor
        if (H < 0) H += 1
        if (H > 1) H -= 1

        if (S == 0.0) {

            red =  (V * 255).toInt()
            green = (V * 255).toInt()
            blue = (V * 255).toInt()

        } else {
            var varh = H * 6
            if (varh == 6.0) varh = 0.0
            val vari :Int =  varh.toInt()
            val var1 :Double = V * (1 - S)
            val var2 :Double = V * (1 - S * (varh - vari))
            val var3 :Double = V * (1 - S * (1 - (varh - vari)))
            var varr :Double
            var varg: Double
            var varb: Double
            when (vari) {
                0 -> {
                    varr = V
                    varg = var3
                    varb = var1
                }
                1 -> {
                    varr = var2
                    varg = V
                    varb = var1
                }
                2 -> {
                    varr = var1
                    varg = V
                    varb = var3
                }
                3 -> {
                    varr = var1
                    varg = var2
                    varb = V
                }
                4 -> {
                    varr = var3
                    varg = var1
                    varb = V
                }
                else -> {
                    varr = V
                    varg = var1
                    varb = var2
                }
            }

            red = (varr * 255).toInt()
            green = (varg * 255).toInt()
            blue = (varb * 255).toInt()
        }
        return postProcess(red,green,blue)
    }

}