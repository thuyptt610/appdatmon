package com.example.doan;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class CustomerAD extends AppCompatActivity {
    private ArrayList<User> users;
    private UserAdapter userAdapter;
    private EditText editTextNameC, editTextEmail, editTextPhone, editTextUsername, editTextPassword, editTextImage;
    private Button buttonEditUser, btnBack;
    private ListView listViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_ad);

        // Initialize views
        editTextNameC = findViewById(R.id.editTextNameC);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextImage = findViewById(R.id.editTextImage);
        buttonEditUser = findViewById(R.id.buttonEditUser);
        listViewUsers = findViewById(R.id.listViewUsers);
        btnBack = findViewById(R.id.buttonBack);

        users = new ArrayList<>();
        userAdapter = new UserAdapter(this, users);
        listViewUsers.setAdapter(userAdapter);

        loadUsersFromDatabase();

        btnBack.setOnClickListener(v -> finish());

        buttonEditUser.setOnClickListener(v -> editUser());

        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = users.get(position);
            loadUserDataToEditTexts(selectedUser);
        });

        listViewUsers.setOnItemLongClickListener((parent, view, position, id) -> {
            User selectedUser = users.get(position);
            Intent intent = new Intent(CustomerAD.this, User_Detail.class);
            intent.putExtra("nameC", selectedUser.getNameC());
            intent.putExtra("email", selectedUser.getEmail());
            intent.putExtra("phone", selectedUser.getPhone());
            intent.putExtra("username", selectedUser.getUsername());
            intent.putExtra("password", selectedUser.getPassword());
            intent.putExtra("img_User", selectedUser.getImgUser());
            startActivity(intent);
            return true;
        });
    }

    private void loadUsersFromDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String nameC = sharedPreferences.getString("nameC", "");

        if (!nameC.isEmpty()) {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();

            Cursor cursor = database.rawQuery("SELECT * FROM Users WHERE nameC = ?", new String[]{nameC});
            if (cursor != null && cursor.moveToFirst()) {
                users.clear();
                do {
                    int customerId = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String email = cursor.getString(2);
                    String phone = cursor.getString(3);
                    String username = cursor.getString(4);
                    String password = cursor.getString(5);
                    String imgUser = cursor.getString(6);
                    int isAdmin = cursor.getInt(7);
                    User user = new User(customerId, name, email, phone, username, password, imgUser, isAdmin);
                    users.add(user);
                } while (cursor.moveToNext());

                cursor.close();
                database.close();
                userAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private boolean isValidEmail(String email) {
        return email.endsWith("@gmail.com");
    }

    private boolean isUsernameTaken(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void editUser() {
        String nameC = editTextNameC.getText().toString();
        String email = editTextEmail.getText().toString();
        String phone = editTextPhone.getText().toString();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String imgUser = editTextImage.getText().toString();

        if (!isValidPhone(phone)) {
            showToast("Phone number must be 10 digits and contain no letters or special characters.");
            return;
        }

        if (!isValidEmail(email)) {
            showToast("Email must end with @gmail.com.");
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nameC", nameC);
        values.put("email", email);
        values.put("phone", phone);
        values.put("username", username);
        values.put("password", password);
        values.put("img_User", imgUser);

        int rowsAffected = database.update("Users", values, "nameC = ?", new String[]{nameC});
        database.close();

        if (rowsAffected > 0) {
            for (User user : users) {
                if (user.getNameC().equals(nameC)) {
                    user.setNameC(nameC);
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setImgUser(imgUser);
                    break;
                }
            }
            userAdapter.notifyDataSetChanged();
            showToast("User updated successfully");
            clearEditTexts();
        } else {
            showToast("Error updating user");
        }
    }

    private void loadUserDataToEditTexts(User user) {
        editTextNameC.setText(user.getNameC());
        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(user.getPhone());
        editTextUsername.setText(user.getUsername());
        editTextPassword.setText(user.getPassword());
        editTextImage.setText(user.getImgUser());
    }

    private void clearEditTexts() {
        editTextNameC.setText("");
        editTextEmail.setText("");
        editTextPhone.setText("");
        editTextUsername.setText("");
        editTextPassword.setText("");
        editTextImage.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("nameC", editTextNameC.getText().toString());
        resultIntent.putExtra("phone", editTextPhone.getText().toString());
        resultIntent.putExtra("email", editTextEmail.getText().toString());
        resultIntent.putExtra("username", editTextUsername.getText().toString());
        resultIntent.putExtra("password", editTextPassword.getText().toString());
        resultIntent.putExtra("imageResName", editTextImage.getText().toString());
        setResult(AppCompatActivity.RESULT_OK, resultIntent);

        super.onBackPressed();
    }
}
