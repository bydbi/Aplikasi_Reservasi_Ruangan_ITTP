package com.app.logistikittp.view.home.user.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.logistikittp.data.model.Waktu
import com.app.logistikittp.databinding.ItemWaktuBinding

class WaktuAdapter(private var itemList: List<Waktu>, private val waktuItemClickListener: WaktuItemClickListener?) :
    RecyclerView.Adapter<WaktuAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemWaktuBinding) : RecyclerView.ViewHolder(binding.root)

    private val selectedWaktuList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWaktuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.buttonWaktu.text = item.jam
        if (item.status != null) {
            if (item.status) {
                val selected = selectedWaktuList.contains(item.jam)
                holder.binding.buttonWaktu.setBackgroundColor(
                    if (selected) Color.parseColor("#71C4EF") else Color.WHITE
                )
                holder.binding.buttonWaktu.isEnabled = true
                holder.binding.buttonWaktu.setOnClickListener {
                    toggleSelectedWaktu(item.jam!!)
                    notifyDataSetChanged()
                    waktuItemClickListener?.onWaktuClicked(getSelectedWaktuRange())
                }
            } else {
                holder.binding.buttonWaktu.setBackgroundColor(Color.parseColor("#CAC9C6"))
                holder.binding.buttonWaktu.isEnabled = false
            }
        } else {
            holder.binding.buttonWaktu.setBackgroundColor(Color.WHITE)
            holder.binding.buttonWaktu.isEnabled = false
        }
    }

    private fun toggleSelectedWaktu(waktu: String) {
        if (selectedWaktuList.contains(waktu)) {
            selectedWaktuList.remove(waktu)
        } else {
            selectedWaktuList.add(waktu)
        }
    }

    private fun getSelectedWaktuRange(): String {
        val sortedWaktuList = selectedWaktuList.sorted()
        return if (sortedWaktuList.size >= 2) {
            "${sortedWaktuList.first()}-${sortedWaktuList.last()}"
        } else {
            ""
        }
    }

    fun setDataWaktu(waktuList: List<Waktu>) {
        this.itemList = waktuList
        notifyDataSetChanged()
    }

    interface WaktuItemClickListener {
        fun onWaktuClicked(waktuRange: String)
    }

    fun getSelectedWaktu(): String {
        return if (selectedWaktuList.isNotEmpty()) {
            getSelectedWaktuRange()
        } else {
            ""
        }
    }
}
