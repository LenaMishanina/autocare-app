package com.practice.autocare.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practice.autocare.R
import com.practice.autocare.models.service.ServiceEventResponse

class HistoryAdapter(private val services: ArrayList<ServiceEventResponse>): RecyclerView.Adapter<HistoryAdapter.ViewHolderService>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderService {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.service_layout, parent, false)
        return ViewHolderService(itemView)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: ViewHolderService, position: Int) {
        val currentService = services[position]
        holder.serviceType.text = currentService.service_type
        holder.dueDate.text = currentService.due_date
        val mileage = currentService.due_mileage.toString()
        holder.DueMileage.text = mileage
    }

    class ViewHolderService(itemView: View): RecyclerView.ViewHolder(itemView) {
        val serviceType = itemView.findViewById<TextView>(R.id.tvServiceType)
        val dueDate = itemView.findViewById<TextView>(R.id.tvDueDate)
        val DueMileage = itemView.findViewById<TextView>(R.id.tvDueMileage)
    }

}