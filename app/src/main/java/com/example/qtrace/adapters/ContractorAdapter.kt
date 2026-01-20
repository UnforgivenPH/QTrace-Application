package com.example.qtrace.adapters

import android.app.AlertDialog
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

        // Handle Expertise List
        val expertiseString = if (item.expertise.isNotEmpty()) {
            item.expertise.joinToString(", ")
        } else {
            "General Services"
        }
        holder.specTv.text = "Expertise: $expertiseString"

        // ✅ CLICK LISTENER: Show Full Details in Dialog
        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(holder.itemView.context, com.example.qtrace.ContractorDetailActivity::class.java)
            intent.putExtra("CONTRACTOR_DATA", item)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = list.size
}