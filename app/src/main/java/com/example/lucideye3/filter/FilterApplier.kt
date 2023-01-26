package com.example.lucideye3.filter

import android.graphics.Bitmap

class FilterApplier {
    fun applyFilter(savedBitmap: Bitmap?,selectedFilter:FilterNames):Bitmap{
        val size = savedBitmap!!.width * savedBitmap.height
        val pixels = IntArray(size)
        savedBitmap.getPixels(pixels, 0, savedBitmap.width, 0, 0, savedBitmap.width, savedBitmap.height)
        if (selectedFilter != FilterNames.DEFAULT) {
            for (i in pixels.indices) {
                when (selectedFilter) {
                    FilterNames.PROTANOPES_SIM -> pixels[i] =
                        FilterCalculator.calculateProtanopesSim(pixels[i])
                    FilterNames.DEUTERANOPES_SIM -> pixels[i] =
                        FilterCalculator.calculateDeuteranopesSim(pixels[i])
                    FilterNames.TRITANOPES_SIM -> pixels[i] =
                        FilterCalculator.calculateTritanopesSim(pixels[i])

                    //Rotation factors for protanopes,deuteranopes,tritanopes assistances are 0.3,0.25,0.1 coaccordingly
                    FilterNames.PROTANOPES_AID -> pixels[i] =
                        FilterCalculator.calculateAid(pixels[i], 0.3)
                    FilterNames.DEUTERANOPES_AID -> pixels[i] =
                        FilterCalculator.calculateAid(pixels[i], 0.25)
                    FilterNames.TRITANOPES_AID -> pixels[i] =
                        FilterCalculator.calculateAid(pixels[i], 0.1)
                }

            }
        }

        val bitmap = savedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        //Setting the new bitmap
        bitmap.setPixels(
            pixels, 0, savedBitmap.width, 0, 0,
            savedBitmap.width, savedBitmap.height
        )
        return bitmap
    }
}