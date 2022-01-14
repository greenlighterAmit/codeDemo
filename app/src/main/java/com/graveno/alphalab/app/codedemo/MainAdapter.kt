package com.graveno.alphalab.app.codedemo

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.graveno.alphalab.app.codedemo.databinding.AdapterMainBinding
import com.graveno.alphalab.app.codedemo.databinding.DialogAddSubBinding


class MainAdapter(
    val context : Context,
    var stockList : ArrayList<MainRcModel>,
    var callback : OnItemClick
) : RecyclerView.Adapter<MainAdapter.ViewHold>(), SubAdapter.OnSub {

    class ViewHold(val binder: AdapterMainBinding, val stockList: ArrayList<MainRcModel>, val callback: OnItemClick,) : RecyclerView.ViewHolder(binder.root), AddSubAdapter.OnAddSub {
        private val TAG : String = "Main Adapter"
        fun bindInfo(
            context: Context,
            model: MainRcModel,
            position: Int,
            subcallback: MainAdapter
        ) {
            var adapter : SubAdapter? = null
            binder.txtSerMain.text = model.serverEntry
            binder.btnAddSub.setOnClickListener {
                //invoke dialog and add new...
                val dialog : Dialog = Dialog(context)
                val diaBinder : DialogAddSubBinding = DialogAddSubBinding.inflate(LayoutInflater.from(context))
                dialog.setContentView(diaBinder.root)
                diaBinder.btnAddSub.setOnClickListener { dialog.dismiss() }
                diaBinder.rcAddSub.layoutManager = LinearLayoutManager(context)
                diaBinder.rcAddSub.adapter = AddSubAdapter(
                    context = context,
                    callback = this,
                    addSubList = ArrayList(model.options),
                    mainPosition = position
                )
                dialog.show()
            }
            when(adapter) {
                null -> {
                    adapter = SubAdapter(
                        context = context,
                        subList = model.selectedOption,
                        callback = subcallback,
                        mainPosition = position
                    )
                    binder.rcSub.layoutManager = LinearLayoutManager(context)
                    binder.rcSub.adapter = adapter
                }
                else -> {
                    adapter?.let {
                        it.subList = ArrayList(model.selectedOption)
                        binder.rcSub.adapter = it
                    }
                }
            }
        }

        override fun onAddSubChange(mainPosition: Int, subModel: MainRcModel.SubMain, position: Int) {
            stockList[mainPosition].apply {
                this.options.removeAt(position)
                this.selectedOption.add(selectedOption.size, subModel)
            }
            callback.onMainChange(model = stockList[mainPosition], position = mainPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHold {
        val binder : AdapterMainBinding = AdapterMainBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHold(binder, stockList, callback)
    }

    override fun onBindViewHolder(holder: ViewHold, position: Int) {
        holder.bindInfo(
            context = context,
            model = stockList[position],
            position = position,
            subcallback = this
        )
    }

    override fun getItemCount(): Int {
        return stockList.size
    }

    interface OnItemClick {
        fun onMainChange(model: MainRcModel, position: Int)
    }

    override fun  onSubChange(mainPosition: Int, subList : ArrayList<MainRcModel.SubMain>) {
        subList.forEach {
            Log.e("TAG sub","found changed as ${it.userInput} and ${it.userSelection} for ${it.name}")
        }
    }
}