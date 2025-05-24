package com.practice.autocare.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practice.autocare.R
import com.practice.autocare.models.service.ServiceEventResponse
import com.practice.autocare.models.service.ServiceEventResponseComp
import java.util.Locale

class HistoryAdapter(private val services: ArrayList<ServiceEventResponseComp>): RecyclerView.Adapter<HistoryAdapter.ViewHolderService>() {
    private var filteredServices = ArrayList<ServiceEventResponseComp>(services)

    enum class SortType {
        DATE_ASC, DATE_DESC, MILEAGE_ASC, MILEAGE_DESC
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderService {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.service_layout, parent, false)
        return ViewHolderService(itemView)
    }

    override fun getItemCount(): Int {
        return filteredServices.size
    }

    override fun onBindViewHolder(holder: ViewHolderService, position: Int) {
        val currentService = filteredServices[position]
        holder.serviceType.text = currentService.service_type
        holder.dueDate.text = currentService.due_date
        val mileage = "${currentService.due_mileage.toInt()} км"
        holder.DueMileage.text = mileage
    }

    fun filter(query: String?) {
        filteredServices.clear()
        if (query.isNullOrEmpty()) {
            filteredServices.addAll(services)
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            services.forEach { service ->
                if (service.service_type?.lowercase(Locale.getDefault())?.contains(lowerCaseQuery) == true) {
                    filteredServices.add(service)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun sort(sortType: SortType) {
        when (sortType) {
            SortType.DATE_ASC -> {
                filteredServices.sortBy { it.due_date }
            }
            SortType.DATE_DESC -> {
                filteredServices.sortByDescending { it.due_date }
            }
            SortType.MILEAGE_ASC -> {
                filteredServices.sortBy { it.due_mileage }
            }
            SortType.MILEAGE_DESC -> {
                filteredServices.sortByDescending { it.due_mileage }
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolderService(itemView: View): RecyclerView.ViewHolder(itemView) {
        val serviceType = itemView.findViewById<TextView>(R.id.tvServiceType)
        val dueDate = itemView.findViewById<TextView>(R.id.tvDueDate)
        val DueMileage = itemView.findViewById<TextView>(R.id.tvDueMileage)
    }

}