package com.lucideye.lucideye.fragment

import com.otaliastudios.cameraview.CameraListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.otaliastudios.cameraview.filter.NoFilter
import com.otaliastudios.cameraview.PictureResult
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.view.animation.LinearInterpolator
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.lucideye3.R
import com.example.lucideye3.databinding.FragmentCameraBinding
import java.io.IOException
import java.util.*
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.lucideye3.filter.FilterApplier
import com.example.lucideye3.State
import com.example.lucideye3.filter.CameraViewFilter
import com.example.lucideye3.filter.FilterNames
import com.example.lucideye3.filter.HSVConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CameraFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentCameraBinding
    private var bitmap: Bitmap? = null
    private var savedBitmap: Bitmap? = null
    private var selectedFilter: FilterNames = FilterNames.DEFAULT
    var state = State.CAMERA
    private var timer: Timer? = Timer()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(LayoutInflater.from(requireActivity()))
        val adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.filterNames, R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding.camera.setLifecycleOwner(viewLifecycleOwner)
        binding.camera.playSounds = false
        binding.camera.isSoundEffectsEnabled = false
        binding.btnCapture.setOnClickListener(this)
        binding.btnAccept.setOnClickListener(this)
        binding.btnReject.setOnClickListener(this)
        binding.colorPicker.setOnClickListener(this)
        binding.gallery.setOnClickListener(this)
        binding.saveButton.setOnClickListener(this)
        setLayoutState(State.CAMERA)

        //Spinner adapter for selecting filters
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View?, selectedItemPosition: Int, selectedId: Long
            ) {
                selectedFilter = FilterNames.values().first { it.ordinal == selectedItemPosition }
                if (selectedFilter != FilterNames.DEFAULT) {
                    if (state == State.CAMERA || state == State.CAMERA_WITH_COLORPICKER)
                        binding.camera.filter = CameraViewFilter(selectedFilter).generateShader()
                    else {
                        if (state == State.SHOWING_IMAGE_FROM_GALLERY)
                            setLayoutState(State.LOADING_FOR_GALLERY_PIC)
                        else if (state == State.SHOWING_CAPTURED_IMAGE)
                            setLayoutState(State.LOADING_FOR_CAPTURED_PIC)
                        applyFilter(selectedFilter)
                    }
                } else {
                    setLayoutState(state)
                    binding.camera.filter = NoFilter()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        //Camera events listener
        binding.camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                savedBitmap = BitmapFactory.decodeByteArray(result.data, 0, result.data.size)
                if (state == State.LOADING_FOR_CAPTURED_PIC) applyFilter(selectedFilter)
                else if (state == State.CAMERA_WITH_COLORPICKER)
                    binding.colorName.text = HSVConverter.getColorName(savedBitmap!!.getPixel(savedBitmap!!.width / 2, savedBitmap!!.height / 2))
            }
        })


        //Loading circle animation
        val valueAnimator = ValueAnimator.ofInt(1, 360)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 500
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { animation -> binding.customLoadingBar.setValue((animation.animatedValue as Int)) }
        valueAnimator.start()
        return binding.root
    }

    //Method for applying a filter to an image from gallery
    fun applyFilter(selectedFilter: FilterNames) {
        lifecycleScope.launch(Dispatchers.IO) {
            bitmap = FilterApplier().applyFilter(savedBitmap, selectedFilter)
            requireActivity().runOnUiThread {
                if (state == State.LOADING_FOR_GALLERY_PIC) setLayoutState(State.SHOWING_IMAGE_FROM_GALLERY)
                else setLayoutState(State.SHOWING_CAPTURED_IMAGE)
            }
        }
    }

    //Opening the gallery
    private var startGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImage = result.data!!.data
            try {
                savedBitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().applicationContext.contentResolver,
                    selectedImage
                )
                bitmap = savedBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            binding.spinner.setSelection(0)
            setLayoutState(State.SHOWING_IMAGE_FROM_GALLERY)
        } else {
            binding.spinner.setSelection(0)
            setLayoutState(State.CAMERA)
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnCapture -> {
                setLayoutState(State.LOADING_FOR_CAPTURED_PIC)
                binding.camera.takePicture()
            }
            R.id.colorPicker -> {
                if (state == State.CAMERA) {
                    setLayoutState(State.CAMERA_WITH_COLORPICKER)
                    timer = Timer()
                    timer!!.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (selectedFilter == FilterNames.DEFAULT) binding.camera.takePictureSnapshot() else binding.camera.takePicture()
                        }
                    }, 0, 500)
                } else {
                    timer!!.cancel()
                    setLayoutState(State.CAMERA)
                }
            }
            R.id.btn_reject -> {
                binding.spinner.setSelection(0)
                setLayoutState(State.CAMERA)
            }
            R.id.btn_accept -> {
                binding.spinner.setSelection(0)
                MediaStore.Images.Media.insertImage(
                    requireActivity().contentResolver,
                    bitmap,
                    "LucidEye" + Calendar.getInstance().time,
                    ""
                )
                Toast.makeText(activity, getString(R.string.saved_to_gallery), Toast.LENGTH_LONG)
                    .show()
                setLayoutState(State.CAMERA)
            }
            R.id.gallery -> {
                setLayoutState(State.LOADING_FOR_GALLERY_PIC)
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startGalleryLauncher.launch(galleryIntent)
            }
            R.id.saveButton -> {
                MediaStore.Images.Media.insertImage(
                    requireActivity().contentResolver,
                    bitmap,
                    "LucidEye" + Calendar.getInstance().time,
                    ""
                )
                Toast.makeText(activity, getString(R.string.saved_to_gallery), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    //Overriding the press of back button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (state == State.CAMERA || state == State.CAMERA_WITH_COLORPICKER)
                        parentFragmentManager.popBackStack()
                    else setLayoutState(State.CAMERA)
                    return true
                }
                return false
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    fun setLayoutState(newState: State) {
        state = newState
        when (state) {
            State.CAMERA -> {
                binding.camera.open()
                binding.colorName.visibility = View.GONE
                binding.crosshair.visibility = View.GONE
                binding.btnCapture.visibility = View.VISIBLE
                binding.btnAccept.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
                binding.gallery.visibility = View.VISIBLE
                binding.camera.visibility = View.VISIBLE
                binding.ivBitmap.visibility = View.INVISIBLE
                binding.colorPicker.visibility = View.VISIBLE
                binding.saveButton.visibility = View.GONE
                binding.customLoadingBar.visibility = View.INVISIBLE
            }
            State.CAMERA_WITH_COLORPICKER -> {
                binding.colorName.visibility = View.VISIBLE
                binding.crosshair.visibility = View.VISIBLE
                binding.customLoadingBar.visibility = View.INVISIBLE
            }
            State.SHOWING_CAPTURED_IMAGE -> {
                binding.btnAccept.visibility = View.VISIBLE
                binding.btnReject.visibility = View.VISIBLE
                binding.camera.visibility = View.GONE
                binding.btnCapture.visibility = View.GONE
                binding.colorPicker.visibility = View.GONE
                binding.crosshair.visibility = View.GONE
                binding.colorName.visibility = View.GONE
                binding.ivBitmap.visibility = View.VISIBLE
                if (selectedFilter != FilterNames.DEFAULT) binding.ivBitmap.setImageBitmap(bitmap)
                else binding.ivBitmap.setImageBitmap(savedBitmap)
                binding.colorPicker.visibility = View.GONE
                binding.gallery.visibility = View.GONE
                binding.customLoadingBar.visibility = View.INVISIBLE
            }
            State.SHOWING_IMAGE_FROM_GALLERY -> {
                binding.saveButton.visibility = View.VISIBLE
                binding.ivBitmap.visibility = View.VISIBLE
                if (selectedFilter != FilterNames.DEFAULT) binding.ivBitmap.setImageBitmap(bitmap)
                else binding.ivBitmap.setImageBitmap(savedBitmap)
                binding.btnCapture.visibility = View.GONE
                binding.colorPicker.visibility = View.GONE
                binding.camera.visibility = View.GONE
                binding.saveButton.visibility = View.VISIBLE
                binding.customLoadingBar.visibility = View.INVISIBLE
            }
            State.LOADING_FOR_CAPTURED_PIC -> {
                binding.btnAccept.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
                binding.customLoadingBar.visibility = View.VISIBLE
                binding.btnCapture.visibility = View.GONE
                binding.colorPicker.visibility = View.GONE
                binding.gallery.visibility = View.GONE
            }
            State.LOADING_FOR_GALLERY_PIC -> {
                binding.colorName.visibility = View.GONE
                binding.crosshair.visibility = View.GONE
                binding.customLoadingBar.visibility = View.VISIBLE
            }
        }
    }

}