package com.example.lucideye3.filter

import kotlin.math.max
import kotlin.math.min

object HSVConverter {
    fun getColorName(color:Int):String{
        val varR = (color shr 16 and 0xff) / 255.0
        val varG = (color shr 8 and 0xff) / 255.0
        val varB = (color and 0xff) / 255.0
        val varMin = min(varR, min(varG, varB))
        val varMax = max(varR, max(varG, varB))
        val delMax = varMax - varMin
        var H = 0.0
        var S = 0.0
        if (delMax == 0.0) {
            H = 0.0
            S = 0.0
        } else {
            S = delMax / varMax
            val delR = ((varMax - varR) / 6 + delMax / 2) / delMax
            val delG = ((varMax - varG) / 6 + delMax / 2) / delMax
            val delB = ((varMax - varB) / 6 + delMax / 2) / delMax
            when {
                varR == varMax -> H = delB - delG
                varG == varMax -> H = 1.0 / 3.0 + delR - delB
                varB == varMax -> H = 2.0 / 3.0 + delG - delR
            }
            if (H < 0) H = 0.0
            if (H > 1) H -= 1.0
        }
        H *= 360
        if (varMax >= 0.85 && S <= 0.1) return "WHITE"
        else if (varMax <= 0.15) return "BLACK"
        else if (S <= 0.1) return "GREY"
        else if (H == 0.0) return "PINK"
        else if (H > 0 && H <= 15) return "RED"
        else if (H in 15.0..45.0 && varMax <= 50) return "BROWN"
        else if (H in 15.0..45.0) return "ORANGE"
        else if (H in 46.0..75.0) return "YELLOW"
        else if (H in 76.0..105.0) return "LIGHT GREEN"
        else if (H in 106.0..165.0) return "GREEN"
        else if (H in 166.0..195.0) return "CYAN"
        else if (H in 196.0..255.0) return "BLUE"
        else if (H in 256.0..285.0) return "PURPLE"
        else if (H in 286.0..315.0) return "MAGENTA"
        else if (H in 316.0..360.0) return "PINK"
        else return ""
    }
}