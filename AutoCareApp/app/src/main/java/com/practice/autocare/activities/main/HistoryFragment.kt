package com.practice.autocare.activities.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practice.autocare.R
import com.practice.autocare.activities.adapter.HistoryAdapter
import com.practice.autocare.api.RetrofitClient.Companion.api
import com.practice.autocare.databinding.FragmentHistoryBinding
import com.practice.autocare.databinding.FragmentLoginBinding
import com.practice.autocare.models.service.ServiceEventResponse
import com.practice.autocare.models.service.ServiceEventResponseComp
import com.practice.autocare.util.Constants
import com.practice.autocare.util.Constants.Companion.getUserEmail
import com.practice.autocare.util.Constants.Companion.handleNetworkError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HistoryAdapter
    private var services = ArrayList<ServiceEventResponseComp>()

    private var currentSortType = HistoryAdapter.SortType.DATE_DESC // Сортировка по умолчанию

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.apply { // для того чтобы можно было в любое место кликнуть и начать искать
            // Убираем стандартные отступы
            setPadding(0, 0, 0, 0)
            // Находим текстовое поле и делаем его растянутым
            findViewById<EditText>(androidx.appcompat.R.id.search_src_text).apply {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            // Обработчик клика по всей области
            setOnClickListener {
                isIconified = false // Разворачиваем при любом клике
            }
        }

        binding.rViewHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        val email = getUserEmail(requireContext()) // Используем контекст фрагмента
        if (email != null) {
            getServices(email)
        } else {
            showEmptyState(true)
        }

        setupSearchView()
        // Обработчик клика по кнопке сортировки
        binding.sortButton.setOnClickListener {
            showSortDialog()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                // Проверяем, есть ли результаты после фильтрации
                val isEmpty = adapter.itemCount == 0
                showEmptyState(isEmpty && !newText.isNullOrEmpty())
                return true
            }
        })
    }

    private fun showSortDialog() {
        val items = arrayOf(
            "По дате (новые сначала)",
            "По дате (старые сначала)",
            "По пробегу (по возрастанию)",
            "По пробегу (по убыванию)"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Сортировка")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        currentSortType = HistoryAdapter.SortType.DATE_DESC
                        adapter.sort(currentSortType)
                    }
                    1 -> {
                        currentSortType = HistoryAdapter.SortType.DATE_ASC
                        adapter.sort(currentSortType)
                    }
                    2 -> {
                        currentSortType = HistoryAdapter.SortType.MILEAGE_ASC
                        adapter.sort(currentSortType)
                    }
                    3 -> {
                        currentSortType = HistoryAdapter.SortType.MILEAGE_DESC
                        adapter.sort(currentSortType)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun getServices(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getServiceEvents(
                    email = email,
                    isCompleted = true
                )

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        response.body()?.let { completedServices ->
                            services = completedServices
                            adapter = HistoryAdapter(services)
                            binding.rViewHistory.adapter = adapter
                            adapter.sort(currentSortType)

                            showEmptyState(services.isEmpty())
                        } ?: run {
                            showEmptyState(true)
                            Log.e("TAG_History", "Response body is null")
                        }
                    } else {
                        // Обработка HTTP ошибок (400, 500 и т.д.)
                        val errorMessage = response.errorBody()?.string() ?: "Get services failed"
                        Log.e("TAG_History", errorMessage)
                        Toast.makeText(context, getString(R.string.http_error_service), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    handleNetworkError(requireContext(), e)
                    showEmptyState(true)
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