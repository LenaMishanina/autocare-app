package com.practice.autocare.activities.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.practice.autocare.R
import com.practice.autocare.api.RetrofitClient.Companion.api
import com.practice.autocare.databinding.FragmentRegisterUserBinding
import com.practice.autocare.models.auth.RegisterUserRequest
import com.practice.autocare.util.Constants.Companion.setupErrorClearingOnTextChanged
import com.practice.autocare.util.Constants.Companion.MAIN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        btnRegUser.setOnClickListener {
            if (loginEditText.text.isNullOrEmpty()) {
                loginContainer.error = getString(R.string.required)
            } else if (emailEditText.text.isNullOrEmpty()) {
                emailContainer.error = getString(R.string.required)
            } else if (pwdEditText.text.isNullOrEmpty()) {
                pwdContainer.error = getString(R.string.required)
            } else {
                registerUser(
                    RegisterUserRequest(
                        loginEditText.text.toString(),
                        emailEditText.text.toString(),
                        pwdEditText.text.toString(),
                    )
                )

            }
        }

        linkToLogin.setOnClickListener {
            MAIN.navController.navigate(R.id.action_registerUserFragment_to_loginFragment)
        }

        setupErrorClearingOnTextChanged(loginEditText, loginContainer)
        setupErrorClearingOnTextChanged(emailEditText, emailContainer)
        setupErrorClearingOnTextChanged(pwdEditText, pwdContainer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterUserFragment()
    }

    private fun registerUser(userRequest: RegisterUserRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.registerUser(userRequest)

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        MAIN.navController.navigate(R.id.action_registerUserFragment_to_registerAutoFragment)
                    } else {
                        // Обработка HTTP ошибок (400, 500 и т.д.)
                        val errorMessage = response.errorBody()?.string() ?: "Registration failed"
                        Log.e("TAG_Reg", errorMessage)
                        Toast.makeText(context, getString(R.string.http_error_reg_user), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    when (e) {
                        is java.net.ConnectException -> {
                            Toast.makeText(
                                context,
                                "Cannot connect to server. Please check your internet connection",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is java.net.SocketTimeoutException -> {
                            Toast.makeText(
                                context,
                                "Connection timeout. Please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                context,
                                "An error occurred: ${e.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

//    private fun registerUser(userRequest: RegisterUserRequest) = with(binding) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = api.registerUser(userRequest)
//
//            activity?.runOnUiThread {
//                if (response.isSuccessful) {
//                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//                    MAIN.navController.navigate(R.id.action_registerUserFragment_to_registerAutoFragment)
//                } else {
//                    Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

}