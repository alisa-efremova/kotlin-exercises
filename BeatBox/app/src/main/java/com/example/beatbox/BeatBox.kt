package com.example.beatbox

import android.content.res.AssetManager

private const val SOUNDS_FOLDER = "sample_sounds"

class BeatBox(private val assets: AssetManager) {

    val sounds: List<Sound>

    init {
        sounds = loadSounds()
    }

    private fun loadSounds(): List<Sound> {
        val soundNames = try {
            assets.list(SOUNDS_FOLDER)!!
        } catch (e: Exception) {
            emptyArray()
        }

        val sounds = mutableListOf<Sound>()
        soundNames.forEach { soundName ->
            val assetPath = "$SOUNDS_FOLDER/$soundName"
            sounds.add(Sound(assetPath))
        }

        return sounds
    }
}