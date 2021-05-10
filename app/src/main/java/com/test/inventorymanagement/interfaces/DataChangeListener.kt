package com.test.inventorymanagement.interfaces

import com.test.inventorymanagement.models.Item

interface DataChangeListener {
    fun OnError(message:String)
    fun OnManufacturerListReceived(manufacturerList:ArrayList<String>)
    fun OnItemsListReceived(itemsList:ArrayList<Item>)
}