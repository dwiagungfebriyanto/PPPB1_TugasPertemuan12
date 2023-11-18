package com.example.contacts

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.contacts.data.Contact
import com.example.contacts.data.ContactDao
import com.example.contacts.data.ContactRoomDatabase
import com.example.contacts.databinding.ActivityContactDetailBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ContactDetailActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityContactDetailBinding.inflate(layoutInflater)
    }
    private lateinit var mContactDao: ContactDao
    private lateinit var executorService: ExecutorService

    // Mendefinisikan kode permintaan untuk mengedit kontak
    companion object {
        const val EDIT_CONTACT_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = ContactRoomDatabase.getDatabase(this)
        mContactDao = db!!.contactDao()!!

        with(binding) {
            // Mengambil objek Contact dari intent
            val contact = intent.getSerializableExtra("contact") as Contact
            // Mengisi data kontak ke tampilan
            txtFirstLetter.setText(contact.name.substring(0, 1))
            txtName.setText(contact.name)
            txtPhone.setText(contact.phone)
            txtEmail.setText(contact.email)

            btnEdit.setOnClickListener {
                // Membuat intent untuk ContactFormActivity dengan objek Contact yang akan di-edit
                val intentToContactFormActivity = Intent(this@ContactDetailActivity, ContactFormActivity::class.java)
                intentToContactFormActivity.putExtra("contact", contact)
                startActivityForResult(intentToContactFormActivity, EDIT_CONTACT_REQUEST_CODE)
            }

            btnDelete.setOnClickListener {
                // Menampilkan dialog konfirmasi penghapusan menggunakan DeleteContactFragment
                DeleteContactFragment(contact).show(supportFragmentManager, "DELETE_DIALOG")
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }

            btnPhone.setOnClickListener {
                // Membuat intent untuk melakukan panggilan telepon
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${contact.phone}")
                startActivity(intent)
            }

            btnMessage.setOnClickListener {
                // Membuat intent untuk mengirim pesan SMS
                val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.phone, null))
                startActivity(intent)
            }

            btnEmail.setOnClickListener {
                // Membuat intent untuk mengirim email
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:${contact.email}")
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_CONTACT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Mengambil objek Contact yang telah diedit dari hasil intent
            val editedContact = data?.getSerializableExtra("editedContact") as? Contact

            if (editedContact != null) {
                // Mengupdate tampilan dengan data kontak yang telah di-edit
                with(binding) {
                    txtName.setText(editedContact.name)
                    txtPhone.setText(editedContact.phone)
                    txtEmail.setText(editedContact.email)
                    txtFirstLetter.setText(editedContact.name.substring(0, 1))
                }
            }
        }
    }
}
