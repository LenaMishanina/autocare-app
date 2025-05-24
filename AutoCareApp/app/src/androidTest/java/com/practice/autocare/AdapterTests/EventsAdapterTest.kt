package com.practice.autocare.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsAdapterInstrumentedTest {

    private lateinit var testEvents: MutableList<String>
    private lateinit var onItemDeleted: (Int, String) -> Unit
    private lateinit var adapter: EventsAdapter
    private lateinit var inflater: LayoutInflater
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        inflater = LayoutInflater.from(context)

        testEvents = mutableListOf(
            "Замена масла",
            "Поворот шин",
            "Проверка тормозов"
        )

        onItemDeleted = { _, _ -> }
        adapter = EventsAdapter(testEvents, onItemDeleted)
    }

    @Test
    fun getItemCount_returnsCorrectSize() {
        assertEquals(3, adapter.itemCount)
    }

    @Test
    fun onCreateViewHolder_inflatesCorrectLayout() {
        val parent = createTestParent()
        val holder = adapter.onCreateViewHolder(parent, 0)

        assertNotNull(holder.textView)
        assertEquals(TextView::class.java, holder.textView::class.java)
    }

    @Test
    fun onBindViewHolder_bindsDataCorrectly() {
        val parent = createTestParent()
        val holder = adapter.onCreateViewHolder(parent, 0)

        adapter.onBindViewHolder(holder, 1)

        assertEquals("Поворот шин", holder.textView.text)
    }

    @Test
    fun adapter_handlesEmptyListCorrectly() {
        val emptyAdapter = EventsAdapter(mutableListOf(), onItemDeleted)
        assertEquals(0, emptyAdapter.itemCount)
    }

    @Test
    fun viewHolder_containsCorrectView() {
        val parent = createTestParent()
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        val holder = EventsAdapter.EventViewHolder(view)

        assertNotNull(holder.textView)
        assertEquals(TextView::class.java, holder.textView::class.java)
    }

    @Test
    fun adapter_updatesWhenDataChanges() {
        assertEquals(3, adapter.itemCount)

        testEvents.add("Wheel Alignment")
        assertEquals(4, adapter.itemCount)

        testEvents.removeAt(0)
        assertEquals(3, adapter.itemCount)
    }

    private fun createTestParent(): ViewGroup {
        // Создаем FrameLayout как родительский контейнер
        return FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Добавляем RecyclerView с LayoutManager
            addView(RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
            })
        }
    }
}