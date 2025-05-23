package com.practice.autocare.activities.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.autocare.R
import com.practice.autocare.activities.adapter.HistoryAdapter
import com.practice.autocare.api.RetrofitClient.Companion.api
import com.practice.autocare.databinding.FragmentHistoryBinding
import com.practice.autocare.databinding.FragmentLoginBinding
import com.practice.autocare.models.service.ServiceEventResponse
import com.practice.autocare.util.Constants
import com.practice.autocare.util.Constants.Companion.getUserEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HistoryAdapter
    private var services = ArrayList<ServiceEventResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rViewHistory.layoutManager = LinearLayoutManager(view.context)
        binding.rViewHistory.setHasFixedSize(true)

        // Инициализация RecyclerView
//        setupRecyclerView()

        binding.rViewHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
//            adapter = this@HistoryFragment.adapter
            setHasFixedSize(true)
        }

        val email = getUserEmail(requireContext()) // Используем контекст фрагмента
        if (email != null) {
            getServices(email)
        } else {
            showEmptyState(true)
        }

    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(services)
        binding.rViewHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun getServices(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getServiceEvents(email)

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        services = response.body()!!

                        if (services.isEmpty()) {
                            showEmptyState(true)
                        } else {
                            showEmptyState(false)
                            // отображение в recycleview
                            adapter = HistoryAdapter(services)
                            binding.rViewHistory.adapter = this@HistoryFragment.adapter

                        }

                        Log.d("TAG_History", "getServices ${services.toString()}")
//                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//                     TODO   MAIN.navController.navigate(R.id.action_registerAutoFragment_to_CalendarFragment)
                    } else {
                        // Обработка HTTP ошибок (400, 500 и т.д.)
                        val errorMessage = response.errorBody()?.string() ?: "Get services failed"
                        Log.e("TAG_History", errorMessage)
                        Toast.makeText(context, getString(R.string.http_error_service), Toast.LENGTH_LONG).show()
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
    private fun showEmptyState(show: Boolean) {
        binding.emptyText.visibility = if (show) View.VISIBLE else View.GONE
        binding.rViewHistory.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}