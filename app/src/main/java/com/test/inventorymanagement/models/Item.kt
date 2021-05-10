package com.test.inventorymanagement.models

data class Item(var id: String,
                var sku: String,
                var name: String,
                var manufacturer: String,
                var quantity: String,
                var unit: String,
                var editedFlag: Boolean)
