package com.test.inventorymanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.inventorymanagement.R
import com.test.inventorymanagement.interfaces.UpdateItemSelectionListener
import com.test.inventorymanagement.models.Item
import com.test.inventorymanagement.viewholders.SearchResultViewHolder

class SearchResultAdapter(
    val itemsList: ArrayList<Item>,
    val itemselectedListener: UpdateItemSelectionListener,
    val clickEnabledFlag: Boolean
): RecyclerView.Adapter<SearchResultViewHolder>(),
    UpdateItemSelectionListener {
    var adapterItemSelectedListener:UpdateItemSelectionListener?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_result_item_list, parent, false)
        return SearchResultViewHolder(v)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bindItems(itemsList[position],itemselectedListener,this@SearchResultAdapter,clickEnabledFlag)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }



    override fun updateStatus(position: Int, selectedFlag: Boolean) {
        itemsList[position].editedFlag=selectedFlag
    }
}