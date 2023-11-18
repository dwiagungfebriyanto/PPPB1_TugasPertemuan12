package com.example.contacts.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class ContactRoomDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao?
    companion object {
        @Volatile
        private var INSTANCE: ContactRoomDatabase? = null
        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context) : ContactRoomDatabase? {
            synchronized(ContactRoomDatabase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = databaseBuilder(context.applicationContext,
                        ContactRoomDatabase::class.java, "contact_database")
                        .build()
                }
            }
            return INSTANCE
        }
    }
}