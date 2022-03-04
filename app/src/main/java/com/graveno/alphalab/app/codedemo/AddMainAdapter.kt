package com.graveno.alphalab.app.codedemo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graveno.alphalab.app.codedemo.databinding.AdapterAddMainBinding

class AddMainAdapter(
    val context: Context,
    var addMainList : ArrayList<MainRcModel>,
    val callback : OnAddMain
) : RecyclerView.Adapter<AddMainAdapter.ViewHold>() {
    class ViewHold(val binder : AdapterAddMainBinding) : RecyclerView.ViewHolder(binder.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHold {
        val binder : AdapterAddMainBinding = AdapterAddMainBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHold(binder)
    }

    override fun onBindViewHolder(holder: ViewHold, position: Int) {
        holder.binder.txtAddMain.text = addMainList[position].serverEntry
        holder.binder.txtAddMain.setOnClickListener {
            callback.onAddMainSelect(
                model = addMainList[position],
                position = position
            )
        }
    }

    override fun getItemCount(): Int {
        return addMainList.size
    }

    interface OnAddMain {
        fun onAddMainSelect(model: MainRcModel, position: Int)
    }
}