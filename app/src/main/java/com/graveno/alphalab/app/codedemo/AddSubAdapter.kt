package com.graveno.alphalab.app.codedemo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graveno.alphalab.app.codedemo.databinding.AdapterAddSubBinding

class AddSubAdapter(
    val context: Context,
    val callback: OnAddSub,
    var addSubList: ArrayList<MainRcModel.SubMain>,
    val mainPosition: Int
) : RecyclerView.Adapter<AddSubAdapter.ViewHold>() {
    class ViewHold(val binder : AdapterAddSubBinding) : RecyclerView.ViewHolder(binder.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHold {
        val binder : AdapterAddSubBinding = AdapterAddSubBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHold(binder)
    }



    override fun onBindViewHolder(holder: ViewHold, position: Int) {
        holder.binder.txtAddMain.text = addSubList[position].name
        holder.binder.txtAddMain.setOnClickListener {
            callback.onAddSubChange(
                subModel = addSubList[position],
                position = position,
                mainPosition = mainPosition
            )
        }
    }

    override fun getItemCount(): Int {
        return addSubList.size
    }

    interface OnAddSub {
        fun onAddSubChange(mainPosition : Int, subModel : MainRcModel.SubMain, position: Int)
    }
}