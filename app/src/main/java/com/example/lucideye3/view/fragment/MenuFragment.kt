package com.lucideye.lucideye.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lucideye3.R
import com.example.lucideye3.databinding.FragmentMenuBinding
import com.example.lucideye3.view.fragment.TutorialFragment
import kotlinx.coroutines.*
import java.lang.Thread.sleep


class MenuFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var job: Job
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)
        binding.startCamera.setOnClickListener(this)
        binding.startTutorial.setOnClickListener(this)
        colorSwap()
        return binding.root
    }

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }

    //Method for the switching eye gradient
    private fun colorSwap() {
        val colorArr = IntArray(6)
        colorArr[0] = (1..254).random()
        colorArr[1] = (1..254).random()
        colorArr[2] = (1..254).random()
        colorArr[3] = (1..254).random()
        colorArr[4] = (1..254).random()
        colorArr[5] = (1..254).random()
        val goingUp = BooleanArray(6)
        for (i in 0..5) goingUp[i] = i % 2 == 0

        //All the calculations for gradient are calculated in coroutine
        job = lifecycleScope.launch(Dispatchers.IO) {
            while (this.isActive) {
                try {
                    sleep(25)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                for (i in 0..5) {
                    if (goingUp[i]) colorArr[i] += (Math.random() * 3).toInt() else colorArr[i] -= (Math.random() * 3).toInt()
                    if (colorArr[i] < 50) goingUp[i] = true
                    if (colorArr[i] > 215) goingUp[i] = false
                }
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    intArrayOf(
                        -0x1000000 or (colorArr[0] shl 16) or (colorArr[1] shl 8) or colorArr[2],
                        -0x1000000 or (colorArr[3] shl 16) or (colorArr[4] shl 8) or colorArr[5]
                    )
                )
                gd.cornerRadius = 1000f

                //Only the thread that created the view can change it
                requireActivity().runOnUiThread { binding.eye.background = gd }
            }
        }
        job.start()
    }

    override fun onClick(view: View) {
        val selectedFragment: Fragment = if (view.id == R.id.startCamera) CameraFragment()
        else TutorialFragment()
        requireActivity().supportFragmentManager.beginTransaction()
        .setCustomAnimations(
            R.animator.slide_in_left,
            R.animator.slide_in_right,
            R.animator.slide_in_left,
            R.animator.slide_in_right
        )
        .replace(R.id.fragment_container_view, selectedFragment, null)
        .addToBackStack(null)
        .commit()
    }
}
