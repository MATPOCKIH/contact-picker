package contact.picker

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.github.farhad.contactpicker.ContactPicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        photo.setImageURI(Uri.parse("content://com.android.contacts/contacts/146/photo"))


        button_pick.setOnClickListener {

            val contactPicker: ContactPicker? = ContactPicker.create(
                activity = this,
                onContactPicked = {
                  /*  Glide.with(this).load(it.photoUri)
                        .addListener()
                        .into(photo)



                        val bp = MediaStore.Images.Media
                            .getBitmap(getContentResolver(),
                                Uri.parse(it.photoUri))*/


                    photo.setImageURI(Uri.parse(it.photoUri))
                    text.text = "${it.name}: ${it.number} [${it.photoUri}]"
                },
                onFailure = { text.text = it.localizedMessage })

            //contactPicker?.pick() // call this to open the picker app chooser
        }
    }


}
