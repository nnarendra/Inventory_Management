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


}