package contact.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import contact.picker.PickedContact;
import contact.picker.R;
public class ContactPickerView extends LinearLayout {

    public enum ContactPickerState {
        NOT_PICKED,
        PICKED
    }

 //   private TextView contactNameTextView, contactPhoneTextView;
  //  private ImageView contactAvatarImageView;
    private RelativeLayout/* contactContainer,*/ edittextContainer;
    private ImageView pickContactButton/*, clearButton*/;
    private EditText enteredPhoneEditText;
    private ContactView contactView;

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

        enteredPhoneEditText = findViewById(R.id.entered_phone_number);
        edittextContainer = findViewById(R.id.edittext_container);
        pickContactButton = findViewById(R.id.pick_contact_button);
        contactView = findViewById(R.id.contact_view);

        pickContactButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onPickContactClicked();
                }
            }
        });

        contactView.setListener(new ContactView.OnContactClearListener() {
            @Override
            public void OnClearButtonClicked() {
                setContact(null);
            }
        });
    }

    public String getEnteredPhone() {
        return enteredPhoneEditText.getText().toString();
    }

    public void setContact(PickedContact contact) {
        this.contact = contact;
        contactView.setContact(contact);
        enteredPhoneEditText.setText(contact == null ? "" : contact.getNumber());
        setState(contact == null ? ContactPickerState.NOT_PICKED : ContactPickerState.PICKED);
    }

    public PickedContact getContact() {
        return contact;
    }

    private void setState(ContactPickerState state) {
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
            ObjectAnimator animY2 = ObjectAnimator.ofFloat(contactView, "alpha", 1f);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.setDuration(500);
            animSetXY.playTogether(animY1, animY2);
            animSetXY.start();
        } else {
            ObjectAnimator animY1 = ObjectAnimator.ofFloat(edittextContainer, "alpha", 1f);
            ObjectAnimator animY2 = ObjectAnimator.ofFloat(contactView, "alpha", 0f);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.setDuration(500);
            animSetXY.playTogether(animY1, animY2);
            animSetXY.start();
        }
    }

    private OnUserInteractionListener listener;

    public void setListener(OnUserInteractionListener listener) {
        this.listener = listener;
    }

    public interface OnUserInteractionListener {
        void onPickContactClicked();
    }
}
