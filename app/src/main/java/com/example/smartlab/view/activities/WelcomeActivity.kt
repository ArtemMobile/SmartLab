package com.example.smartlab.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smartlab.R
import com.example.smartlab.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private val binding: ActivityWelcomeBinding by lazy{
        ActivityWelcomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}