package com.example.arkady.touchy.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.arkady.touchy.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
    }
}
