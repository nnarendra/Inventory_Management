package com.test.inventorymanagement.db

import android.database.sqlite.SQLiteDatabase
import android.util.Log

public class WriteUtility {

    companion object {


            fun createTable(db: SQLiteDatabase?, tableName: String, fields: Map<String, String>) {
                var command =
                    "CREATE TABLE $tableName ( _id INTEGER PRIMARY KEY AUTOINCREMENT"
                for ((key, value) in fields) command = "$command, $key $value"
                command = "$command )"
                Log.i("createTable", command)
                db?.execSQL(command)
            }
    }
}