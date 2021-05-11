package com.test.inventorymanagement.viewmodels

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.test.inventorymanagement.db.InventoryManagementDBHelper
import com.test.inventorymanagement.interfaces.DataChangeListener
import com.test.inventorymanagement.interfaces.UploadDataStatusListener
import com.test.inventorymanagement.models.Item
import com.test.inventorymanagement.utils.Constants
import com.test.inventorymanagement.utils.Utils

class InventoryManagementViewModel : ViewModel() {
    var dataChangeListener: DataChangeListener? = null
    var uploadDataStatusListener: UploadDataStatusListener? = null
    var databaseHandler: InventoryManagementDBHelper? = null
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG: String = "InventoryManagementViewModel"

    fun getManufacturerList() {
        val tempList = ArrayList<String>()
        db.collection(Constants.FIREBASE_TABLE_NAME)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    tempList.add(Utils.getString(document, Constants.MANUFACTURER))
                }
                dataChangeListener?.OnManufacturerListReceived(ArrayList(tempList.distinct()))
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                dataChangeListener?.OnError("some thing went wrong")
            }
    }

    fun getAllItems() {

        val itemsRef = db.collection(Constants.FIREBASE_TABLE_NAME)
        itemsRef.orderBy(Constants.QUANTITY, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                dataChangeListener?.OnItemsListReceived(getListFromResult(result, true))
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                dataChangeListener?.OnError("some thing went wrong")
            }
    }

    private fun getListFromResult(
        result: QuerySnapshot?,
        needToInsertFlag: Boolean
    ): ArrayList<Item> {
        val itemsList = ArrayList<Item>()
        if (result == null) {
            return itemsList
        }
        for (document in result) {
            Log.d(TAG, "${document.id} => ${document.data}")
            val sku: String = Utils.getString(document, Constants.SKU)
            val name: String = Utils.getString(document, Constants.NAME)
            val manufacturer: String = Utils.getString(document, Constants.MANUFACTURER)
            val quantity: String = Utils.getString(document, Constants.QUANTITY)
            val unit: String = Utils.getString(document, Constants.UNIT)
            val item = Item(document.id, sku, name, manufacturer, quantity, unit, false)
            itemsList.add(item)

            if (needToInsertFlag) {
               addItem(item)
            }

        }
        return itemsList
    }

    fun getFilteredItemList(
        namea: String,
        selectedItems: String,
        selectedItemsList: ArrayList<String>, needFromSQL: Boolean
    ) {
        val name: String = namea.capitalize()

        if (name.isNotEmpty() && selectedItemsList.isNotEmpty()) {
            if (needFromSQL) {
                getItemListWithSTwoFiltersFromSQL(name,selectedItemsList)
            } else {
                getItemListWithSelectedItemFilters(selectedItemsList)
            }

        } else if (selectedItems.isNotEmpty()) {
            if (needFromSQL) {
                getItemListWithSelectedItemFiltersFromSQL(selectedItemsList)
            } else {
                getItemListWithSelectedItemFilters(selectedItemsList)
            }

        } else if (name.isNotEmpty()) {
            if (needFromSQL) {
                getItemListWithNameFiltersFromSQL(name)
            } else {
                getItemListWithNameFilters(name)
            }


        } else {
            dataChangeListener?.OnError("something went wrong")
        }
    }

    private fun getItemListWithSTwoFiltersFromSQL(name: String,selectedItemsList: java.util.ArrayList<String>) {
        val itemList: ArrayList<Item>
        val args:String=selectedItemsList.joinToString(separator = "','")
        val db = databaseHandler?.readableDatabase
        var cursor: Cursor? = db?.query(

            Constants.TABLE_NAME,
            null,
            Constants.KEY_NAME + " like ? OR manufacturer in('$args')",
            arrayOf("$name%"),
            null,
            null,
            "quantity Desc",
            null
        )
        itemList=getItemsFromCursor(cursor)
        if (itemList.isNotEmpty()){
            dataChangeListener?.OnItemsListReceived(itemList)
        }else{
            dataChangeListener?.OnError("No items found")
        }
    }

    private fun getItemListWithNameFiltersFromSQL(name: String) {
        val itemList: ArrayList<Item>
        val db = databaseHandler?.readableDatabase
       /* var cursor: Cursor? = db?.rawQuery(
            "SELECT * FROM items where name like '%" + name
                    + "%'", null
        )*/

        var cursor: Cursor? = db?.query(

            Constants.TABLE_NAME,
            null,
            Constants.KEY_NAME + " like ?",
            arrayOf("$name%"),
            null,
            null,
            "quantity Desc",
            null
        )
        itemList=getItemsFromCursor(cursor)
        if (itemList.isNotEmpty()){
            dataChangeListener?.OnItemsListReceived(itemList)
        }else{
            dataChangeListener?.OnError("No items found")
        }
    }

    private fun getItemListWithSelectedItemFiltersFromSQL(finalSelectedItems: ArrayList<String>) {
        var itemList: ArrayList<Item>
        val args:String=finalSelectedItems.joinToString(separator = "','")
        //val selectQuery = "SELECT  * FROM items where  manufacturer in('$args') order by quantity Desc"
        val db = databaseHandler?.readableDatabase
        val cursor: Cursor? =
            db?.query(
                Constants.TABLE_NAME,
                null,
                "manufacturer in('$args')",
                null,
                null,
                null,
                "quantity Desc"
            )
        itemList=getItemsFromCursor(cursor)
        if (itemList.isNotEmpty()){
            dataChangeListener?.OnItemsListReceived(itemList)
        }else{
            dataChangeListener?.OnError("No items found")
        }

    }

    fun getAllItemsFromSQL() {
        var itemList: ArrayList<Item> = ArrayList<Item>()
        val selectQuery = "SELECT  * FROM items order by quantity Desc"
        val db = databaseHandler?.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db?.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {

            dataChangeListener?.OnError(e.message.toString())
        }
        itemList=getItemsFromCursor(cursor)
         if (itemList.isNotEmpty()){
             dataChangeListener?.OnItemsListReceived(itemList)
         }else{
             dataChangeListener?.OnError("No items found")
         }
    }
    fun getManufacturerListFromSQL(){
        val itemList: ArrayList<String> = ArrayList<String>()
        val selectQuery = "select  DISTINCT manufacturer  from items"
        val db = databaseHandler?.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db?.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {

            dataChangeListener?.OnError(e.message.toString())
        }

        if (cursor?.moveToFirst() == true) {
            do {

                val manufacturer = cursor?.getString(cursor.getColumnIndex(Constants.MANUFACTURER))

                itemList.add(manufacturer)
            } while (cursor.moveToNext())
        }
        if (itemList.isNotEmpty()){
            dataChangeListener?.OnManufacturerListReceived(itemList)
        }else{
            dataChangeListener?.OnError("No items found")
        }
    }
    private fun getItemsFromCursor(cursor: Cursor?):ArrayList<Item> {
        val itemList: ArrayList<Item> = ArrayList<Item>()
        if (cursor?.moveToFirst() == true) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(Constants.SERVER_ID))
                val sku = cursor.getString(cursor.getColumnIndex(Constants.SKU))
                val name = cursor.getString(cursor.getColumnIndex(Constants.NAME))
                val manufacturer = cursor.getString(cursor.getColumnIndex(Constants.MANUFACTURER))
                val quantity = cursor.getString(cursor.getColumnIndex(Constants.QUANTITY))
                val unit = cursor.getString(cursor.getColumnIndex(Constants.UNIT))

                var item = Item(id, sku, name, manufacturer, quantity, unit, false)
                itemList.add(item)
            } while (cursor.moveToNext())
        }
        return itemList
    }

    private fun getItemListWithNameFilters(name: String) {
        val itemsRef = db.collection(Constants.FIREBASE_TABLE_NAME)
        itemsRef.orderBy("name").startAt(name)
            .endAt(name + '\uf8ff')//.orderBy("quantity", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                dataChangeListener?.OnItemsListReceived(getListFromResult(result, false))
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                dataChangeListener?.OnError(exception.message.toString())
            }

    }

    private fun getItemListWithSelectedItemFilters(finalSelectedItems: java.util.ArrayList<String>) {

        var itemsRef = db.collection(Constants.FIREBASE_TABLE_NAME)
        itemsRef.whereIn(Constants.MANUFACTURER, finalSelectedItems)
            //  itemsRef.orderBy("quantity", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                dataChangeListener?.OnItemsListReceived(getListFromResult(result, false))

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                dataChangeListener?.OnError(exception.message.toString())
            }
    }

    private fun getItemListWithTwoFilters(
        name: String,
        finalSelectedItems: java.util.ArrayList<String>
    ) {

        // Create a reference to the cities collection
        var itemsRef = db.collection(Constants.FIREBASE_TABLE_NAME)
        itemsRef.whereIn(Constants.MANUFACTURER, finalSelectedItems)
        itemsRef.whereGreaterThanOrEqualTo("name", name)
            //itemsRef.orderBy("quantity", Query.Direction.DESCENDING)

            //itemsRef.orderBy("name").startAt(name).endAt(name + '\uf8ff')
            .get()
            .addOnSuccessListener { result ->
                dataChangeListener?.OnItemsListReceived(getListFromResult(result, false))
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                dataChangeListener?.OnError(exception.message.toString())
            }
    }

    fun updateItems(itemsList: java.util.ArrayList<Item>) {
        for (item in itemsList) {
            if (item.editedFlag) {

                var hashMap: HashMap<String, Any> = HashMap<String, Any>()
                hashMap.put(Constants.SKU, item.sku)
                hashMap.put(Constants.NAME, item.name)
                hashMap.put(Constants.MANUFACTURER, item.manufacturer)
                hashMap.put(Constants.UNIT, item.unit)
                hashMap.put(Constants.QUANTITY, item.quantity)
                db.collection(Constants.FIREBASE_TABLE_NAME).document(item.id)
                    .set(hashMap)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                        uploadDataStatusListener?.OnEventListener("Inventory quantity successfully updated")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error writing document", e)
                        uploadDataStatusListener?.OnEventListener("Something went wrong while updating Inventory quantity ")
                    }
            }
        }
    }

    //method to insert data
    fun addItem(item: Item): Long? {
        val db = databaseHandler?.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Constants.SERVER_ID, item.id)
        contentValues.put(Constants.NAME, item.name)
        contentValues.put(Constants.SKU, item.sku)
        contentValues.put(Constants.MANUFACTURER, item.manufacturer)
        contentValues.put(Constants.QUANTITY, item.quantity.toInt())
        contentValues.put(Constants.UNIT, item.unit)
        // Inserting Row
        var success: Long? = db?.insert(Constants.TABLE_NAME, null, contentValues)

            if(success != null&&success<=-1){
                success = db?.update(
                    Constants.TABLE_NAME,
                    contentValues,
                    "server_id= '" + item.id + "'",
                    null
                )?.toLong()

            }

        db?.close()
        return success
    }
}



