package com.test.inventorymanagement.viewholders

import android.graphics.Color
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.test.inventorymanagement.R
import com.test.inventorymanagement.interfaces.UpdateItemSelectionListener
import com.test.inventorymanagement.models.Item

class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindItems(
        item: Item,
        itemselectedListener: UpdateItemSelectionListener,
        adapteritemselectedListener: UpdateItemSelectionListener,
        clickEnabledFlag: Boolean
    ) {
        val parentLayout =
            itemView.findViewById<androidx.cardview.widget.CardView>(R.id.parent_layout)
        val tvManufacturerName =
            itemView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_manufacturer_name)
        tvManufacturerName.text = item.manufacturer
        val tvItemName =
            itemView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_item_name)
        tvItemName.text = item.name
        val tvQtn = itemView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_qtn)
        tvQtn.text = item.quantity
        if (item.editedFlag) {
            parentLayout?.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                R.color.teal_200))
        } else {
            parentLayout?.setBackgroundColor(ContextCompat.getColor(
                itemView.context,
                R.color.white))
        }


        parentLayout.setOnClickListener {

            if (clickEnabledFlag) {
                return@setOnClickListener
            }
            updateSelectionColor(
                item.editedFlag,
                parentLayout,
                adapteritemselectedListener,
                itemselectedListener
            )
        }
    }

    private fun updateSelectionColor(
        editedFlag: Boolean,
        parentLayout: CardView?,
        adapteritemselectedListener: UpdateItemSelectionListener,
        itemselectedListener: UpdateItemSelectionListener
    ) {
        if (!editedFlag) {
            adapteritemselectedListener?.updateStatus(absoluteAdapterPosition, true)
            itemselectedListener?.updateStatus(absoluteAdapterPosition, true)
            parentLayout?.setBackgroundColor(ContextCompat.getColor(
                itemView.context,
                R.color.teal_200))
        } else {
            adapteritemselectedListener?.updateStatus(absoluteAdapterPosition, false)
            itemselectedListener?.updateStatus(absoluteAdapterPosition, false)
            parentLayout?.setBackgroundColor(ContextCompat.getColor(
                itemView.context,
                R.color.white))

        }
    }
}