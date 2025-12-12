package com.example.courier_mobile.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.courier_mobile.R
import com.example.courier_mobile.view.SettingFragment
import com.example.courier_mobile.view.TaskFragment
import com.example.courier_mobile.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(TaskFragment())

        binding.navMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.task ->loadFragment(TaskFragment())
                R.id.setting ->loadFragment(SettingFragment())
            }
            true
        }

        if (intent.getBooleanExtra("GO_TO_TASK", false)) {
            // Pindah langsung ke TaskFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, TaskFragment())
                .commit()
        }

    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }

}