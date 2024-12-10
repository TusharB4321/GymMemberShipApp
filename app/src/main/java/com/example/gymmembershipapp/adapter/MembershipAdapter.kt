package com.example.gymmembershipapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymmembershipapp.data.MembershipModel
import com.example.gymmembershipapp.databinding.MembershipListLayoutBinding

class MembershipAdapter(private val onProductClick: (MembershipModel) -> Unit,private val context: Context, private val list:ArrayList<MembershipModel>): RecyclerView.Adapter<MembershipAdapter.ViewHolder>() {
    class ViewHolder( val binding: MembershipListLayoutBinding)
        :RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(MembershipListLayoutBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model=list[position]

        holder.binding.tvCost.text=model.cost
        holder.binding.tvDuration.text=model.duration
        holder.binding.tvPlanName.text=model.planName
        holder.binding.tvBenefits.text=model.benefits

        holder.binding.root.setOnClickListener { onProductClick(model) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}