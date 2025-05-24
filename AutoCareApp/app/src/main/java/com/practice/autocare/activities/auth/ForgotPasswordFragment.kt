package com.practice.autocare.activities.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.practice.autocare.R
import com.practice.autocare.activities.main.MainActivity
import com.practice.autocare.api.RetrofitClient.Companion.api
import com.practice.autocare.databinding.FragmentForgotPasswordBinding
import com.practice.autocare.databinding.FragmentLoginBinding
import com.practice.autocare.models.auth.ForgotPasswordRequest
import com.practice.autocare.util.Constants
import com.practice.autocare.util.Constants.Companion.MAIN
import com.practice.autocare.util.Constants.Companion.saveUserEmail
import com.practice.autocare.util.Constants.Companion.setupErrorClearingOnTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        btnGetToken.setOnClickListener {
            if (emailEditText.text.isNullOrEmpty()) {
                emailContainer.error = getString(R.string.required)
            } else {
                getToken(
                    ForgotPasswordRequest(
                        email = emailEditText.text.toString()
                    )
                )
            }
        }

        setupErrorClearingOnTextChanged(emailEditText, emailContainer)
    }

    private fun getToken(emailRequest: ForgotPasswordRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.forgotPwd(emailRequest)

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        val resetToken = response.body()!!.reset_token
                        val bundle = bundleOf("resetToken" to resetToken)
                        MAIN.navController.navigate(
                            R.id.action_forgotPasswordFragment_to_recoverPasswordFragment,
                            bundle
                        )
                    } else {
                        // Обработка HTTP ошибок (400, 500 и т.д.)
                        val errorMessage = response.errorBody()?.string() ?: "Forgot pwd failed"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            ForgotPasswordFragment()
    }
}