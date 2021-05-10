package com.test.inventorymanagement


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.MultiAutoCompleteTextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.inventorymanagement.adapters.SearchResultAdapter
import com.test.inventorymanagement.db.InventoryManagementDBHelper
import com.test.inventorymanagement.interfaces.DataChangeListener
import com.test.inventorymanagement.interfaces.UpdateItemSelectionListener
import com.test.inventorymanagement.models.Item
import com.test.inventorymanagement.utils.CustomProgressbar
import com.test.inventorymanagement.utils.Utils
import com.test.inventorymanagement.utils.toast
import com.test.inventorymanagement.viewmodels.InventoryManagementViewModel


class StoreInventoryActivity : AppCompatActivity(), DataChangeListener,
    UpdateItemSelectionListener {
    private final val TAG: String = "StoreInventoryActivity"
    private lateinit var inventoryManagementViewModel: InventoryManagementViewModel
    private lateinit var mactvManufacturer: androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
    private lateinit var etItemNameSearchBox: androidx.appcompat.widget.AppCompatEditText
    private lateinit var rvSearchResultItemsList: androidx.recyclerview.widget.RecyclerView
    private lateinit var tvNetworkStatus: androidx.appcompat.widget.AppCompatTextView
    private var isConnectedToNetwork: Boolean = false
    private var selectedItemsList: ArrayList<String> = ArrayList<String>()
    private var lastSelectedItemsList: ArrayList<String> = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_items)

        //for viewmodel
        val inventoryManagementViewModel =
            ViewModelProvider(this).get(InventoryManagementViewModel::class.java)
        inventoryManagementViewModel.dataChangeListener = this
        inventoryManagementViewModel.databaseHandler= InventoryManagementDBHelper(this)
        isConnectedToNetwork = Utils.isConnectedToNetwork(this@StoreInventoryActivity)

        //For toll bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle("Store Inventory");
        actionBar?.setDisplayHomeAsUpEnabled(true)
        //for getting views
        tvNetworkStatus = findViewById(R.id.tv_network_status)
        updateNetworkStatus(isConnectedToNetwork)
        etItemNameSearchBox = findViewById(R.id.et_search)
        mactvManufacturer = findViewById(R.id.mactv_manufacturer)
        rvSearchResultItemsList = findViewById(R.id.rv_search_result_list)

        val selectedIds: String = intent.getStringExtra("selectedIds").toString()
        if (!selectedIds.isNullOrEmpty()) {
            lastSelectedItemsList = selectedIds.split(",").toTypedArray().toCollection(ArrayList())

        }


        findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.msg_upload_inventory).text =
            "Store Inventory"
        val BtnEdit = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_search_act)
        BtnEdit.text = "EDIT"
        BtnEdit.setOnClickListener {
            startActivity(Intent(this@StoreInventoryActivity, UpdateInventoryActivity::class.java))
        }
        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_search).setOnClickListener {
            if (etItemNameSearchBox.text.toString()
                    .isNotEmpty() || mactvManufacturer.text.toString().isNotEmpty()
            ) {
                inventoryManagementViewModel.getFilteredItemList(
                    etItemNameSearchBox.text.toString(),
                    mactvManufacturer.text.toString(),
                    selectedItemsList,true
                )
            } else {
                toast("Please enter Item name or select Manufacturer name")
            }
        }
        CustomProgressbar.showProgressBar(this@StoreInventoryActivity,false)
        inventoryManagementViewModel.getManufacturerListFromSQL()
        inventoryManagementViewModel.getAllItemsFromSQL()
        mactvManufacturer.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                val selectedItem: String = parent.getItemAtPosition(position).toString()
                if (!selectedItemsList.contains(selectedItem)) {
                    selectedItemsList.add(selectedItem)
                }
                mactvManufacturer.setText(selectedItemsList.joinToString(separator = ","))
            }
    }

    override fun onResume() {
        super.onResume()
        isConnectedToNetwork = Utils.isConnectedToNetwork(this@StoreInventoryActivity)
        updateNetworkStatus(isConnectedToNetwork)
    }

    private fun updateNetworkStatus(connectedToNetwork: Boolean) {
        if (isConnectedToNetwork) {
            tvNetworkStatus.setTextColor(
                ContextCompat.getColor(
                    this@StoreInventoryActivity,
                    R.color.color_2fed05
                )
            )
            tvNetworkStatus.text = "ON Line"
        } else {
            tvNetworkStatus.setTextColor(
                ContextCompat.getColor(
                    this@StoreInventoryActivity,
                    R.color.color_ed0505
                )
            )
            tvNetworkStatus.text = "off Line"
        }
    }

    override fun OnError(message: String) {
        CustomProgressbar.hideProgressBar()
        toast(message)
    }

    override fun OnManufacturerListReceived(manufacturerList: ArrayList<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.select_dialog_item,
            manufacturerList
        )
        mactvManufacturer.threshold = 1
        mactvManufacturer.setAdapter(adapter)
        mactvManufacturer.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
    }

    override fun OnItemsListReceived(itemsList: ArrayList<Item>) {
        //adding a layoutmanager

        updateSelection(itemsList, lastSelectedItemsList).also {
            val tempitemsList: ArrayList<Item> = it
            rvSearchResultItemsList.layoutManager =
                LinearLayoutManager(this@StoreInventoryActivity )
            val adapter = SearchResultAdapter(tempitemsList, this@StoreInventoryActivity, true)
            CustomProgressbar.hideProgressBar()
            rvSearchResultItemsList.adapter = adapter
        }


    }

    private fun updateSelection(
        itemsList: java.util.ArrayList<Item>,
        lastSelectedItemsList: java.util.ArrayList<String>
    ): java.util.ArrayList<Item> {

        itemsList.forEachIndexed { index, element ->
            if (lastSelectedItemsList.contains(element.id)) {
                itemsList[index].editedFlag = true
            }
        }
        return itemsList
    }

    override fun updateStatus(position: Int, selectedFlag: Boolean) {
        //Not needed
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}