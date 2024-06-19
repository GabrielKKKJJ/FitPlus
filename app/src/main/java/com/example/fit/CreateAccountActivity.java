package com.example.fit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button createAccountBtn = findViewById(R.id.createAccountBtn);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.emailInput);
                EditText password = findViewById(R.id.passwordInput);
                EditText confirmedPassword = findViewById(R.id.confirmPasswordInput);
                createAccount(email.getText().toString(), password.getText().toString(), confirmedPassword.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("USER", currentUser.getDisplayName());
            reload();
        }
    }

    private void createAccount(String email, String password, String confirmedPass) {
        System.out.println(email+" "+ password+" "+ confirmedPass);
        Log.d("PRINT", email+" "+ password+" "+ confirmedPass);
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(CreateAccountActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(confirmedPass)) {
            Toast.makeText(CreateAccountActivity.this, "A senhas são diferentes!", Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sucesso na criação da conta
                        FirebaseUser user = mAuth.getCurrentUser();
                        createDbForUser(user.getEmail());
                        reload();
                    } else {
                        // Se a criação da conta falhar, mostre uma mensagem para o usuário
                        Toast.makeText(CreateAccountActivity.this, "Falha na criação da conta: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void reload() {
        // Redirect to the MainActivity if the user is already signed in
        Intent intent = new Intent(this, PagesActivity.class);
        startActivity(intent);
        finish();  // Close the CreateAccountActivity so the user can't go back to it
    }

    private void createDbForUser(String email) {
        DocumentReference userDocRef = db.collection("usuarios").document(email);

        // Cria um documento vazio na coleção de atividades
        Map<String, Object> atividadeData = new HashMap<>();
        userDocRef.collection("atividades").document().set(atividadeData);

        // Cria um documento vazio na coleção de metas
        Map<String, Object> metaData = new HashMap<>();
        userDocRef.collection("metas").document().set(metaData);
    }


}
