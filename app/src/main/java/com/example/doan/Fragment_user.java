package com.example.doan;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_user extends Fragment {
    private ArrayList<User> users;
    private UserAdapter userAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_user() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_user.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_user newInstance(String param1, String param2) {
        Fragment_user fragment = new Fragment_user();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        ListView listViewUsers = view.findViewById(R.id.listViewUsers);
        users = new ArrayList<>();
        userAdapter = new UserAdapter(getActivity(), users);
        listViewUsers.setAdapter(userAdapter);

        loadUsersFromDatabase();

        EditText editTextUserId = view.findViewById(R.id.editTextUserId);
        Button buttonDeleteUser = view.findViewById(R.id.buttonDeleteUser);
        Button buttonAddUser = view.findViewById(R.id.buttonAddUser);
        Button buttonEditUser = view.findViewById(R.id.buttonEditUser);

        buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idToDelete = editTextUserId.getText().toString();
                deleteUserFromDatabase(idToDelete);
            }
        });

        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewUser();
            }
        });

        buttonEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUser();
            }
        });
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = users.get(position);
                loadUserDataToEditTexts(selectedUser);
            }
        });
        listViewUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = users.get(position);
                Intent intent = new Intent(getActivity(), User_Detail.class);
                intent.putExtra("nameC", selectedUser.getNameC());
                intent.putExtra("email", selectedUser.getEmail());
                intent.putExtra("phone", selectedUser.getPhone());
                intent.putExtra("username", selectedUser.getUsername());
                intent.putExtra("password", selectedUser.getPassword());
                intent.putExtra("img_User", selectedUser.getImgUser());
                startActivity(intent);
                return true;
            }
        });


        return view;
    }

    private void loadUsersFromDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM Users", null);
        if (cursor != null && cursor.moveToFirst()) {
            users.clear();
            do {
                int customerId = cursor.getInt(0);
                String nameC = cursor.getString(1);
                String email = cursor.getString(2);
                String phone = cursor.getString(3);
                String username = cursor.getString(4);
                String password = cursor.getString(5);
                String imgUser = cursor.getString(6);
                int isAdmin = cursor.getInt(7);
                User user = new User(customerId, nameC, email, phone, username, password, imgUser, isAdmin);
                users.add(user);
            } while (cursor.moveToNext());

            cursor.close();
            database.close();
            userAdapter.notifyDataSetChanged();
        }
    }
    private void deleteUserFromDatabase(String userId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.delete("Users", "customerid = ?", new String[]{userId});

        database.close();

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getCustomerId() == Integer.parseInt(userId)) {
                users.remove(i);
                break;
            }
        }

        userAdapter.notifyDataSetChanged();
        clearEditTexts();
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void addNewUser() {
        EditText editTextUserId = getView().findViewById(R.id.editTextUserId);
        EditText editTextNameC = getView().findViewById(R.id.editTextNameC);
        EditText editTextEmail = getView().findViewById(R.id.editTextEmail);
        EditText editTextPhone = getView().findViewById(R.id.editTextPhone);
        EditText editTextUsername = getView().findViewById(R.id.editTextUsername);
        EditText editTextPassword = getView().findViewById(R.id.editTextPassword);
        EditText editTextImage = getView().findViewById(R.id.editTextImage);
        EditText editTextIsAdmin = getView().findViewById(R.id.editTextIsAdmin);

        String userId = editTextUserId.getText().toString();
        String nameC = editTextNameC.getText().toString();
        String email = editTextEmail.getText().toString();
        String phone = editTextPhone.getText().toString();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String imgUser = editTextImage.getText().toString();
        int isAdmin = Integer.parseInt(editTextIsAdmin.getText().toString());

        if (!isValidPhone(phone)) {
            showToast("Phone number must be 10 digits and contain no letters or special characters.");
            return;
        }

        if (!isValidEmail(email)) {
            showToast("Email must end with @gmail.com.");
            return;
        }

        if (isUsernameTaken(username)) {
            showToast("Username is already taken. Please choose another one.");
            return;
        }
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nameC", nameC);
        values.put("email", email);
        values.put("phone", phone);
        values.put("username", username);
        values.put("password", password);
        values.put("img_User", imgUser);
        values.put("is_admin", isAdmin);

        long newRowId = database.insert("Users", null, values);

        database.close();

        if (newRowId != -1) {
            User newUser = new User((int) newRowId, nameC, email, phone, username, password, imgUser, isAdmin);
            users.add(newUser);
            userAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "User added successfully", Toast.LENGTH_SHORT).show();
            clearEditTexts();
        } else {
            Toast.makeText(getActivity(), "Error adding user", Toast.LENGTH_SHORT).show();
        }
    }

    private void editUser() {
        EditText editTextUserId = getView().findViewById(R.id.editTextUserId);
        EditText editTextNameC = getView().findViewById(R.id.editTextNameC);
        EditText editTextEmail = getView().findViewById(R.id.editTextEmail);
        EditText editTextPhone = getView().findViewById(R.id.editTextPhone);
        EditText editTextUsername = getView().findViewById(R.id.editTextUsername);
        EditText editTextPassword = getView().findViewById(R.id.editTextPassword);
        EditText editTextImage = getView().findViewById(R.id.editTextImage);
        EditText editTextIsAdmin = getView().findViewById(R.id.editTextIsAdmin);

        String userId = editTextUserId.getText().toString();
        String nameC = editTextNameC.getText().toString();
        String email = editTextEmail.getText().toString();
        String phone = editTextPhone.getText().toString();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String imgUser = editTextImage.getText().toString();
        int isAdmin = Integer.parseInt(editTextIsAdmin.getText().toString());

        if (!isValidPhone(phone)) {
            showToast("Phone number must be 10 digits and contain no letters or special characters.");
            return;
        }

        if (!isValidEmail(email)) {
            showToast("Email must end with @gmail.com.");
            return;
        }
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nameC", nameC);
        values.put("email", email);
        values.put("phone", phone);
        values.put("username", username);
        values.put("password", password);
        values.put("img_User", imgUser);
        values.put("is_admin", isAdmin);

        int rowsAffected = database.update("Users", values, "customerid = ?", new String[]{userId});

        database.close();

        if (rowsAffected > 0) {
            for (User user : users) {
                if (user.getCustomerId() == Integer.parseInt(userId)) {
                    user.setNameC(nameC);
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setImgUser(imgUser);
                    user.setIsAdmin(isAdmin);
                    break;
                }
            }
            userAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "User updated successfully", Toast.LENGTH_SHORT).show();
            clearEditTexts();
        } else {
            Toast.makeText(getActivity(), "Error updating user", Toast.LENGTH_SHORT).show();
        }
    }
    private void clearEditTexts() {
        EditText editTextUserId = getView().findViewById(R.id.editTextUserId);
        EditText editTextNameC = getView().findViewById(R.id.editTextNameC);
        EditText editTextEmail = getView().findViewById(R.id.editTextEmail);
        EditText editTextPhone = getView().findViewById(R.id.editTextPhone);
        EditText editTextUsername = getView().findViewById(R.id.editTextUsername);
        EditText editTextPassword = getView().findViewById(R.id.editTextPassword);
        EditText editTextImage = getView().findViewById(R.id.editTextImage);
        EditText editTextIsAdmin = getView().findViewById(R.id.editTextIsAdmin);

        editTextUserId.setText("");
        editTextNameC.setText("");
        editTextEmail.setText("");
        editTextPhone.setText("");
        editTextUsername.setText("");
        editTextPassword.setText("");
        editTextImage.setText("");
        editTextIsAdmin.setText("");
    }
    private void loadUserDataToEditTexts(User user) {
        EditText editTextUserId = getView().findViewById(R.id.editTextUserId);
        EditText editTextNameC = getView().findViewById(R.id.editTextNameC);
        EditText editTextEmail = getView().findViewById(R.id.editTextEmail);
        EditText editTextPhone = getView().findViewById(R.id.editTextPhone);
        EditText editTextUsername = getView().findViewById(R.id.editTextUsername);
        EditText editTextPassword = getView().findViewById(R.id.editTextPassword);
        EditText editTextImage = getView().findViewById(R.id.editTextImage);
        EditText editTextIsAdmin = getView().findViewById(R.id.editTextIsAdmin);

        editTextUserId.setText(String.valueOf(user.getCustomerId()));
        editTextNameC.setText(user.getNameC());
        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(user.getPhone());
        editTextUsername.setText(user.getUsername());
        editTextPassword.setText(user.getPassword());
        editTextImage.setText(user.getImgUser());
        editTextIsAdmin.setText(String.valueOf(user.getIsAdmin()));
    }

}
