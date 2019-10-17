package contact.picker;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import contact.ContactsListAdapter;
import contact.TestAdapter;
import contact.views.ContactPickerView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class TestActivity extends AppCompatActivity {


    AppCompatButton add, remove, open;

    RecyclerView rv;
    TestAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        open = findViewById(R.id.open);
        rv = findViewById(R.id.rv);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItem(new PickedContact("+7 928 44 55", "Виктор Алибабаевич", null));
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.removeItem();
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet(new PickedContact("+7 928 44 55", "Виктор Алибабаевич", null));
            }
        });

        List<PickedContact> list = new ArrayList<>();
        list.add(new PickedContact("+7 928 44 55", "Василий Алибабаевич", null));
        list.add(new PickedContact("+7 928 44 55", "Петр Алибабаевич", null));
        list.add(new PickedContact("+7 928 44 55", "Антон Алибабаевич", null));
        list.add(new PickedContact("+7 928 44 55", "Егор Алибабаевич", null));

        adapter = new TestAdapter(list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(false);
        rv.setAdapter(adapter);
        //rv.setNestedScrollingEnabled(false);



    }

    private void openBottomSheet(PickedContact contact) {
        ShareBottomDialogFragment mySheetDialog = new ShareBottomDialogFragment(contact, new ShareBottomDialogFragment.OnShareMenuItemClickedListener() {
            @Override
            public void onCancelShare() {

            }

            @Override
            public void onResendShare() {

            }
        });
        FragmentManager fm = getSupportFragmentManager();
        mySheetDialog.show(fm, "modalSheetDialog");
    }

}
