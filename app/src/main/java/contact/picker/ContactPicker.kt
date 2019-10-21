package contact.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import contact.views.ContactPickerView
import java.lang.RuntimeException


data class PickedContact(
    val number: String,
    val name: String?,
    val photoUri: String?
)

class ContactPicker constructor(private val requestCode: Int = 23) : Fragment() {

    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2

    private lateinit var onContactPicked: (PickedContact) -> Void
    private lateinit var onFailure: (Throwable) -> Unit
    private lateinit var activity: AppCompatActivity
    private lateinit var contactView: ContactPickerView

    companion object {

        private const val TAG = "ContactPicker"

        fun create(
            activity: AppCompatActivity,
            contactView: ContactPickerView,
            onContactPicked: (PickedContact) -> Void,
            onFailure: (Throwable) -> Unit
        ): ContactPicker? {

            return try {
                val picker = ContactPicker()
                picker.activity = activity
                picker.contactView = contactView
                picker.onContactPicked = onContactPicked
                picker.onFailure = onFailure
                Handler().post {
                    activity.supportFragmentManager.beginTransaction()
                        .add(picker, TAG)
                        .commitNowAllowingStateLoss()
                }
                picker
            } catch (e: Exception) {
                onFailure(e)
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contactView.setListener { tryPick() }
        return null
    }

    fun tryPick() {
        getPermissionToReadUserContacts()
    }

    fun pick() {
        try {

            Intent().apply {
                data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                action = Intent.ACTION_PICK
                startActivityForResult(this, requestCode)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun getPermissionToReadUserContacts() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                MY_PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            pick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                this.requestCode -> {
                    var cursor: Cursor? = null
                    try {
                        cursor = data?.data.let { uri ->
                            uri as Uri
                            activity?.contentResolver?.query(uri, null, null, null, null)
                        }
                        cursor?.let {
                            it.moveToFirst()
                            val phoneNumber =
                                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val name =
                                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            val photoUri =
                                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI))
                            it.close()
                            val contact = PickedContact(phoneNumber, name, photoUri)
                            contactView.setContact(contact)
                            onContactPicked(contact)
                        }

                    } catch (e: Exception) {
                        onFailure(e)
                        cursor?.close()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    pick()
                } else {
                    // permission denied
                }
                return
            }
        }
    }

    fun getContactDisplayNameByNumber(number: String): PickedContact? {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        var name = ""

        val contentResolver = activity?.contentResolver
        val contactLookup = contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI),
            null,
            null,
            null
        )

        try {
            if (contactLookup != null && contactLookup!!.getCount() > 0) {
                contactLookup!!.moveToNext()
                name =
                    contactLookup!!.getString(contactLookup!!.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                val phoneNumber = contactLookup.getString(contactLookup!!.getColumnIndex(ContactsContract.PhoneLookup.NUMBER))
                val photoUri = contactLookup.getString(contactLookup!!.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI))
                return PickedContact(phoneNumber, name, photoUri)
            }
        }
        finally {
            if (contactLookup != null) {
                contactLookup!!.close()
            }
        }
        return null
    }

    fun createContactByNumber(number: String): PickedContact {
        return PickedContact(number, "Без имени", null)
    }


}