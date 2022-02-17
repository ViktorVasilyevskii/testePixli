package com.vasilyevskii.test.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vasilyevskii.test.App
import com.vasilyevskii.test.MakePhotoActivity
import com.vasilyevskii.test.R
import com.vasilyevskii.test.api.model.DataDTO

class RecycleViewAdapter : RecyclerView.Adapter<RecycleViewAdapter.RecycleViewHolder>(){

    var dataDTO: List<DataDTO> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val contextView = parent.context
        val layoutIdForListItem = R.layout.data_item

        val inflater = LayoutInflater.from(contextView)
        val view = inflater.inflate(layoutIdForListItem, parent, false)

        return RecycleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        val data = dataDTO[position]
        holder.bind(data.id.toString(), data.target)
    }

    override fun getItemCount(): Int = dataDTO.size

    class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val idTextView: TextView = itemView.findViewById(R.id.text_id)
        private val targetTextView: TextView = itemView.findViewById(R.id.text_target)

        fun bind(id: String, target: String){
            idTextView.text = id
            targetTextView.text = target

            clickItem(target)
        }

        private fun clickItem(valueTarget: String){
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, MakePhotoActivity::class.java)
                intent.putExtra(App().namePutExtraTarget, valueTarget)
                itemView.context.startActivity(intent)
            }
        }


        private fun putExtraMakePhotoActivity(){

        }

    }
}