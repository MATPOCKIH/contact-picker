package contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import contact.picker.PickedContact;
import contact.picker.R;
import contact.views.ContactView;

public class ContactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private enum State {
        CROPPED,
        FULL
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private static final int COUNT_VISIBLE_ITEMS = 3;

    private List<PickedContact> contactList;
    private List<PickedContact> croppedContactList = new ArrayList<>(COUNT_VISIBLE_ITEMS);

    private State currentState = State.FULL;

    private OnItemClickListener onItemClickListener;

    public ContactsListAdapter(List<PickedContact> contactList) {
        this.contactList = contactList;
        this.croppedContactList.addAll(contactList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            ContactView v = (ContactView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new ItemViewHolder(v);
        } else if (viewType == TYPE_HEADER) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contactlist_header, parent, false);
            return new HeaderViewHolder(v);
        } else if (viewType == TYPE_FOOTER) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contactlist_footer, parent, false);
            return new FooterViewHolder(v);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final PickedContact contact = getItem(position);
            final ItemViewHolder itemHolder = ((ItemViewHolder) holder);
            itemHolder.contactView.setContact(contact);
            itemHolder.contactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null) {
                        onItemClickListener.OnItemClicked(view, position);

                    }
                    removeItem(contact, itemHolder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(contactList.isEmpty()) return 0;
        return contactList.size() > COUNT_VISIBLE_ITEMS ? croppedContactList.size() + 2 : croppedContactList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        if(isPositionFooter(position))
            return TYPE_FOOTER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position > 3 && position > croppedContactList.size();
    }

    private PickedContact getItem(int position) {
        return contactList.get(position - 1);
    }

    public void addItem(PickedContact contact) {
        if(contactList.size() < COUNT_VISIBLE_ITEMS) {
            contactList.add(contact);
            croppedContactList.add(contact);
            notifyItemInserted(contactList.size());
        } else if (contactList.size() == COUNT_VISIBLE_ITEMS) {
            // должна появиться кнопка показать все
            notifyItemInserted(COUNT_VISIBLE_ITEMS + 1);
           // croppedContactList.add(contact);
            contactList.add(contact);
        } else {
            contactList.add(contact);
        }
    }

    public void removeItem(PickedContact contact, int position) {
        if(contactList.size() > croppedContactList.size()) {
            // переносим из одного в другой
            PickedContact moveContact = contactList.get(COUNT_VISIBLE_ITEMS);
            croppedContactList.remove(contact);
            croppedContactList.add(moveContact);
        } else {
            croppedContactList.remove(contact);
        }
        contactList.remove(contact);

        if(getItemCount() == 0) {
            notifyItemRangeRemoved(0, 2);
        } else if (getItemCount() == COUNT_VISIBLE_ITEMS + 1) {
            notifyItemRemoved(position);
            notifyItemRemoved(COUNT_VISIBLE_ITEMS + 1 + 2);
        } else {
            notifyItemRemoved(position);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ContactView contactView;
        public ItemViewHolder(ContactView v) {
            super(v);
            contactView = v;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public HeaderViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public FooterViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public interface OnItemClickListener {
        void OnItemClicked (View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
