package com.test.inventorymanagement.db

import android.database.sqlite.SQLiteDatabase
import com.test.inventorymanagement.utils.Constants

object InitializationHelper {

    fun createItemsTable(db: SQLiteDatabase?) {
        WriteUtility.createTable( db, Constants.TABLE_NAME,TableDescriptor.getItemsTable())
    }

}