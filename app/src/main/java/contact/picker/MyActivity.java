package contact.picker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import contact.ContactsListAdapter;
import contact.ShareIntentsUtils;
import contact.views.ContactPickerView;
import kotlin.Function;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class MyActivity extends AppCompatActivity implements ContactsListAdapter.OnItemClickListener {

    ContactPickerView contactPickerView;
    ContactPicker contactPicker;
    AppCompatButton findButton;
    TextView name;

    RecyclerView contactsRecyclerView;
    ContactsListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactPickerView = findViewById(R.id.contact_picker);
        findButton = findViewById(R.id.findContact);
        name = findViewById(R.id.name);
        contactsRecyclerView = findViewById(R.id.contactsRecycler);

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

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickedContact contact = contactPickerView.getContact();
                if(contact == null) {
                    String number = contactPickerView.getEnteredPhone();
                    if(number.length() >= 5) {
                        contact = contactPicker.getContactDisplayNameByNumber(number);
                        if (contact != null) {
                            //contactPickerView.setContact(contact);
                            adapter.addItem(contact);
                        } else {
                            name.setText("Не найдено");
                        }
                    }
                } else {
                    adapter.addItem(contact);
                }
                showAlertDialog(contact);
                contactPickerView.setContact(null);
            }
        });

        List<PickedContact> list = new ArrayList<>();

        adapter = new ContactsListAdapter(list);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerView.setHasFixedSize(false);
        contactsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);
    }

    private void openBottomSheet(final PickedContact contact, final int position) {
        ShareBottomDialogFragment mySheetDialog = new ShareBottomDialogFragment(contact, new ShareBottomDialogFragment.OnShareMenuItemClickedListener() {
            @Override
            public void onCancelShare() {
                adapter.removeItem(contact, position);
            }

            @Override
            public void onResendShare() {
                ShareIntentsUtils.sendShareTextIntent(MyActivity.this, getMessage("iPhone aaronskiy"));
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        mySheetDialog.show(fm, "modalSheetDialog");
    }

    @Override
    public void OnItemClicked(View view, int position) {
        PickedContact contact = adapter.getItem(position);
        openBottomSheet(contact, position);
    }

    private String getMessage(String cameraName) {
        return String.format("Привет, я поделился с тобой камерой %s, так что можешь использовать её в качестве камеры наблюдения.", cameraName);
    }

    private void showAlertDialog(final PickedContact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Сообщить +7 (928) 556-78-54 о том, что вы поделились камерой iPhone aaronskiy?")
                .setTitle("Готово");
        builder.setPositiveButton("Отправить SMS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ShareIntentsUtils.sendSmsIntent(MyActivity.this, contact.getNumber(), getMessage("iPhone aaronskiy"));
            }
        });
/*
        builder.setNeutralButton("Отправить через...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ShareIntentsUtils.sendShareTextIntent(MyActivity.this, getMessage("iPhone aaronskiy"));
            }
        });
*/
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
