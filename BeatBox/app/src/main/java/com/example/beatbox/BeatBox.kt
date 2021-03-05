package com.example.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException

private const val SOUNDS_FOLDER = "sample_sounds"
private const val MAX_SOUNDS = 5

class BeatBox(private val assets: AssetManager) {

    val sounds: List<Sound>
    var speed = 0
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_SOUNDS)
        .build()

    init {
        sounds = loadSounds()
    }

    fun play(sound: Sound) {
        sound.soundId?.let {
            val rate = 1.0f + speed / 100.0f
            soundPool.play(it, 1.0f, 1.0f, 1, 1, rate)
        }
    }

    fun release() {
        soundPool.release()
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
            val sound = Sound(assetPath)
            try {
                loadSound(sound)
                sounds.add(sound)
            } catch (e: IOException) {
                Log.e("BeatBox", "Sound $soundName cannot be loaded", e)
            }
        }

        return sounds
    }

    private fun loadSound(sound: Sound) {
        val afd: AssetFileDescriptor = assets.openFd(sound.assetPath)
        sound.soundId = soundPool.load(afd, 1)
    }
}