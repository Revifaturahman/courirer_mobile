//package com.example.courier_mobile.view.login
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import android.widget.Toast
//import com.example.courier_mobile.databinding.ActivityLoginBinding
//import com.example.courier_mobile.utils.Result
//import com.example.courier_mobile.view.MainActivity
//import com.example.courier_mobile.viewmodel.LoginViewModel
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityLoginBinding
//    private val viewModel: LoginViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.loginBtn.setOnClickListener {
//            val username = binding.usernameEdit.text.toString()
//            val password = binding.passwordEdit.text.toString()
//
//            if (username.isBlank() || password.isBlank()) {
//                Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            viewModel.login(username, password).observe(this) { result ->
//                when (result) {
//                    is Result.Loading -> {
//                        binding.loadingBar.visibility = View.VISIBLE
//                    }
//
//                    is Result.Success -> {
//                        binding.loadingBar.visibility = View.GONE
//                        val token = result.data?.token
//
//                        // ✅ Simpan token ke SharedPreferences
//                        val sharedPref = getSharedPreferences("courier_prefs", MODE_PRIVATE)
//                        sharedPref.edit().putString("token", token).apply()
//
//                        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
//                        Log.d("LoginActivity", "Token disimpan: $token")
//
//                        // ✅ Arahkan ke MainActivity
//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
//                        finish() // supaya tombol back tidak kembali ke login
//                    }
//
//                    is Result.Error -> {
//                        binding.loadingBar.visibility = View.GONE
//                        Toast.makeText(this, "Login gagal: ${result.message}", Toast.LENGTH_SHORT).show()
//                        Log.e("LoginActivity", "Login error: ${result.message}")
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    }
//}
