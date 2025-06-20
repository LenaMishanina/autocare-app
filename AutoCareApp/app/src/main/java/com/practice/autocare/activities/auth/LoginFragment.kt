package com.practice.autocare.activities.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.practice.autocare.R
import com.practice.autocare.activities.main.MainActivity
import com.practice.autocare.api.RetrofitClient.Companion.api
import com.practice.autocare.databinding.FragmentLoginBinding
import com.practice.autocare.models.auth.LoginRequest
import com.practice.autocare.util.Constants
import com.practice.autocare.util.Constants.Companion.saveUserEmail
import com.practice.autocare.util.Constants.Companion.setupErrorClearingOnTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        btnLogin.setOnClickListener {
            if (emailEditText.text.isNullOrEmpty()) {
                emailContainer.error = getString(R.string.required)
            } else if (pwdEditText.text.isNullOrEmpty()) {
                pwdContainer.error = getString(R.string.required)
            } else {
                login(
                    LoginRequest(
                        email = emailEditText.text.toString(),
                        password = pwdEditText.text.toString(),
                    )
                )
            }
        }

        linkToRegUser.setOnClickListener {
            Constants.MAIN.navController.navigate(R.id.action_loginFragment_to_registerUserFragment)
        }

        linkForgotPwd.setOnClickListener {
            Constants.MAIN.navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        setupErrorClearingOnTextChanged(emailEditText, emailContainer)
        setupErrorClearingOnTextChanged(pwdEditText, pwdContainer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }

    private fun login(loginRequest: LoginRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.login(loginRequest)

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        // Используем контекст фрагмента
                        saveUserEmail(requireContext(), loginRequest.email)

                        startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        requireActivity().finish()
                    } else {
                        // Обработка HTTP ошибок (400, 500 и т.д.)
                        val errorMessage = response.errorBody()?.string() ?: "Login failed"
                        Log.e("TAG_Reg", errorMessage)
                        Toast.makeText(context, getString(R.string.http_error_reg_user), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Constants.handleNetworkError(requireContext(), e)
                }
            }
        }
    }

}