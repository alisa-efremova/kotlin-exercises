package com.example.beatbox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.beatbox.databinding.ListItemSoundBinding

class SoundAdapter(private val sounds: List<Sound>)
    : RecyclerView.Adapter<SoundHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ListItemSoundBinding>(
            layoutInflater, R.layout.list_item_sound, parent, false)
        return SoundHolder(binding)
    }

    override fun onBindViewHolder(holder: SoundHolder, position: Int) {
        val sound = sounds[position]
        holder.bind(sound)
    }

    override fun getItemCount() = sounds.size
}