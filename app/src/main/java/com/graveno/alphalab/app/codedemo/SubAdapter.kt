package com.graveno.alphalab.app.codedemo

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graveno.alphalab.app.codedemo.databinding.AdapterSubBinding

class SubAdapter(
    val context : Context,
    val mainPosition : Int,
    var subList : ArrayList<MainRcModel.SubMain>,
    val callback : OnSub
) : RecyclerView.Adapter<SubAdapter.ViewHold>() {

    private var updatePosition : Int = -1
    private var update1Position : Int = -1
    private var update2Position : Int = -1

    class ViewHold(val binder: AdapterSubBinding) : RecyclerView.ViewHolder(binder.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHold {
        val binder : AdapterSubBinding = AdapterSubBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHold(binder)
    }

    override fun onBindViewHolder(holder: ViewHold, pos: Int) {
        val position = pos
        holder.binder.txtMain.text = subList[position].name
        holder.binder.etxtSelec.setText(subList[pos].userSelection)
        holder.binder.etxtMain.setText(subList[position].userInput)
        holder.binder.etxtSelec.clearFocus()
        holder.binder.etxtMain.clearFocus()
//        holder.binder.etxtMain.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                val model = MainRcModel.SubMain(
//                    id = subList[updatePosition].id,
//                    serverEntry = subList[position].serverEntry,
//                    userInput = p0.toString(),
//                    userSelection = holder.binder.etxtSelec.text.toString(),
//                    name = subList[updatePosition].name
//                )
//                Log.e("TAG sub","found new model as $model")
//                subList.removeAt(updatePosition)
//                subList.add(model)
//                callback.onSubChange(
//                    mainPosition = mainPosition,
//                    subList = subList
//                )
//            }
//
//        })
        holder.binder.etxtMain.setOnFocusChangeListener { view, hasFocus ->
            when(hasFocus) {
                true -> {
                    Log.e("TAG sub","focused on position $position")
                    update1Position = position
                }
                false -> {
                    val model = MainRcModel.SubMain(
                        id = subList[update1Position].id,
                        serverEntry = subList[pos].serverEntry,
                        userInput = holder.binder.etxtMain.text.toString(),
                        userSelection = holder.binder.etxtSelec.text.toString(),
                        name = subList[update1Position].name
                    )
                    subList.removeAt(update1Position)
                    subList.add(update1Position, model)
                    callback.onSubChange(
                        mainPosition = mainPosition,
                        subList = subList
                    )
                }
            }
        }
        holder.binder.etxtSelec.setOnFocusChangeListener { view, hasFocus ->
            when(hasFocus) {
                true -> {
                    Log.e("TAG sub","focused on position $position")
                    update2Position = position
                }
                false -> {
                    val model = MainRcModel.SubMain(
                        id = subList[update2Position].id,
                        serverEntry = subList[pos].serverEntry,
                        userInput = holder.binder.etxtMain.text.toString(),
                        userSelection = holder.binder.etxtSelec.text.toString(),
                        name = subList[update2Position].name
                    )
                    subList.removeAt(update2Position)
                    subList.add(update2Position, model)
                    callback.onSubChange(
                        mainPosition = mainPosition,
                        subList = subList
                    )
                }
            }
        }
//        holder.binder.root.setOnFocusChangeListener { view, hasFocus ->
//            when(hasFocus) {
//                true -> {
//                    Log.e("TAG sub","focused on position $position")
//                    updatePosition = position
//                }
//                false -> {
//                    val model = MainRcModel.SubMain(
//                        id = subList[updatePosition].id,
//                        serverEntry = subList[pos].serverEntry,
//                        userInput = holder.binder.etxtMain.text.toString(),
//                        userSelection = holder.binder.etxtSelec.text.toString(),
//                        name = subList[updatePosition].name
//                    )
//                    Log.e("TAG sub","found new model as $model")
//                    subList.removeAt(updatePosition)
//                    subList.add(model)
////                    subList[updatePosition].userInput =
////                    subList[updatePosition].userSelection = holder.binder.etxtSelec.text.toString()
//                    subList.forEach {
//                        Log.e("TAG","stock ${it.userInput} and ${it.userSelection}")
//                    }
//                    callback.onSubChange(
//                        mainPosition = mainPosition,
//                        subList = this.subList
//                    )
//                }
//            }
//        }
    }

    override fun getItemCount(): Int {
        return subList.size
    }

    interface OnSub {
        fun onSubChange(mainPosition: Int, subList : ArrayList<MainRcModel.SubMain>)
    }
}