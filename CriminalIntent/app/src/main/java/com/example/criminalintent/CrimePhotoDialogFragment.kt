package com.example.criminalintent

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs

class CrimePhotoDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: CrimePhotoDialogFragmentArgs by navArgs()

        val dialog = Dialog(requireActivity())

        val layoutInflater: LayoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.layout_crime_photo, null)
        dialog.setContentView(layout)

        val imageView: ImageView = layout.findViewById(R.id.crime_photo_image_view)
        imageView.setImageBitmap(BitmapFactory.decodeFile(args.photoFile.path))

        return dialog
    }

}