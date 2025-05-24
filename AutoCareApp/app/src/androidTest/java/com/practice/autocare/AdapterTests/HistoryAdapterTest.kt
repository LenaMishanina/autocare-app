package com.practice.autocare.AdapterTests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.practice.autocare.R
import com.practice.autocare.activities.adapter.HistoryAdapter
import com.practice.autocare.models.service.ServiceEventResponse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryAdapterTest {

    private lateinit var adapter: HistoryAdapter
    private val testServices = arrayListOf(
        ServiceEventResponse(
            car_id = 1,
            service_type = "Замена масла",
            due_date = "2025-05-22",
            due_mileage = 5000.0,
            event_id = 1
        ),
        ServiceEventResponse(
            car_id = 1,
            service_type = "Вращение шины",
            due_date = "2025-05-15",
            due_mileage = 10000.0,
            event_id = 2
        )
    )

    @Before
    fun setUp() {
        adapter = HistoryAdapter(testServices)
    }

    @Test
    fun getItemCountReturnsCorrectSize() {
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun onBindViewHolderBindsDataCorrectly() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.service_layout, null) as ViewGroup
        val holder = HistoryAdapter.ViewHolderService(view)

        adapter.onBindViewHolder(holder, 0)

        // Verify data was set correctly
        val serviceType = view.findViewById<TextView>(R.id.tvServiceType)
        val dueDate = view.findViewById<TextView>(R.id.tvDueDate)
        val dueMileage = view.findViewById<TextView>(R.id.tvDueMileage)

        assertEquals("Замена масла", serviceType.text)
        assertEquals("2025-05-25", dueDate.text)
        assertEquals("5000.0", dueMileage.text)
    }

    @Test
    fun filterWorksCorrectly() {
        assertEquals(2, adapter.itemCount)

        adapter.filter("")
        assertEquals(2, adapter.itemCount)

        adapter.filter("масла")
        assertEquals(1, adapter.itemCount)
        assertEquals("Замена масла", adapter.filteredServices[0].service_type)

        adapter.filter("ТО")
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun sortWorksCorrectly() {
        // Test DATE_ASC sort
        adapter.sort(HistoryAdapter.SortType.DATE_ASC)
        assertEquals("2025-05-01", adapter.filteredServices[0].due_date)
        assertEquals("2025-06-15", adapter.filteredServices[1].due_date)

        // Test DATE_DESC sort
        adapter.sort(HistoryAdapter.SortType.DATE_DESC)
        assertEquals("2025-06-15", adapter.filteredServices[0].due_date)
        assertEquals("2025-05-01", adapter.filteredServices[1].due_date)

        // Test MILEAGE_ASC sort
        adapter.sort(HistoryAdapter.SortType.MILEAGE_ASC)
        assertEquals(5000.0, adapter.filteredServices[0].due_mileage, 0.0)
        assertEquals(10000.0, adapter.filteredServices[1].due_mileage, 0.0)

        // Test MILEAGE_DESC sort
        adapter.sort(HistoryAdapter.SortType.MILEAGE_DESC)
        assertEquals(10000.0, adapter.filteredServices[0].due_mileage, 0.0)
        assertEquals(5000.0, adapter.filteredServices[1].due_mileage, 0.0)
    }
}