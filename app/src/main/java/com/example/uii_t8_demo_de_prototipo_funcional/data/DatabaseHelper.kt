package com.example.uii_t8_demo_de_prototipo_funcional.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.uii_t8_demo_de_prototipo_funcional.model.Item

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "app_items.db"
        const val DATABASE_VERSION = 1

        const val TABLE_ITEMS = "items"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_DESC = "description"
        const val COL_CATEGORY = "category"
        const val COL_IMAGE = "imageUrl"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_ITEMS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT NOT NULL,
                $COL_DESC TEXT,
                $COL_CATEGORY TEXT,
                $COL_IMAGE TEXT
            );
        """.trimIndent()
        db.execSQL(createTable)

        // Preload 5 records
        val initial = listOf(
            Item(name = "Rudy", description = "Raza: Labrador Retriever | Género: Macho | Edad: 3 años", category = "Perro"),
            Item(name = "Mimi", description = "Raza: Siamés | Género: Hembra | Edad: 2 años", category = "Gato"),
            Item(name = "Rocky", description = "Raza: Bulldog | Género: Macho | Edad: 5 años", category = "Perro"),
            Item(name = "Lola", description = "Raza: Golden Retriever | Género: Hembra | Edad: 4 años", category = "Perro"),
            Item(name = "Paco", description = "Raza: Periquito | Género: Macho | Edad: 1 año", category = "Ave")
        )
        initial.forEach { insertItem(db, it) }
    }

    // Helper (used only inside onCreate)
    private fun insertItem(db: SQLiteDatabase, item: Item): Long {
        val values = ContentValues().apply {
            put(COL_NAME, item.name)
            put(COL_DESC, item.description)
            put(COL_CATEGORY, item.category)
            put(COL_IMAGE, item.imageUrl)
        }
        return db.insert(TABLE_ITEMS, null, values)
    }

    // Public insert
    fun insertItem(item: Item): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, item.name)
            put(COL_DESC, item.description)
            put(COL_CATEGORY, item.category)
            put(COL_IMAGE, item.imageUrl)
        }
        val id = db.insert(TABLE_ITEMS, null, values)
        db.close()
        return id
    }

    // Read all
    fun getAllItems(): List<Item> {
        val list = mutableListOf<Item>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_ITEMS, null, null, null, null, null, "$COL_ID ASC")
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getLong(it.getColumnIndexOrThrow(COL_ID))
                    val name = it.getString(it.getColumnIndexOrThrow(COL_NAME))
                    val desc = it.getString(it.getColumnIndexOrThrow(COL_DESC)) ?: ""
                    val cat = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY)) ?: ""
                    val img = it.getString(it.getColumnIndexOrThrow(COL_IMAGE))
                    val item = Item(id = id, name = name, description = desc, category = cat, imageUrl = img)
                    list.add(item)
                } while (it.moveToNext())
            }
        }
        db.close()
        return list
    }

    // Read by id
    fun getItemById(id: Long): Item? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ITEMS,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndexOrThrow(COL_NAME))
                val desc = it.getString(it.getColumnIndexOrThrow(COL_DESC)) ?: ""
                val cat = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY)) ?: ""
                val img = it.getString(it.getColumnIndexOrThrow(COL_IMAGE))
                db.close()
                return Item(id = id, name = name, description = desc, category = cat, imageUrl = img)
            }
        }
        db.close()
        return null
    }

    // Update
    fun updateItem(item: Item): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, item.name)
            put(COL_DESC, item.description)
            put(COL_CATEGORY, item.category)
            put(COL_IMAGE, item.imageUrl)
        }
        val rows = db.update(TABLE_ITEMS, values, "$COL_ID = ?", arrayOf(item.id.toString()))
        db.close()
        return rows
    }

    // Delete
    fun deleteItem(id: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_ITEMS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }
}
