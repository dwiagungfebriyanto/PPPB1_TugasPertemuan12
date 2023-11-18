package com.example.contacts

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.contacts.data.Contact
import com.example.contacts.data.ContactDao
import com.example.contacts.data.ContactRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DeleteContactFragment(private val contact: Contact) : DialogFragment() {
    private lateinit var mContactDao: ContactDao
    private lateinit var executorService: ExecutorService

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        executorService = Executors.newSingleThreadExecutor()
        val db = ContactRoomDatabase.getDatabase(requireContext())
        mContactDao = db!!.contactDao()!!

        return activity?.let {
            // Menggunakan kelas Builder untuk konstruksi dialog
            val builder = AlertDialog.Builder(it)
            // Mengatur pesan untuk dialog
            builder.setMessage("Delete this contact?")
                // Menambahkan tombol untuk menghapus kontak
                .setPositiveButton("Yes") { dialog, id ->
                    delete(contact)
                    // Menutup activity jika sukses menghapus kontak
                    (activity as? ContactDetailActivity)?.finish()
                }
                // Menambahkan tombol untuk membatalkan penghapusan
                .setNegativeButton("Cancel") { dialog, id ->
                    // Menutup dialog
                    dismiss()
                }
            // Membuat objek AlertDialog dan mengembalikannya
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Metode untuk menghapus kontak menggunakan executor service
    private fun delete(contact: Contact) {
        executorService.execute { mContactDao.delete(contact) }
    }
}