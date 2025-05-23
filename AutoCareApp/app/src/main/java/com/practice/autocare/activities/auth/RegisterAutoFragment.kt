package com.practice.autocare.activities.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.practice.autocare.R
import com.practice.autocare.databinding.FragmentRegisterAutoBinding
import com.practice.autocare.util.Constants

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
            if (brandEditText.text.isNullOrEmpty()) {
                brandContainer.error = getString(R.string.required)
            } else if (modelEditText.text.isNullOrEmpty()) {
                modelContainer.error = getString(R.string.required)
            } else if (mileageEditText.text.isNullOrEmpty()) {
                mileageContainer.error = getString(R.string.required)
            } else {
                Toast.makeText(requireContext(), "OK", Toast.LENGTH_SHORT).show()
            }
        }

        linkToCalendar.setOnClickListener {
//            Constants.MAIN.navController.navigate(R.id.action_registerAutoFragment_to_CalendarFragment)
            Toast.makeText(requireContext(), "переход на календарь без регистрации авто", Toast.LENGTH_SHORT).show()
        }

        Constants.setupErrorClearingOnTextChanged(brandEditText, brandContainer)
        Constants.setupErrorClearingOnTextChanged(modelEditText, modelContainer)
        Constants.setupErrorClearingOnTextChanged(mileageEditText, mileageContainer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterAutoFragment()
    }

}