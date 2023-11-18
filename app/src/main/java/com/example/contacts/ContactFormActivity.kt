package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.contacts.data.Contact
import com.example.contacts.data.ContactDao
import com.example.contacts.data.ContactRoomDatabase
import com.example.contacts.databinding.ActivityContactFormBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ContactFormActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityContactFormBinding.inflate(layoutInflater)
    }
    private lateinit var mContactDao: ContactDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = ContactRoomDatabase.getDatabase(this)
        mContactDao = db!!.contactDao()!!

        with(binding) {
            // Memeriksa apakah ada ekstra "contact" dalam intent
            if (intent.hasExtra("contact")) {
                // Jika ada, berarti ini adalah mode pengeditan kontak
                val contact = intent.getSerializableExtra("contact") as Contact
                // Mengisi formulir dengan data kontak yang ada
                edtName.setText(contact.name)
                edtPhone.setText(contact.phone)
                edtEmail.setText(contact.email)

                btnSave.setOnClickListener {
                    // Membuat objek Contact yang telah diubah
                    val editedContact = Contact(
                        id = contact.id,
                        name = edtName.text.toString(),
                        phone = edtPhone.text.toString(),
                        email = edtEmail.text.toString()
                    )
                    // Memperbarui kontak di database
                    update(editedContact)
                    // Kembali ke activity DetailContactActivity dengan membawa data kontak yang telah diubah
                    returnToDetailActivity(editedContact)
                    // Menutup activity FormContactActivity
                    finish()
                }
            } else {
                // Jika tidak ada ekstra "contact", berarti ini adalah mode pembuatan kontak baru
                btnSave.setOnClickListener {
                    // Memeriksa apakah semua input telah diisi
                    if (edtName.text.toString() != "" && edtPhone.text.toString() != "" && edtEmail.text.toString() != "") {
                        // Jika ya, membuat objek Contact baru
                        val newContact = Contact(
                            name = edtName.text.toString(),
                            phone = edtPhone.text.toString(),
                            email = edtEmail.text.toString()
                        )
                        // Menyimpan kontak baru ke database
                        insert(newContact)
                        // Beralih ke activity DetailContactActivity untuk menampilkan detail kontak baru
                        startActivity(
                            Intent(this@ContactFormActivity, ContactDetailActivity::class.java)
                                .putExtra("contact", newContact)
                        )
                        // Menutup activity FormContactActivity
                        finish()
                    } else {
                        // Jika ada input yang kosong, tampilkan pesan kesalahan
                        Toast.makeText(this@ContactFormActivity, "Something is empty.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            btnCancel.setOnClickListener {
                onBackPressed()
            }
        }
    }

    // Metode untuk menyimpan kontak ke database
    private fun insert(contact: Contact) {
        executorService.execute{ mContactDao.insert(contact) }
    }

    // Metode untuk memperbarui kontak di database
    private fun update(contact: Contact) {
        executorService.execute { mContactDao.update(contact) }
    }

    // Metode untuk kembali ke activity DetailContactActivity dengan membawa data kontak yang telah diubah
    private fun returnToDetailActivity(contact: Contact) {
        val resultIntent = Intent().apply {
            putExtra("editedContact", contact)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}