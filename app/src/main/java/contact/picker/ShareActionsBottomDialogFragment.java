package contact.picker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import contact.views.ContactView;

public class ShareActionsBottomDialogFragment extends BottomSheetDialogFragment implements NavigationView.OnNavigationItemSelectedListener {

    private PickedContact contact;
    private ContactView contactView;
    private NavigationView navigationView;
    private OnShareMenuItemClickedListener listener;

    public ShareActionsBottomDialogFragment(PickedContact contact, OnShareMenuItemClickedListener listener) {
        this.contact = contact;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_share_action_dialog_fragment, container);
        contactView = view.findViewById(R.id.contact);
        navigationView = view.findViewById(R.id.nav_view_bottomSheetFrag);
        contactView.setContact(contact);

        navigationView.setNavigationItemSelectedListener(this);
        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.menu_action_share_cancel:
                if(listener != null) {
                    listener.onCancelShare();
                }
                break;
            case R.id.menu_action_share_resend:
                if(listener != null) {
                    listener.onResendShare();
                }
                break;
        }
        dismiss();
        return false;
    }

    public interface OnShareMenuItemClickedListener {
        void onCancelShare();
        void onResendShare();
    }
}
