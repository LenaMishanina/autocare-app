package com.practice.autocare.activities.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practice.autocare.R
import com.practice.autocare.api.AuthApi
import com.practice.autocare.databinding.ActivityRegisterUserBinding
import com.practice.autocare.models.auth.RegisterUserRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterUserActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterUserBinding
    lateinit var authApi: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRetrofit()

        binding.apply {
            btnAccept.setOnClickListener {
                registerUser(
                    RegisterUserRequest(
                        edLogin.text.toString(),
                        edEmail.text.toString(),
                        edPassword.text.toString(),
                    )
                )
            }
        }

    }

    private fun initRetrofit() {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.84:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        authApi = retrofit.create(AuthApi::class.java)
    }

    private fun registerUser(userRequest: RegisterUserRequest) = with(binding) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = authApi.registerUser(userRequest)

            runOnUiThread {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterUserActivity, "Success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegisterUserActivity, "Fail", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}