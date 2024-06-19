package com.example.fit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fit.databinding.ActivityPagesBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PagesActivity extends AppCompatActivity {

    private ActivityPagesBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static Database db = new Database();
    private final String user = mAuth.getCurrentUser().getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("TAG", user);
        replaceFragment(new AtividadesFragment(db, user));
        setupBottomNavigationView();

    }

    private void setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.atividades:
                    replaceFragment(new AtividadesFragment(db, user));
                    break;
                case R.id.metas:
                    replaceFragment(new MetaFragment(db, user));
                    break;
                case R.id.imc:
                    replaceFragment(new ImcFragment());
                    break;
                default:
                    return false;
            }
            return true;
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    // altera o TextView (int)
    public static void setText(View parent, int textViewId, int value) {
        TextView textView = parent.findViewById(textViewId);
        textView.setText(String.valueOf(value));
    }

    // altera o TextView (String)
    public static void setText(View parent, int textViewId, String value) {
        TextView textView = parent.findViewById(textViewId);
        textView.setText(value);
    }
}