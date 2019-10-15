package contact.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;

import contact.picker.MyActivity;
import contact.picker.PickedContact;
import contact.picker.R;
public class ContactPickerView extends LinearLayout {

    public enum ContactPickerState {
        NOT_PICKED,
        PICKED
    }

    private TextView contactNameTextView, contactPhoneTextView;
    private ImageView contactAvatarImageView;
    private RelativeLayout contactContainer, edittextContainer;
    private ImageView pickContactButton, clearButton;

    private Context context;
    private ContactPickerState state = ContactPickerState.NOT_PICKED;

    private PickedContact contact;

    public ContactPickerView(Context context) {
        super(context);
        init(context, null);
    }

    public ContactPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_contact_picker, this, true);
        setOrientation(VERTICAL);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ContactPickerView);
        String contactName = typedArray.getString(R.styleable.ContactPickerView_cpv_contact_name);
        String contactPhone = typedArray.getString(R.styleable.ContactPickerView_cpv_contact_phone);
        typedArray.recycle();

        contactNameTextView = findViewById(R.id.contact_name);
        contactPhoneTextView = findViewById(R.id.contact_phone);
        contactAvatarImageView = findViewById(R.id.contact_avatar);
        contactContainer = findViewById(R.id.contact_container);
        edittextContainer = findViewById(R.id.edittext_container);
        pickContactButton = findViewById(R.id.pick_contact_button);
        clearButton = findViewById(R.id.clear_button);

        //setName(contactName);
       // setPhone(contactPhone);

        pickContactButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onPickContactClicked();
                }
            }
        });

        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setContact(null);
            }
        });

    }

    private void setName(String contactName) {
        contactNameTextView.setText(contactName);
    }

    private void setPhone(String contactPhone) {
        contactPhoneTextView.setText(contactPhone);
    }

    private void setAvatar(String uri) {
        Glide.with(context).load(uri)
                .fallback(R.drawable.ic_person_black_24dp)
                .circleCrop()
                .into(contactAvatarImageView);
    }

    public void setContact(PickedContact contact) {
        this.contact = contact;
        if(contact == null) {
            setName("");
            setPhone("");
            setAvatar(null);
            setState(ContactPickerState.NOT_PICKED);
        } else {
            setName(contact.getName());
            setPhone(contact.getNumber());
            setAvatar(contact.getPhotoUri());
            setState(ContactPickerState.PICKED);
        }
    }

    public void setState(ContactPickerState state) {
        if(state == this.state) return;
        this.state = state;
        animateView(state);
    }

    private void animateView (ContactPickerState state) {

        int startValue = state == ContactPickerState.PICKED ? 0 : -120;
        int finishValue = state == ContactPickerState.PICKED ? -120 : 0;

        ValueAnimator animator = ValueAnimator.ofInt(startValue, finishValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator){
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) edittextContainer.getLayoutParams();
                lp.setMargins(0, (Integer) valueAnimator.getAnimatedValue(), 0,0);
                edittextContainer.setLayoutParams(lp);
            }
        });
        animator.setDuration(200);
        animator.setStartDelay(state == ContactPickerState.PICKED ? 250 : 0);
        animator.start();

        if(state == ContactPickerState.PICKED) {
            ObjectAnimator animY1 = ObjectAnimator.ofFloat(edittextContainer, "alpha", 0f);
            ObjectAnimator animY2 = ObjectAnimator.ofFloat(contactContainer, "alpha", 1f);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.setDuration(500);
            animSetXY.playTogether(animY1, animY2);
            animSetXY.start();
        } else {
            ObjectAnimator animY1 = ObjectAnimator.ofFloat(edittextContainer, "alpha", 1f);
            ObjectAnimator animY2 = ObjectAnimator.ofFloat(contactContainer, "alpha", 0f);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.setDuration(500);
            animSetXY.playTogether(animY1, animY2);
            animSetXY.start();
        }
    }

    OnUserInteractionListener listener;

    public void setListener(OnUserInteractionListener listener) {
        this.listener = listener;
    }

    public interface OnUserInteractionListener {
        void onPickContactClicked();
    }
}
