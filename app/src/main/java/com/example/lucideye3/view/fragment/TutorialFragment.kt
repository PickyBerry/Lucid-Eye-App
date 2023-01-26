package com.example.lucideye3.view.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lucideye3.databinding.FragmentTutorialBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep


class TutorialFragment : Fragment() {
    private lateinit var binding: FragmentTutorialBinding
    private lateinit var textViews: MutableList<TextView>
    private lateinit var imTextViews: MutableList<TextView>
    private lateinit var imageViews: MutableList<ImageView>
    private var page: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialBinding.inflate(layoutInflater)
        textViews = mutableListOf(
            binding.upperText, binding.middleText, binding.lowerText,
            binding.upperText2, binding.middleText2, binding.middleText3
        )
        imTextViews = mutableListOf(
            binding.tvImageLeft, binding.tvImageMiddle, binding.tvImageRight,
            binding.tvImageLeft2, binding.tvImageMiddle2, binding.tvImageRight2
        )
        imageViews = mutableListOf(
            binding.imageLeft, binding.imageMiddle, binding.imageRight, binding.imageLeft2,
            binding.imageMiddle2,binding.imageRight2, binding.colorPicker1, binding.colorPicker2
        )


        binding.continueBtn.setOnClickListener {
            if (page == 1) page = 2 else if (page == 2) {
                page = 3
                binding.returnBtn.visibility = View.VISIBLE
                binding.continueBtn.visibility = View.GONE
            }
        }
        binding.returnBtn.setOnClickListener { parentFragmentManager.popBackStack() }


        //The key coroutine that will play the animations and switch pages
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                appearAnimation(textViews[0])
                sleep(1500)
                appearAnimation(textViews[1])
                sleep(1500)
                for (i in 0..2) {
                    appearAnimation(imageViews[i])
                    appearAnimation(imTextViews[i])
                }
                appearAnimation(textViews[2])
                sleep(1000)
                appearAnimation(binding.continueBtn)
                while (page != 2) {}
                sleep(500)
                for (i in 0..2) {
                    disappearAnimation(textViews[i])
                    disappearAnimation(imageViews[i])
                    disappearAnimation(imTextViews[i])
                    disappearAnimation(binding.continueBtn)
                }
                sleep(1500)
                appearAnimation(textViews[3])
                sleep(1500)
                appearAnimation(textViews[4])
                sleep(1500)
                for (i in 3..5) {
                    appearAnimation(imageViews[i])
                    appearAnimation(imTextViews[i])
                }
                sleep(1000)
                appearAnimation(binding.continueBtn)
                while (page != 3) {
                }
                sleep(250)
                for (i in 3..5) {
                    disappearAnimation(textViews[i])
                    disappearAnimation(imageViews[i])
                    disappearAnimation(imTextViews[i])
                }
                sleep(1500)
                appearAnimation(textViews[5])
                sleep(1500)
                appearAnimation(imageViews[6])
                appearAnimation(imageViews[7])
                sleep(1000)
                appearAnimation(binding.returnBtn)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    private fun appearAnimation(view: View){
        view.animate().alpha(1.0f).duration = 2000
    }

    private fun disappearAnimation(view: View){
        view.animate().alpha(0.0f).duration = 1000
    }
}