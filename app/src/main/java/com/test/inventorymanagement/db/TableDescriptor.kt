package com.test.inventorymanagement.db

import com.test.inventorymanagement.utils.Constants
import java.util.*

public class TableDescriptor {

    companion object {
        fun getItemsTable(): MutableMap<String, String>{
            val columns: MutableMap<String, String> = LinkedHashMap()
            columns[Constants.SERVER_ID] = "text unique"
            columns[Constants.NAME] = "text"
            columns[Constants.SKU] = "text"
            columns[Constants.MANUFACTURER] = "text"
            columns[Constants.QUANTITY] = "INTEGER"
            columns[Constants.UNIT] = "text"
           return columns
        }
    }
}