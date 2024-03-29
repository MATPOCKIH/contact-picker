package contact.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import contact.picker.PickedContact;
import contact.picker.R;

public class ContactView extends RelativeLayout {

    private Context context;

    private TextView contactNameTextView, contactPhoneTextView;
    private ImageView contactAvatarImageView;
    private ImageView clearButton, menuButton;

    private boolean hasContextMenu = false;

    public ContactView(Context context) {
        super(context);
        init(context, null);
    }

    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_contact, this, true);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ContactView);
        hasContextMenu = typedArray.getBoolean(R.styleable.ContactView_cv_has_menu, false);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contactNameTextView = findViewById(R.id.contact_name);
        contactPhoneTextView = findViewById(R.id.contact_phone);
        contactAvatarImageView = findViewById(R.id.contact_avatar);
        menuButton = findViewById(R.id.menu_button);
        initMenuButton();
    }

    private void setName(String contactName) {
        contactNameTextView.setText(contactName);
    }

    private void setPhone(String contactPhone) {
        contactPhoneTextView.setText(contactPhone);
    }

    private void setAvatar(String uri) {
        Glide.with(context).load(uri)
                .fallback(R.drawable.ic_account_circle_black_40dp)
                .circleCrop()
                .into(contactAvatarImageView);
    }

    public void setContact(PickedContact contact) {
        setName(contact != null ? contact.getName() : "");
        setPhone(contact != null ? contact.getNumber() : "");
        setAvatar(contact != null ? contact.getPhotoUri() : "");
    }

    private void initMenuButton() {
        menuButton.setVisibility(hasContextMenu ? VISIBLE : GONE);
        if(hasContextMenu) {
            menuButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.OnMenuButtonClicked();
                }
            });
        }
    }

    private OnContactContextMenuClicked listener;

    public void setListener(OnContactContextMenuClicked listener) {
        this.listener = listener;
    }

    public interface OnContactContextMenuClicked {
        void OnMenuButtonClicked();
    }
}
