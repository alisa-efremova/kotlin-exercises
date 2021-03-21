package com.example.criminalintent

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File

class CrimePhotoDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val photoFile = arguments?.getSerializable(EXTRA_PHOTO_FILE) as File

        val dialog = Dialog(requireActivity())

        val layoutInflater: LayoutInflater = requireActivity().getSystemService(
            Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.layout_crime_photo, null)
        dialog.setContentView(layout)

        val imageView: ImageView = layout.findViewById(R.id.crime_photo_image_view)

        imageView.viewTreeObserver.addOnGlobalLayoutListener {
            PictureUtils().setScaledImage(imageView, photoFile.path)
        }

        return dialog
    }

    companion object {
        private const val EXTRA_PHOTO_FILE = "photo_file"

        fun newInstance(photoFile: File): DialogFragment {
            return CrimePhotoDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_PHOTO_FILE, photoFile)
                }
            }
        }
    }
}