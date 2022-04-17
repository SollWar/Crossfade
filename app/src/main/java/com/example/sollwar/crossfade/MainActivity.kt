package com.example.sollwar.crossfade

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
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

        var crossFadeValue = binding.seekBar.progress + 1

        val getFilePath1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.path1 = Environment.getExternalStorageDirectory().toString() +
                        "/" +
                        result.data!!.data!!.lastPathSegment!!.substring(8)
            }
        }
        val getFilePath2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.path2 = Environment.getExternalStorageDirectory().toString() +
                        "/" +
                        result.data!!.data!!.lastPathSegment!!.substring(8)
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
            viewModel.startCrossFade(crossFadeValue)
            if (viewModel.path1 == "") Toast.makeText(this, "Выберите файл 1", Toast.LENGTH_SHORT).show()
            if (viewModel.path2 == "") Toast.makeText(this, "Выберите файл 2", Toast.LENGTH_SHORT).show()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                crossFadeValue = p1 + 1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Toast.makeText(this@MainActivity, "CrossFade = ${crossFadeValue + 1}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}