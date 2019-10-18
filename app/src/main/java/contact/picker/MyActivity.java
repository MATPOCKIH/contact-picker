package contact.picker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MyActivity extends AppCompatActivity{

    Button shareIt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareIt = findViewById(R.id.share);
        shareIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet();
            }
        });
    }

    private void openBottomSheet() {
        ShareBottomDialogFragment shareBottomDialogFragment = new ShareBottomDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        shareBottomDialogFragment.show(fm, "shareBottomDialogFragment");
    }

}
