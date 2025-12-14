package com.example.tempatkita.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tempatkita.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtGotoRegister;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtGotoRegister = findViewById(R.id.txtGotoRegister);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());
        txtGotoRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email & Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
        .addOnSuccessListener(a -> {

            String uid = auth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid).get()
            .addOnSuccessListener(doc -> {

                String role = doc.getString("role");

                getSharedPreferences("USER_DATA", MODE_PRIVATE)
                        .edit()
                        .putString("role", role)
                        .apply();
                // ==========================================================

                if (role.equals("admin")) {
                    startActivity(new Intent(this, AddWisataActivity.class));
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }

                finish();
            });
        });
    }
}
