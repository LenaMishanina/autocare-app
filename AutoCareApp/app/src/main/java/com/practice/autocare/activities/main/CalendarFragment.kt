package com.practice.autocare.activities.main

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import com.practice.autocare.R
import com.practice.autocare.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: Long = 0
    private val eventsList = mutableListOf<String>()
    private lateinit var eventsAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка рамки для ListView
        binding.eventsListView.apply {
            background = resources.getDrawable(R.drawable.list_view_border, null)
            divider = resources.getDrawable(R.drawable.list_divider, null)
            dividerHeight = 1
        }

        // Инициализация адаптера для списка событий
        eventsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eventsList)
        binding.eventsListView.adapter = eventsAdapter

        // Текст при пустом списке
        binding.emptyListText.visibility = if (eventsList.isEmpty()) View.VISIBLE else View.GONE
        binding.eventsListView.visibility = if (eventsList.isEmpty()) View.GONE else View.VISIBLE

        // Установка текущей даты
        selectedDate = binding.calendarView.date

        // Обработчик выбора даты
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDate = calendar.timeInMillis
            loadEventsForDate(selectedDate)
        }

        // Обработчик кнопки добавления события
        binding.addEventButton.setOnClickListener { showAddEventDialog() }
    }

    private fun showAddEventDialog() {
        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(selectedDate))

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Добавить запись на $dateString")

            val input = EditText(requireContext()).apply {
                inputType = InputType.TYPE_CLASS_TEXT
            }
            setView(input)

            setPositiveButton("Добавить") { dialog, _ ->
                val eventText = input.text.toString()
                if (eventText.isNotEmpty()) {
                    addEvent(eventText)
                }
                dialog.dismiss()
            }
            setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

            show()
        }
    }

    private fun addEvent(eventText: String) {
        eventsList.add(eventText)
        eventsAdapter.notifyDataSetChanged()
        updateEmptyState()
        // Здесь можно сохранять событие в базу данных
    }

    private fun loadEventsForDate(date: Long) {
        eventsList.clear()
        // Здесь можно загружать события из базы данных
        eventsAdapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        binding.emptyListText.visibility = if (eventsList.isEmpty()) View.VISIBLE else View.GONE
        binding.eventsListView.visibility = if (eventsList.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}