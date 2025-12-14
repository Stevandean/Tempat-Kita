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

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmailReg, edtPasswordReg;
    private Button btnRegister;
    private TextView txtGotoLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmailReg = findViewById(R.id.edtEmailReg);
        edtPasswordReg = findViewById(R.id.edtPasswordReg);
        btnRegister = findViewById(R.id.btnRegister);
        txtGotoLogin = findViewById(R.id.txtGotoLogin);
        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
        txtGotoLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void registerUser() {
        String email = edtEmailReg.getText().toString().trim();
        String pass = edtPasswordReg.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email & Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, pass)
        .addOnSuccessListener(a -> {
            String uid = auth.getCurrentUser().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            HashMap<String, Object> user = new HashMap<>();
            user.put("email", email);
            user.put("role", "user");  // default user

            db.collection("users").document(uid)
                    .set(user)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
        });
    }
}
