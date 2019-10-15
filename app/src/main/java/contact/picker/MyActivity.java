package contact.picker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;


import contact.views.ContactPickerView;
import kotlin.Function;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class MyActivity extends AppCompatActivity {
    AppCompatButton button;
    AppCompatTextView text;
    AppCompatImageView image;
    AppCompatImageView thumbnail;
    ContactPickerView contactPickerView;

    ContactPicker contactPicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button_pick);
        text = findViewById(R.id.text);
        image = findViewById(R.id.photo);
        thumbnail = findViewById(R.id.thumbnail);
        contactPickerView = findViewById(R.id.contact_picker);

        new Function1<PickedContact, Void>(){
            @Override
            public Void invoke(PickedContact contact) {
                return null;
            }
        };

        contactPicker = ContactPicker.Companion.create(this, contactPickerView, new Function1<PickedContact, Void>(){
            @Override
            public Void invoke(PickedContact contact) {
                return null;
            }
        }, new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                return null;
            }
        });

    }
}
