package com.practice.autocare.activities.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.practice.autocare.R
import com.practice.autocare.databinding.FragmentLoginBinding
import com.practice.autocare.util.Constants
import com.practice.autocare.util.Constants.Companion.setupErrorClearingOnTextChanged

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
                Toast.makeText(requireContext(), "OK", Toast.LENGTH_SHORT).show()
            }
        }

        linkToRegUser.setOnClickListener {
            Constants.MAIN.navController.navigate(R.id.action_loginFragment_to_registerUserFragment)
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

}