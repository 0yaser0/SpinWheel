package com.example.chooooseone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chooooseone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val validateButton = binding.validateBtn
        val inputText = binding.inputText
        val spinWheelView = binding.spinWheel

        validateButton.setOnClickListener {
            val input = inputText.text.toString().trim()
            if (input.isNotEmpty()) {
                spinWheelView.addSector(input)
                inputText.text?.clear()
            }
        }

        binding.spinBtn.setOnClickListener {
            spinWheelView.spin()
        }

    }
}

