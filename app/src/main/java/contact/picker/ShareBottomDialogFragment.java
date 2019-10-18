package contact.picker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import contact.ContactsListAdapter;
import contact.ShareIntentsUtils;
import contact.views.ContactPickerView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ShareBottomDialogFragment extends BottomSheetDialogFragment implements ContactsListAdapter.OnItemClickListener {

    private ContactPickerView contactPickerView;
    private ContactPicker contactPicker;
    private AppCompatButton shareButton;

    private RecyclerView contactsRecyclerView;
    private ContactsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_share_bottom_sheet_dialog_fragment, container);

        contactPickerView = view.findViewById(R.id.contact_picker);
        shareButton = view.findViewById(R.id.findContact);
        contactsRecyclerView = view.findViewById(R.id.contactsRecycler);

        contactPicker = ContactPicker.Companion.create((AppCompatActivity) getActivity(), contactPickerView, new Function1<PickedContact, Void>(){
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

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickedContact contact = contactPickerView.getContact();
                if(contact == null) {
                    String number = contactPickerView.getEnteredPhone();
                    if(number.length() >= 5) {
                        contact = contactPicker.getContactDisplayNameByNumber(number);
                        if(contact == null) {
                            contact = contactPicker.createContactByNumber(number);
                        }
                        adapter.addItem(contact);
                    }  else {
                        return;
                    }
                } else {
                    adapter.addItem(contact);
                }

                showAlertDialog(contact, "iPhone aaronskiy");
                contactPickerView.setContact(null);
            }
        });

        List<PickedContact> list = new ArrayList<>();

        adapter = new ContactsListAdapter(list);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsRecyclerView.setHasFixedSize(false);
        contactsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

        return view;
    }


    private void openBottomSheet(final PickedContact contact, final int position) {
        ShareActionsBottomDialogFragment mySheetDialog = new ShareActionsBottomDialogFragment(contact, new ShareActionsBottomDialogFragment.OnShareMenuItemClickedListener() {
            @Override
            public void onCancelShare() {
                adapter.removeItem(contact, position);
            }

            @Override
            public void onResendShare() {
                ShareIntentsUtils.sendShareTextIntent((AppCompatActivity) getActivity(), getMessage("iPhone aaronskiy"));
            }
        });
        FragmentManager fm = getActivity().getSupportFragmentManager();
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

    private void showAlertDialog(final PickedContact contact, final String cameraName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Сообщить " + contact.getNumber() + " о том, что вы поделились камерой " + cameraName + "?")
                .setTitle("Готово");
        builder.setPositiveButton("Отправить SMS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ShareIntentsUtils.sendSmsIntent((AppCompatActivity) getActivity(), contact.getNumber(), getMessage(cameraName));
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
