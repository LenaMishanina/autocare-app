package com.practice.autocare.activities.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.practice.autocare.R
import com.practice.autocare.activities.main.MainActivity
import com.practice.autocare.api.RetrofitClient.Companion.api
import com.practice.autocare.databinding.FragmentRecoverPasswordBinding
import com.practice.autocare.models.auth.ResetPasswordRequest
import com.practice.autocare.util.Constants
import com.practice.autocare.util.Constants.Companion.MAIN
import com.practice.autocare.util.Constants.Companion.saveUserEmail
import com.practice.autocare.util.Constants.Companion.setupErrorClearingOnTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecoverPasswordFragment : Fragment() {
    private var _binding: FragmentRecoverPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        val resetToken = arguments?.getString("resetToken") ?: ""

        tokenEditText.setText(resetToken)
        tokenEditText.isEnabled = false

        btnChangePwd.setOnClickListener {
            if (tokenEditText.text.isNullOrEmpty()) {
                tokenContainer.error = getString(R.string.required)
            } else if (pwdNewEditText.text.isNullOrEmpty()) {
                pwdNewContainer.error = getString(R.string.required)
            } else {
                changePwd(
                    ResetPasswordRequest(
                        token = resetToken,
                        new_password = pwdNewEditText.text.toString(),
                    )
                )
            }
        }

        setupErrorClearingOnTextChanged(tokenEditText, tokenContainer)
        setupErrorClearingOnTextChanged(pwdNewEditText, pwdNewContainer)


    }

    private fun changePwd(pwdRequest: ResetPasswordRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.resetPwd(pwdRequest)

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        MAIN.navController.navigate(R.id.action_recoverPasswordFragment_to_loginFragment)
                    } else {
                        // Обработка HTTP ошибок (400, 500 и т.д.)
                        val errorMessage = response.errorBody()?.string() ?: "reset pwd failed"
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
            RecoverPasswordFragment()
    }
}