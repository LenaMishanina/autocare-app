package com.practice.autocare

import com.practice.autocare.activities.adapter.HistoryAdapter

import com.practice.autocare.models.service.ServiceEventResponseComp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HistoryAdapterTest {

    private lateinit var adapter: HistoryAdapter
    private val testServices = listOf(
        ServiceEventResponseComp(1, "Замена масла", "2023-01-15", 10000.0, 1, false),
        ServiceEventResponseComp(1, "Замена тормозных колодок", "2023-03-20", 20000.0, 2, false),
        ServiceEventResponseComp(1, "Замена воздушного фильтра", "2023-02-10", 15000.0, 3, false),
        ServiceEventResponseComp(1, "ЗАМЕНА ТОРМОЗНЫХ КОЛОДОК", "2023-04-01", 22000.0, 4, false),
        ServiceEventResponseComp(1, "замена воздушного фильтра", "2023-05-10", 18000.0, 5, false),
        ServiceEventResponseComp(1, "Замена масла", "2023-06-05", 25000.0, 6, false)
    )

    @Before
    fun setUp() {
        adapter = HistoryAdapter(ArrayList(testServices))
    }

    @Test
    fun testFilterServicesByType() {
        // Применение фильтра
        adapter.filter("тормоз")

        // Проверка результатов
        assertEquals(2, adapter.itemCount)
        assertEquals("Замена тормозных колодок", adapter.filteredServices[0].service_type)
        assertEquals("ЗАМЕНА ТОРМОЗНЫХ КОЛОДОК", adapter.filteredServices[1].service_type)
    }

    @Test
    fun testSortByDateDescending() {
        // Применение сортировки
        adapter.sort(HistoryAdapter.SortType.DATE_DESC)

        // Проверка результатов
        assertEquals("2023-06-05", adapter.filteredServices[0].due_date)
        assertEquals("2023-05-10", adapter.filteredServices[1].due_date)
        assertEquals("2023-04-01", adapter.filteredServices[2].due_date)
        assertEquals("2023-03-20", adapter.filteredServices[3].due_date)
        assertEquals("2023-02-10", adapter.filteredServices[4].due_date)
        assertEquals("2023-01-15", adapter.filteredServices[5].due_date)
    }

    @Test
    fun testSortByMileageAscending() {
        // Применение сортировки
        adapter.sort(HistoryAdapter.SortType.MILEAGE_ASC)

        // Проверка результатов
        assertEquals(10000.0, adapter.filteredServices[0].due_mileage, 0.0)
        assertEquals(15000.0, adapter.filteredServices[1].due_mileage, 0.0)
        assertEquals(18000.0, adapter.filteredServices[2].due_mileage, 0.0)
        assertEquals(20000.0, adapter.filteredServices[3].due_mileage, 0.0)
        assertEquals(22000.0, adapter.filteredServices[4].due_mileage, 0.0)
        assertEquals(25000.0, adapter.filteredServices[5].due_mileage, 0.0)
    }

    @Test
    fun testEmptyStateWhenNoFilterResults() {
        // Применение фильтра, который не даст результатов
        adapter.filter("шины")

        // Проверка, что список пуст после фильтрации
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun testFilterAndSortCombination() {
        // Применение фильтра
        adapter.filter("масла")

        // Применение сортировки по дате (новые сначала)
        adapter.sort(HistoryAdapter.SortType.DATE_DESC)

        // Проверка результатов
        assertEquals(2, adapter.itemCount)
        assertEquals("2023-06-05", adapter.filteredServices[0].due_date)
        assertEquals("2023-01-15", adapter.filteredServices[1].due_date)
    }

    @Test
    fun testCaseInsensitiveFilter() {
        // Применение фильтра в нижнем регистре
        adapter.filter("замена")

        // Проверка, что найдены все записи независимо от регистра
        assertEquals(6, adapter.itemCount)
    }

    @Test
    fun testSortByDateAscending() {
        // Применение сортировки
        adapter.sort(HistoryAdapter.SortType.DATE_ASC)

        // Проверка результатов
        assertEquals("2023-01-15", adapter.filteredServices[0].due_date)
        assertEquals("2023-02-10", adapter.filteredServices[1].due_date)
        assertEquals("2023-03-20", adapter.filteredServices[2].due_date)
        assertEquals("2023-04-01", adapter.filteredServices[3].due_date)
        assertEquals("2023-05-10", adapter.filteredServices[4].due_date)
        assertEquals("2023-06-05", adapter.filteredServices[5].due_date)
    }

    @Test
    fun testSortByMileageDescending() {
        // Применение сортировки
        adapter.sort(HistoryAdapter.SortType.MILEAGE_DESC)

        // Проверка результатов
        assertEquals(25000.0, adapter.filteredServices[0].due_mileage, 0.0)
        assertEquals(22000.0, adapter.filteredServices[1].due_mileage, 0.0)
        assertEquals(20000.0, adapter.filteredServices[2].due_mileage, 0.0)
        assertEquals(18000.0, adapter.filteredServices[3].due_mileage, 0.0)
        assertEquals(15000.0, adapter.filteredServices[4].due_mileage, 0.0)
        assertEquals(10000.0, adapter.filteredServices[5].due_mileage, 0.0)
    }
}