package com.graveno.alphalab.app.codedemo

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.graveno.alphalab.app.codedemo.AppCloning.checkAppCloning
import com.graveno.alphalab.app.codedemo.AppCloning.killProcess
import com.graveno.alphalab.app.codedemo.databinding.ActivityMainBinding
import com.graveno.alphalab.app.codedemo.databinding.AdapterAddMainBinding
import com.graveno.alphalab.app.codedemo.databinding.DialogAddMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), MainAdapter.OnItemClick, AddMainAdapter.OnAddMain {

    private val TAG : String = "MainActivity"
    private lateinit var binder : ActivityMainBinding
    private var adapter : MainAdapter? = null
    private var mainList: ArrayList<MainRcModel> = ArrayList()
    private var responseList : ArrayList<MainRcModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        when(checkAppCloning(activity = this)){
            true -> {
                //real app...
                Log.e(TAG,"launching now....")
                CoroutineScope(Main).launch {
                    someVM().getApidata().observe(this@MainActivity, { list ->
                        Log.e(TAG,"found data as ${list.toArray().toString()}")
                        viewModelHandler(list)
                    })
                }
            }
            false -> {
                //fake app...
                killProcess(this)
            }
        }
    }

    private fun viewModelHandler(list: ArrayList<MainRcModel>) {
        when{
            list.isNullOrEmpty() -> Log.e(TAG,"empty list recieved")
            list.isNotEmpty() -> {
                responseList.clear()
                mainList.clear()
                responseList.addAll(list)

                //initialize...
                adapter = MainAdapter(
                    context = this@MainActivity,
                    stockList = mainList,
                    callback = this@MainActivity
                )
                binder.rcUser.layoutManager = LinearLayoutManager(this)
                binder.rcUser.adapter = adapter
            }
        }

        binder.btnAdd.setOnClickListener {
            val adapterAdd = AddMainAdapter(
                context = this,
                addMainList = responseList,
                callback = this
            )
            val dialog : Dialog = Dialog(this)
            val diaBinder : DialogAddMainBinding = DialogAddMainBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(diaBinder.root)
            diaBinder.btnAddMain.setOnClickListener { dialog.dismiss() }
            diaBinder.rcAddMain.layoutManager = LinearLayoutManager(this)
            diaBinder.rcAddMain.adapter = adapterAdd
            dialog.show()
        }
    }

    override fun onMainChange(model: MainRcModel, position: Int) {
//        mainList.forEachIndexed { index, mainRcModel ->
//            when(index) {
//                position -> {
//                    mainRcModel.options = model.options
//                    mainRcModel.selectedOption = model.selectedOption
//                    mainRcModel.serverEntry = model.serverEntry
//                }
//            }
//        }
        mainList.removeAt(position)
        mainList.add(position, model)
//        mainList[position].apply {
//            this.options = model.options
//            this.serverEntry = model.serverEntry
//            this.selectedOption = model.selectedOption
//        }
        mainList.forEach {
            Log.e(TAG,"found ${it.serverEntry} and ${it.selectedOption}")
        }
        adapter?.let {
            it.stockList = mainList
            when {
                !binder.rcUser.isComputingLayout && binder.rcUser.scrollState == SCROLL_STATE_IDLE -> {
                    binder.rcUser.adapter?.notifyDataSetChanged()
                }
                else -> {
                    binder.rcUser.post(java.lang.Runnable { it.notifyDataSetChanged() })
                }
            }
        }
    }

    override fun onAddMainSelect(model: MainRcModel, position: Int) {
        //todo:reset is happening here...
        responseList.removeAt(position)
        mainList.add(mainList.size, model)
        adapter?.let {
            it.stockList = mainList
            it.notifyDataSetChanged()
        }
    }
}

class someVM {
    val TAG : String = "ViewModel"
    suspend fun getApidata(): MutableLiveData<ArrayList<MainRcModel>> {
        val data : MutableLiveData<ArrayList<MainRcModel>> = MutableLiveData()
        withContext(IO) {
            Log.e(TAG, "calling get api for now....")
            repo().apiCall().collectLatest {
                data.postValue(it)
            }
        }
        return data
    }
}

class repo {
    val TAG : String = "Repository"
    suspend fun apiCall() : Flow<ArrayList<MainRcModel>>{
        return flow<ArrayList<MainRcModel>> {
            Log.e(TAG,"inside flow now")
            withContext(IO) {
                val options : ArrayList<MainRcModel.SubMain> = ArrayList()
                options.add(
                    MainRcModel.SubMain(
                        userSelection = null,
                        userInput = null,
                        id = 1,
                        serverEntry = "first opt",
                        name = "alpha"
                ))
                options.add(
                    MainRcModel.SubMain(
                        userSelection = null,
                        userInput = null,
                        id = 2,
                        serverEntry = "second opt",
                        name = "beta"
                    ))
                options.add(
                    MainRcModel.SubMain(
                        userSelection = null,
                        userInput = null,
                        id = 3,
                        serverEntry = "thirs opt",
                        name = "gamma"
                    ))
                options.add(
                    MainRcModel.SubMain(
                        userSelection = null,
                        userInput = null,
                        id = 3,
                        serverEntry = "thirs opt",
                        name = "java"
                    ))
                options.add(
                    MainRcModel.SubMain(
                        userSelection = null,
                        userInput = null,
                        id = 3,
                        serverEntry = "thirs opt",
                        name = "kotlin"
                    ))
                options.add(
                    MainRcModel.SubMain(
                        userSelection = null,
                        userInput = null,
                        id = 4,
                        serverEntry = "thirs opt",
                        name = "swift"
                    ))
                options.add(
                    MainRcModel.SubMain(
                        userSelection = null,
                        userInput = null,
                        id = 5,
                        serverEntry = "thirs opt",
                        name = "c++"
                    ))
                val data : ArrayList<MainRcModel> = ArrayList()
                data.clear()
                data.add(
                    MainRcModel(
                        serverEntry = "first",
                        options = ArrayList(options),
                        selectedOption = ArrayList()
                    )
                )
                data.add(
                    MainRcModel(
                        serverEntry = "second",
                        options = ArrayList(options)
                    )
                )
                data.add(
                    MainRcModel(
                        serverEntry = "third",
                        options = ArrayList(options)
                    )
                )
                data.add(
                    MainRcModel(
                        serverEntry = "forth",
                        options = ArrayList(options)
                    )
                )
                data.add(
                    MainRcModel(
                        serverEntry = "fifth",
                        options = ArrayList(options)
                    )
                )
                delay(TimeUnit.SECONDS.toMillis(2))
                Log.e(TAG,"found data...")
                this@flow.emit(data)
            }
        }
    }
}