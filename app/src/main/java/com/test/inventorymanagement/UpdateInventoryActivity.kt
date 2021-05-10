package com.test.inventorymanagement

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.inventorymanagement.adapters.UpdateInvntoryListAdapter
import com.test.inventorymanagement.application.InventoryManagementApplication
import com.test.inventorymanagement.db.InventoryManagementDBHelper
import com.test.inventorymanagement.interfaces.DataChangeListener
import com.test.inventorymanagement.interfaces.UpdateSourceDataListener
import com.test.inventorymanagement.interfaces.UploadDataStatusListener
import com.test.inventorymanagement.models.Item
import com.test.inventorymanagement.utils.CustomProgressbar
import com.test.inventorymanagement.utils.Utils
import com.test.inventorymanagement.utils.toast
import com.test.inventorymanagement.viewmodels.InventoryManagementViewModel

class UpdateInventoryActivity : AppCompatActivity(), UpdateSourceDataListener,
    DataChangeListener, UploadDataStatusListener {
    private val TAG: String = "ItemListActivity"
    private var itemsList = ArrayList<Item>()
    private var isConnectedToNetwork: Boolean = false
    private lateinit var rvItemsList: RecyclerView
    private var itemQtnEditedFlag: Boolean = false
    private lateinit var tvNetworkStatus: androidx.appcompat.widget.AppCompatTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        //for viewmodel
        val inventoryManagementViewModel =
            ViewModelProvider(this).get(InventoryManagementViewModel::class.java)
        inventoryManagementViewModel.dataChangeListener = this
        inventoryManagementViewModel.uploadDataStatusListener = this
        inventoryManagementViewModel.databaseHandler=InventoryManagementDBHelper(this)
        isConnectedToNetwork = Utils.isConnectedToNetwork(this@UpdateInventoryActivity)
        //For toll bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Update Inventory";


        //getting views from xml
        tvNetworkStatus = findViewById(R.id.tv_network_status)
        updateNetworkStatus(isConnectedToNetwork)
        rvItemsList = findViewById(R.id.rv_update_items_list)
        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.bt_search).setOnClickListener {
            InventoryManagementApplication.getInstance().trackEvent("UpdateInventoryActivity","Search","",this@UpdateInventoryActivity)
            val intent =
                Intent(this@UpdateInventoryActivity, SearchItemsActivity::class.java)
            startActivity(intent)
        }
        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_save).setOnClickListener {
            var isConnected: Boolean = Utils.isConnectedToNetwork(this@UpdateInventoryActivity)
            updateNetworkStatus(isConnected)
            if (isConnected && itemQtnEditedFlag) {
                CustomProgressbar.showProgressBar(this@UpdateInventoryActivity, false)
                inventoryManagementViewModel.updateItems(itemsList)
            } else {

                if (!itemQtnEditedFlag) {
                    toast("All item quantities already are in sync")
                } else {
                    toast("Please check Network connection to update inventory")
                }

            }

        }
        //adding a layoutmanager
        CustomProgressbar.showProgressBar(this@UpdateInventoryActivity, false)
        rvItemsList.layoutManager = LinearLayoutManager(this@UpdateInventoryActivity)
        inventoryManagementViewModel.getAllItems()

    }


    override fun onResume() {
        super.onResume()
        InventoryManagementApplication.getInstance().trackScreenView("UpdateInventoryActivity",this@UpdateInventoryActivity)
        isConnectedToNetwork = Utils.isConnectedToNetwork(this@UpdateInventoryActivity)
        updateNetworkStatus(isConnectedToNetwork)
    }

    private fun updateNetworkStatus(connectedToNetwork: Boolean) {
        if (connectedToNetwork) {
            tvNetworkStatus.setTextColor(
                ContextCompat.getColor(
                    this@UpdateInventoryActivity,
                    R.color.color_2fed05
                )
            )
            tvNetworkStatus.text = "ON Line"
        } else {
            tvNetworkStatus.setTextColor(
                ContextCompat.getColor(
                    this@UpdateInventoryActivity,
                    R.color.color_ed0505
                )
            )
            tvNetworkStatus.text = "off Line"
        }
    }

    override fun OnUpdateQtn(position: Int, qtn: String) {
        itemQtnEditedFlag = true
        itemsList[position].quantity = qtn
        itemsList[position].editedFlag = true
    }

    override fun OnError(message: String) {
        CustomProgressbar.hideProgressBar()
        toast(message)
    }

    override fun OnManufacturerListReceived(manufacturerList: ArrayList<String>) {
        //This function is not needed here
    }

    override fun OnItemsListReceived(itemsList: ArrayList<Item>) {
        this.itemsList = itemsList
        val adapter =
            UpdateInvntoryListAdapter(itemsList, this@UpdateInventoryActivity)
        CustomProgressbar.hideProgressBar()
        rvItemsList.adapter = adapter
    }

    override fun OnEventListener(message: String) {
        CustomProgressbar.hideProgressBar()
        toast(message)
    }
}