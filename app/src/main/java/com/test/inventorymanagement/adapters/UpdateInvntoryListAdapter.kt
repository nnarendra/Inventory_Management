package com.test.inventorymanagement.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.inventorymanagement.R
import com.test.inventorymanagement.interfaces.UpdateSourceDataListener
import com.test.inventorymanagement.models.Item


class UpdateInvntoryListAdapter(var itemsList: ArrayList<Item>, val updateSourceDataListener: UpdateSourceDataListener) : RecyclerView.Adapter<UpdateInvntoryListAdapter.ViewHolder>(),UpdateSourceDataListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateInvntoryListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.update_qtn_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: UpdateInvntoryListAdapter.ViewHolder, position: Int) {
        holder.bindItems(itemsList[position],updateSourceDataListener,this@UpdateInvntoryListAdapter)
    }

    override fun getItemCount(): Int {
      return  itemsList.size
    }
    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: Item, updateSourceDataListener: UpdateSourceDataListener, itemsListAdapterInterface: UpdateSourceDataListener) {
            val tvSku = itemView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_sku)
            val edQtn  = itemView.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.input_qty)
            val tvName=itemView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_name)
            val tvCompanyName=itemView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_company_name)
            edQtn.setText(item.quantity)
            tvSku.text = item.sku
            tvName.text = item.name
            tvCompanyName.text = item.manufacturer
            edQtn.setOnFocusChangeListener { view, hasFocusChanged ->

                if (!hasFocusChanged) {
                    updateSourceDataListener.OnUpdateQtn(absoluteAdapterPosition,edQtn.getText().toString())
                    itemsListAdapterInterface.OnUpdateQtn(absoluteAdapterPosition,edQtn.getText().toString())

                }
                edQtn.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                        updateSourceDataListener.OnUpdateQtn(absoluteAdapterPosition,edQtn.getText().toString())
                        itemsListAdapterInterface.OnUpdateQtn(absoluteAdapterPosition,edQtn.getText().toString())
                    }

                    override fun afterTextChanged(editable: Editable) {}
                })
            }


        }
    }

    override fun OnUpdateQtn(position: Int, qtn: String) {
        itemsList[position].quantity=qtn
        itemsList[position].editedFlag=true
    }
}