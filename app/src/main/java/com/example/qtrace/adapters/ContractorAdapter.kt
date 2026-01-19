package com.example.qtrace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qtrace.R
import com.example.qtrace.models.Contractor

class ContractorAdapter(private val list: List<Contractor>) :
    RecyclerView.Adapter<ContractorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.tv_contractor_name)
        val specTv: TextView = view.findViewById(R.id.tv_specialization)
        val personTv: TextView = view.findViewById(R.id.tv_contact_person)
        val phoneTv: TextView = view.findViewById(R.id.tv_phone)
        val addressTv: TextView = view.findViewById(R.id.tv_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contractor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.nameTv.text = item.name
        holder.personTv.text = "Contact: ${item.contactPerson}"
        holder.phoneTv.text = "Tel: ${item.phone}"
        holder.addressTv.text = item.address

        // Join the expertise array into a single string
        if (item.expertise.isNotEmpty()) {
            holder.specTv.text = "Expertise: ${item.expertise.joinToString(", ")}"
        } else {
            holder.specTv.text = "General Services"
        }
    }

    override fun getItemCount() = list.size
}