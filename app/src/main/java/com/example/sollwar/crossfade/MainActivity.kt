package com.example.sollwar.crossfade

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.sollwar.crossfade.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        var crossFadeValue = binding.seekBar.progress + 2
        binding.fileName1.text = getString(R.string.track_1, viewModel.path1?.lastPathSegment)
        binding.fileName2.text = getString(R.string.track_2, viewModel.path2?.lastPathSegment)
        binding.crossFadeValue.text = getString(R.string.cross_fade_value, crossFadeValue.toString())

        val getFilePath1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.path1 = result.data!!.data
                binding.fileName1.text = getString(R.string.track_1, result.data!!.data!!.lastPathSegment)
            }
        }
        val getFilePath2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.path2 = result.data!!.data
                binding.fileName2.text = getString(R.string.track_2, result.data!!.data!!.lastPathSegment)
            }
        }

        binding.selectFile1.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            getFilePath1.launch(intent)
        }
        binding.selectFile2.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            getFilePath2.launch(intent)
        }

        binding.playButton.setOnClickListener {
            viewModel.startCrossFade(this, crossFadeValue)
            if (viewModel.path1 == null) Toast.makeText(this, getString(R.string.сhoose_file_1), Toast.LENGTH_SHORT).show()
            if (viewModel.path2 == null) Toast.makeText(this, getString(R.string.сhoose_file_2), Toast.LENGTH_SHORT).show()
        }

        viewModel.getNewCrossFadeValue().observe(
            this
        ) { value ->
            value?.let {
                Toast.makeText(this, getString(R.string.new_crossfade_time, value.toString()), Toast.LENGTH_SHORT).show()
                crossFadeValue = value
                binding.crossFadeValue.text = getString(R.string.cross_fade_value, crossFadeValue.toString())
                binding.seekBar.progress = crossFadeValue - 2
            }
        }

        viewModel.getPlayed().observe(
            this
        ) { played ->
            played?.let {
                when (played) {
                    "mp1Played" -> {
                        binding.playedTextView.text = getString(R.string.played_1_file)
                        binding.playButton.isEnabled = true
                    }
                    "mp2Played" -> {
                        binding.playedTextView.text = getString(R.string.played_2_file)
                        binding.playButton.isEnabled = true
                    }
                    "crossPlayed" -> {
                        binding.playedTextView.text = getString(R.string.transition)
                        binding.playButton.isEnabled = false
                    }
                }
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                crossFadeValue = p1 + 2
                binding.crossFadeValue.text = getString(R.string.cross_fade_value, crossFadeValue.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }
}