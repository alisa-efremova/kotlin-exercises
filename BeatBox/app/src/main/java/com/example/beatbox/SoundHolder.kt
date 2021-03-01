package com.example.beatbox

import androidx.recyclerview.widget.RecyclerView
import com.example.beatbox.databinding.ListItemSoundBinding

class SoundHolder(private val binding: ListItemSoundBinding)
    : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.viewModel = SoundViewModel()
    }

    fun bind(sound: Sound) {
        binding.apply {
            viewModel?.sound = sound
            executePendingBindings()
        }
    }
}