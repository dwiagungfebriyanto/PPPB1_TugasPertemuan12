package com.example.contacts.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(contact: Contact)

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @get:Query("SELECT * FROM contact_table ORDER BY name ASC")
    val allContacts: LiveData<List<Contact>>

    @Query("SELECT * FROM contact_table WHERE name LIKE :searchQuery ORDER BY name ASC")
    fun searchContacts(searchQuery: String): List<Contact>
}