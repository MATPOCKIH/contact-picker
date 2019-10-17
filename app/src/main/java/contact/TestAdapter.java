package contact;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import contact.picker.PickedContact;
import contact.picker.R;
import contact.views.ContactView;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ItemViewHolder> {

    List<PickedContact> dataset;

    public TestAdapter(List<PickedContact> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder((ContactView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.contactView.setContact(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addItem(PickedContact contact) {
        dataset.add(0,contact);
        notifyItemInserted(0);
    }

    public void removeItem() {
        dataset.remove(0);
        notifyItemRemoved(0);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ContactView contactView;
        public ItemViewHolder(ContactView v) {
            super(v);
            contactView = v;
        }
    }
}
