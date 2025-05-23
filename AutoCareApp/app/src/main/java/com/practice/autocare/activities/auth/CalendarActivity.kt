package com.practice.autocare.activities.auth

import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.practice.autocare.R

class CalendarActivity : BaseActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var addEventButton: Button
    private lateinit var eventsListView: ListView

    private var selectedDate: Long = 0
    private val eventsList = mutableListOf<String>()
    private lateinit var eventsAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setupBottomNavigation(R.id.nav_calendar)

        calendarView = findViewById(R.id.calendarView)
        addEventButton = findViewById(R.id.addEventButton)
        eventsListView = findViewById(R.id.eventsListView)

        // Инициализация адаптера для списка событий
        eventsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eventsList)
        eventsListView.adapter = eventsAdapter

        // Установка текущей даты
        selectedDate = calendarView.date

        // Обработчик выбора даты
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDate = calendar.timeInMillis
            loadEventsForDate(selectedDate)
        }

        // Обработчик кнопки добавления события
        addEventButton.setOnClickListener { showAddEventDialog() }
    }

    private fun showAddEventDialog() {
        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(selectedDate))

        AlertDialog.Builder(this).apply {
            setTitle("Добавить запись на $dateString")

            val input = EditText(this@CalendarActivity).apply {
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
        // Здесь можно сохранять событие в базу данных
    }

    private fun loadEventsForDate(date: Long) {
        eventsList.clear()
        // Здесь можно загружать события из базы данных
        eventsAdapter.notifyDataSetChanged()
    }
}