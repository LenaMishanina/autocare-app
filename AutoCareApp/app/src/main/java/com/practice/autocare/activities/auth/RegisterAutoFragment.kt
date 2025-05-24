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
import com.practice.autocare.databinding.FragmentRegisterAutoBinding
import com.practice.autocare.models.auth.RegisterAutoRequest
import com.practice.autocare.util.Constants.Companion.getUserEmail
import com.practice.autocare.util.Constants.Companion.handleNetworkError
import com.practice.autocare.util.Constants.Companion.setupErrorClearingOnTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterAutoFragment : Fragment() {


    private var _binding: FragmentRegisterAutoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterAutoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)


        btnRegAuto.setOnClickListener {
            getUserEmail(requireContext())?.let { Log.d("TAG_AUTO", it) }
            if (brandEditText.text.isNullOrEmpty()) {
                brandContainer.error = getString(R.string.required)
            } else if (modelEditText.text.isNullOrEmpty()) {
                modelContainer.error = getString(R.string.required)
            } else if (yearEditText.text.isNullOrEmpty()) {
                yearContainer.error = getString(R.string.required)
            } else if (mileageEditText.text.isNullOrEmpty()) {
                mileageContainer.error = getString(R.string.required)
            } else {
                val email = getUserEmail(requireContext())
                if (email == null) {
                    Toast.makeText(context, "User email not found", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                registerAuto(
                    email,
                    RegisterAutoRequest(
                        brand = brandEditText.text.toString(),
                        model = modelEditText.text.toString(),
                        year = yearEditText.text.toString().toShort(),
                        mileage = mileageEditText.text.toString().toInt(),
                    )
                )
            }
        }

        linkToCalendar.setOnClickListener {
            startActivity(Intent(requireContext(), com.practice.autocare.activities.main.MainActivity::class.java).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            requireActivity().finish()
        }

        setupErrorClearingOnTextChanged(brandEditText, brandContainer)
        setupErrorClearingOnTextChanged(modelEditText, modelContainer)
        setupErrorClearingOnTextChanged(yearEditText, yearContainer)
        setupErrorClearingOnTextChanged(mileageEditText, mileageContainer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterAutoFragment()
    }

    private fun registerAuto(email: String, autoRequest: RegisterAutoRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.registerAuto(email, autoRequest)

                activity?.runOnUiThread {
                    if (response.isSuccessful) {

                        startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        requireActivity().finish()

                    } else {
                        // Обработка HTTP ошибок (400, 500 и т.д.)
                        val errorMessage = response.errorBody()?.string() ?: "Registration failed"
                        Log.e("TAG_Reg", errorMessage)
                        Toast.makeText(context, getString(R.string.http_error_reg_user), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    handleNetworkError(requireContext(), e)
                }
            }
        }
    }

}