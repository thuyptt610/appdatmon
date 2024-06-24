package com.example.doan;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button btnLogin, btnRegister, btnForgotPass;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.Gmail1);
        passwordField = findViewById(R.id.password1);
        btnLogin = findViewById(R.id.btnsignin1);
        btnRegister = findViewById(R.id.btnRe);
        btnForgotPass = findViewById(R.id.btnQuenPass);
        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();
            String pass = passwordField.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(Login.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int isAdmin = databaseHelper.checkIsAdminByEmail(email, pass);
            if (isAdmin == -1) {
                Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                String nameC = databaseHelper.getNameCByEmail(email);
                int customerid = databaseHelper.getCustomerIdByEmail(email);
                String imageResName = databaseHelper.getImageResNameByEmail(email);
                String phone = databaseHelper.getPhoneByEmail(email);

                // Use SharedPreferences to store the customer_id
                getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putInt("customer_id", customerid)
                        .putString("nameC", nameC)// ben user_info
                        .apply();

                // Gán giá trị customerId vào đối tượng user_current của lớp Utils
                Utils.user_current.setCustomerId(customerid);

                Intent intent = new Intent(Login.this, MainActivity2.class);// admin
                intent.putExtra("nameC", nameC);
                Utils.user_current.setNameC(nameC); // lấy username của admin
                if (isAdmin == 1) {
                    intent = new Intent(Login.this, MainActivity.class); // user
                    intent.putExtra("nameC", nameC);
                    intent.putExtra("imageResName", imageResName);
                    intent.putExtra("phone", phone);
                    intent.putExtra("email", email);
                    intent.putExtra("username", email);
                    intent.putExtra("password", pass);
                }
                intent.putExtra("customer_id", customerid);

                Toast.makeText(Login.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                clearFields();
            }
        });

        btnRegister.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Register.class)));
        btnForgotPass.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ForgotPass.class)));
    }

    private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }
}
