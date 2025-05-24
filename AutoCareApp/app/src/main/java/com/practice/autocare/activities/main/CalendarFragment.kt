package com.practice.autocare.activities.main

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.google.android.material.snackbar.Snackbar
import com.practice.autocare.R
import com.practice.autocare.activities.adapter.HistoryAdapter
import com.practice.autocare.api.RetrofitClient.Companion.api
import com.practice.autocare.databinding.FragmentCalendarBinding
import com.practice.autocare.models.service.CarResponse
import com.practice.autocare.models.service.ServiceEventResponse
import com.practice.autocare.models.service.ServiceEventResponseComp
import com.practice.autocare.models.service.ServiceRequest
import com.practice.autocare.models.service.UpdateServiceStatusRequest
import com.practice.autocare.util.Constants.Companion.getUserEmail
import com.practice.autocare.util.EventsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
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

    private var rememberList = mutableListOf<ServiceEventResponseComp>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = getUserEmail(requireContext())


        //инициализация с кастомным адаптером
        eventsAdapter = EventsAdapter(eventsList) { position, event ->
            clearRecycleView(position)
        }

        binding.eventsRecyclerView.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем разделители
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        // Настройка свайпа для удаления
        if (email != null) {
            setupSwipeToDelete(email)
        }
        updateEmptyState()

        Log.d("TAG_Calendar", eventsList.toString())

        // Установка текущей даты
        selectedDate = binding.calendarView.date
        loadEventsForDate(selectedDate, email)

        // Обработчик выбора даты
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDate = calendar.timeInMillis
            loadEventsForDate(selectedDate, email)
        }

        // Обработчик кнопки добавления события
        binding.addEventButton.setOnClickListener {
            if (email != null) {
                showAddEventDialog(email)
                Log.d("TAG_Calendar", "addEvent : $rememberList")
            }
        }

        // обработчик кнопки завершения дня
        binding.finishDayButton.setOnClickListener {
            if (email != null) {
                showFinishDayDialog(email)
            }
        }
    }

    private fun loadEventsForDate(date: Long, email: String? = null) {
        if (email == null) {
            updateEmptyState()
            return
        }
        rememberList.clear()

        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date))


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getServiceEvents(
                    email = email,
                    dueDate = dateString,
                    isCompleted = false
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        eventsList.clear()
                        response.body()?.forEach { event ->
                            eventsList.add(event.service_type)
                            rememberList.add(event)
                            Log.d("TAG_Calendar", "$event")
                        }
                        eventsAdapter.notifyDataSetChanged()
                        updateEmptyState()
                    } else {
                        Toast.makeText(
                            context,
                            "Ошибка загрузки событий",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка сети: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showAddEventDialog(email: String) {

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

                    CoroutineScope(Dispatchers.IO).launch {
                        val car = getCar(email)

                        activity?.runOnUiThread {
                            if (car != null) {

                                services.forEach { service ->
                                    addEvent(
                                        email,
                                        ServiceRequest(
                                            car_id = car.car_id,
                                            service_type = service,
                                            due_date = dateString,
                                            due_mileage = car.mileage.toDouble()
                                        )
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Не удалось получить данные автомобиля",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
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
            requestFocus()// Автоматически запрашиваем фокус
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

        // Показываем клавиатуру для нового поля
        editText.post {
            editText.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
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

    private fun setupSwipeToDelete(email: String) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deletedEvent = eventsList[position]
                deleteEvent(position, deletedEvent, email)
            }
        }).attachToRecyclerView(binding.eventsRecyclerView)
    }

    private fun deleteEvent(position: Int, event: String, email: String) {
        // 1. Находим событие в rememberList по service_type
        val eventToDelete = rememberList.find { it.service_type == event }
        if (eventToDelete == null) {
            Log.e("TAG_Calendar", "Событие для удаления не найдено: $event")
            Toast.makeText(context, "Ошибка: событие не найдено", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Оптимистичное удаление из UI
        eventsList.removeAt(position)
        rememberList.remove(eventToDelete)
        eventsAdapter.notifyItemRemoved(position)
        updateEmptyState()

        Log.d("TAG_Calendar", "Удаляем событие: ${eventToDelete.event_id}")

        // 3. Удаление через API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.deleteServiceEvents(email, eventToDelete.event_id)

                withContext(Dispatchers.Main) {
                    if (!response.isSuccessful) {
                        // В случае ошибки API показываем сообщение
                        val errorMsg = response.errorBody()?.string() ?: "Неизвестная ошибка"
                        Log.e("TAG_Calendar", "Ошибка удаления: $errorMsg")
                        Toast.makeText(
                            context,
                            "Не удалось удалить услугу",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("TAG_Calendar", "Услуга удалена: ${response.body()?.message}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Обработка сетевых ошибок
                    val errorMsg = when (e) {
                        is SocketTimeoutException -> "Таймаут соединения"
                        is ConnectException -> "Нет подключения к серверу"
                        else -> e.localizedMessage ?: "Неизвестная ошибка"
                    }
                    Log.e("TAG_Calendar", "Сетевая ошибка: $errorMsg")
                    Toast.makeText(
                        context,
                        "Ошибка сети при удалении",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clearRecycleView(position: Int) {
        eventsList.removeAt(position)
        eventsAdapter.notifyItemRemoved(position)
        updateEmptyState()

        Log.d("TAG_Calendar", "clear recycle view")
    }

    private fun addEvent(email: String, serviceRequest: ServiceRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Преобразуем дату в формат "YYYY-MM-DD"
                val formattedDate = convertDateFormat(serviceRequest.due_date)
                val formattedRequest = serviceRequest.copy(due_date = formattedDate)

                val response = api.addServiceEvent(email, formattedRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            eventsList.add(it.service_type)
                            rememberList.add(ServiceEventResponseComp(
                                it.car_id,
                                it.service_type,
                                it.due_date,
                                it.due_mileage,
                                it.event_id,
                                false
                            ))
                            eventsAdapter.notifyItemInserted(eventsList.size - 1)
                            updateEmptyState()
//                            Toast.makeText(context, "Услуга добавлена", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        handleAddError(response)
                    }

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    handleNetworkError(e)
                }
            }
        }
    }

    private fun convertDateFormat(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(inputFormat.parse(inputDate)!!)
        } catch (e: Exception) {
            Log.e("DateConversion", "Error converting date", e)
            "" // или вернуть текущую дату в нужном формате
        }
    }

    private fun handleAddError(response: Response<ServiceEventResponse>) {
        val errorMessage = try {
            response.errorBody()?.string() ?: "Ошибка при добавлении услуги"
        } catch (e: Exception) {
            "Ошибка при обработке ошибки сервера"
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        Log.e("API_AddService", errorMessage)
    }

    private fun handleNetworkError(e: Exception) {
        val message = when (e) {
            is ConnectException -> "Нет подключения к серверу"
            is SocketTimeoutException -> "Таймаут соединения"
            else -> "Ошибка: ${e.localizedMessage}"
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun showFinishDayDialog(email: String) {
        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(selectedDate))

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_finish_day, null)
        val currentMileageView = dialogView.findViewById<TextView>(R.id.current_mileage_text)

        currentMileageView.text = "Загрузка пробега.."

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Завершить день $dateString?")
            setView(dialogView)

            setPositiveButton("Сохранить") { dialog, _ ->
//                moveServicesToHistory()
                if (rememberList.isNotEmpty()) {
                    completeAllServicesForDay(email)
                } else {
//                    Toast.makeText(context, "Нет услуг для завершения", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }

            // Загружаем актуальный пробег при показе диалога
            CoroutineScope(Dispatchers.IO).launch {
                val car = getCar(email)
                withContext(Dispatchers.Main) {
                    currentMileageView.text = if (car != null) {
                        "Текущий пробег: ${car.mileage} км"
                    } else {
                        "Не удалось загрузить пробег"
                    }
                }
            }

            show()
        }
    }

    private fun completeAllServicesForDay(
        email: String,
        reqTrue: UpdateServiceStatusRequest = UpdateServiceStatusRequest(is_completed = true)
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Обновляем все услуги в rememberList
                val deferredResults = rememberList.map { event ->
                    async {
                        try {
                            Log.d("TAG_Calendar", "update: $event")

                            val curEventId = event.event_id

                            val response = api.updateServiceEventStatus(
                                email = email,
                                eventId = curEventId,
                                updateRequest = reqTrue
                            )
                            response.isSuccessful
                        } catch (e: Exception) {
                            false
                        }
                    }
                }

                val results = deferredResults.awaitAll()

                withContext(Dispatchers.Main) {
                    val successCount = results.count { it }
                    if (successCount == rememberList.size) {
                        // Все услуги успешно обновлены
                        eventsList.clear()
                        rememberList.clear()
                        eventsAdapter.notifyDataSetChanged()
                        updateEmptyState()
//                        Toast.makeText(
//                            context,
//                            "День завершен: $successCount услуг обновлено",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    } else {
//                        // Часть услуг не обновилась
//                        Toast.makeText(
//                            context,
//                            "Ошибка: обновлено $successCount из ${rememberList.size} услуг",
//                            Toast.LENGTH_LONG
//                        ).show()

                        // Перезагружаем список, чтобы показать актуальное состояние
                        loadEventsForDate(selectedDate, email)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка сети: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateEmptyState() {
        binding.emptyListText.visibility = if (eventsList.isEmpty()) View.VISIBLE else View.GONE
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

    private suspend fun getCar(email: String): CarResponse? {
        return try {
            val response = api.getCars(email)
            if (response.isSuccessful) {
                val cars = response.body()
                cars?.firstOrNull() // Возвращаем первую машину или null
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to get cars"
                Log.e("TAG_Car", errorMessage)
                null
            }
        } catch (e: Exception) {
            Log.e("TAG_Car", "Error getting car", e)
            null
        }
    }
}