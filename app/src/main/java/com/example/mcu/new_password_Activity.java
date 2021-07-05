package com.example.mcu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class new_password_Activity extends AppCompatActivity {
    EditText new_password, confirm_new_password;
    Button sava_new_password;
    ImageView iconBack;
    private final FirebaseFirestore reference = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        new_password = findViewById(R.id.new_password);
        confirm_new_password = findViewById(R.id.confirm_new_password);
        sava_new_password = findViewById(R.id.save_new_password);
        sava_new_password.setOnClickListener(v ->
                validationData());
        iconBack = findViewById(R.id.forget_password_back);
        iconBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, login_Activity.class);
            startActivity(intent);
            finish();
        });
    }

    private void validationData() {
        String new_pass = new_password.getText().toString().trim();
        String con_new_pass = confirm_new_password.getText().toString().trim();
        String email = getIntent().getExtras().getString("Email");

        //trust data
        // password
        if (new_pass.isEmpty()) {
            new_password.requestFocus();
            // change to Alert
            showAlert(getString(R.string.password_is_required));
            return;
        }
        if (new_pass.length() < 8) {
            new_password.requestFocus();
            // change to Alert
            showAlert(getString(R.string.password_must_be_8_digits));
            return;
        }
        if (con_new_pass.isEmpty()) {
            confirm_new_password.requestFocus();
            // change to Alert
            showAlert(getString(R.string.confirm_password_is_required));
            return;
        }
        if (!new_pass.equals(con_new_pass)) {
            confirm_new_password.requestFocus();
            // change to Alert
            showAlert(getString(R.string.password_not_like_confirm_password));
            return;
        }
        // to update Password in firebase
        updatePassword(email, con_new_pass);
    }

    public void updatePassword(String email, String password) {

        Query queryReference = reference.collection("Users").whereEqualTo("E_mail", email);

        queryReference.get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                    Log.e("document", doc.getId());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("password", password);
                    updates.put("confirm_password", password);

                    reference.collection("Users").document(doc.getId()).update(updates).addOnSuccessListener(aVoid -> {

                        Intent intent = new Intent(new_password_Activity.this, password_success_Activity.class);
                        startActivity(intent);
                        finish();
                    }).addOnFailureListener(e -> Log.e("failure", e.toString()));
                }
            }
        });
    }

    void showAlert(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("attention!")
                .setMessage(msg)
                .setIcon(R.drawable.ic_attention)
                .setPositiveButton("Okay!", null)
                .create().show();
    }
}