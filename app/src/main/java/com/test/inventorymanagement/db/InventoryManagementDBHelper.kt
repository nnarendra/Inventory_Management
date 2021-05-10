package com.test.inventorymanagement.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.test.inventorymanagement.models.Item
import com.test.inventorymanagement.utils.Constants

class InventoryManagementDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "inventoryManagement"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        InitializationHelper.createItemsTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    //TODO currently we don't need this function, since we are not altering table
    //db?.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
    // onCreate(db)

    }

    //method to insert data
    fun addItem(item: Item):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Constants.SERVER_ID, item.id)
        contentValues.put(Constants.NAME, item.name)
        contentValues.put(Constants.SKU, item.sku)
        contentValues.put(Constants.MANUFACTURER,item.manufacturer )
        contentValues.put(Constants.QUANTITY, item.quantity.toInt())
        contentValues.put(Constants.UNIT,item.unit )
        // Inserting Row
        var success:Long = db.insert(Constants.TABLE_NAME, null, contentValues)
        if(success<=-1){
            success = db.update(Constants.TABLE_NAME, contentValues,"server_id= '"+item.id+"'",null).toLong()
        }

        db.close()
        return success
    }
}