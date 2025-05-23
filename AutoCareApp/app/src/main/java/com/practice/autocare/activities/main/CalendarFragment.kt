package com.practice.autocare.activities.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.autocare.R
import com.practice.autocare.databinding.FragmentCalendarBinding
import com.practice.autocare.util.EventsAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: Long = 0
    private val eventsList = mutableListOf<String>()
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //инициализация с кастомным адаптером
        eventsAdapter = EventsAdapter(eventsList) { position, event ->
            deleteEvent(position, event)
        }
        binding.eventsRecyclerView.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем разделители
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        // Настройка свайпа для удаления
        setupSwipeToDelete()

        // Текст при пустом списке
        binding.emptyListText.visibility = if (eventsList.isEmpty()) View.VISIBLE else View.GONE
        //binding.eventsListView.visibility = if (eventsList.isEmpty()) View.GONE else View.VISIBLE
        binding.eventsRecyclerView.visibility = if (eventsList.isEmpty()) View.GONE else View.VISIBLE

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
        // обработчик кнопки завершения дня
        binding.finishDayButton.setOnClickListener { showFinishDayDialog() }
    }

    private fun showAddEventDialog() {
        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(selectedDate))
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_event, null)

        val servicesContainer = dialogView.findViewById<LinearLayout>(R.id.services_container)
        val addButton = dialogView.findViewById<Button>(R.id.add_service_button)

        addServiceField(servicesContainer, "")  // Начальное поле
        // Кнопка добавления нового поля
        addButton.setOnClickListener {
            addServiceField(servicesContainer, "")
        }
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Добавление услуг на $dateString")
            setView(dialogView)

            setPositiveButton("Сохранить") { dialog, _ ->
                val services = getServicesFromFields(servicesContainer)
                if (services.isNotEmpty()) {
                    services.forEach { service ->
                        addEvent(service)
                    }
                }
                dialog.dismiss()
            }
            setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
            show()
        }
    }

    private fun addServiceField(container: LinearLayout, initialValue: String) {
        val context = container.context
        val inputLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16.dpToPx()
            }
        }
        val editText = EditText(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            setText(initialValue)
            hint = "Введите услугу"
        }
        // Удалить услугу при добавлении
        val removeButton = ImageButton(context).apply {
            setImageResource(R.drawable.ic_delete)
            background = null
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                container.removeView(inputLayout)
            }
        }
        inputLayout.addView(editText)
        inputLayout.addView(removeButton)
        container.addView(inputLayout) //Поле ввода услуги
    }

    private fun getServicesFromFields(container: LinearLayout): List<String> {
        val services = mutableListOf<String>()
        for (i in 0 until container.childCount) {
            val inputLayout = container.getChildAt(i) as? LinearLayout ?: continue
            val editText = inputLayout.getChildAt(0) as? EditText ?: continue
            val service = editText.text.toString().trim()
            if (service.isNotEmpty()) {
                services.add(service)
            }
        }
        return services
    }

    private fun Int.dpToPx(): Int {
        return (this * requireContext().resources.displayMetrics.density).toInt()
    }

    private fun setupSwipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deletedEvent = eventsList[position]
                deleteEvent(position, deletedEvent)
            }
        }).attachToRecyclerView(binding.eventsRecyclerView)
    }

    private fun deleteEvent(position: Int, event: String) {
        eventsList.removeAt(position)
        eventsAdapter.notifyItemRemoved(position)
        updateEmptyState()
        // TODO: добавить удаление из бд
    }

    private fun addEvent(eventText: String) {
        eventsList.add(eventText)
        eventsAdapter.notifyDataSetChanged()
        updateEmptyState()
        // TODO: Здесь нужно дописать сохранение события в базу данных
    }

    private fun loadEventsForDate(date: Long) {
        eventsList.clear()
        // TODO: Здесь можно загружать события из базы данных по идеи, если это добавить, то уже должно работать при переключении (есть на это обработка)
        eventsAdapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun showFinishDayDialog() {
        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(selectedDate))

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_finish_day, null)
        val currentMileageView = dialogView.findViewById<TextView>(R.id.current_mileage_text)

        val currentMileage = "10000" // TODO: подтягивать пробег для пользователя
        currentMileageView.text = "Текущий пробег: $currentMileage км"

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Завершить день $dateString?")
            setView(dialogView)

            setPositiveButton("Сохранить") { dialog, _ ->
                moveServicesToHistory()
                dialog.dismiss()
            }
            setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }
            show()
        }
    }
    private fun moveServicesToHistory() {
        eventsList.clear()
        eventsAdapter.notifyDataSetChanged()
        updateEmptyState()
        // TODO: добавить запрос в бд со сменой галки (для добавления в историю)
        Toast.makeText(requireContext(), "День завершен, услуги перенесены в историю", Toast.LENGTH_SHORT).show()
    }

    private fun updateEmptyState() {
        binding.emptyListText.visibility = if (eventsList.isEmpty()) View.VISIBLE else View.GONE
        //binding.eventsListView.visibility = if (eventsList.isEmpty()) View.GONE else View.VISIBLE
        binding.eventsRecyclerView.visibility = if (eventsList.isEmpty()) View.GONE else View.VISIBLE
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