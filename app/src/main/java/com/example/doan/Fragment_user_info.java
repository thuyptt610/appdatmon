package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_user_info#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_user_info extends Fragment {
    private TextView nameCTextView, phoneTextView, emailTextView, usernameTextView, passwordTextView;
    private ImageView profileImageView;
    Button btnLogout, btnRefresh;

    private static final int REQUEST_CODE = 1;

    public Fragment_user_info() {
        // Required empty public constructor
    }

    public static Fragment_user_info newInstance(String nameC, String phone, String email, String username, String password, String imageResName, int customerId) {
        Fragment_user_info fragment = new Fragment_user_info();
        Bundle args = new Bundle();
        args.putString("nameC", nameC);
        args.putString("phone", phone);
        args.putString("email", email);
        args.putString("username", username);
        args.putString("password", password);
        args.putString("imageResName", imageResName);
        args.putInt("customerid", customerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        nameCTextView = view.findViewById(R.id.tvFullNameinf);
        phoneTextView = view.findViewById(R.id.tvPhoneinf);
        emailTextView = view.findViewById(R.id.tvEmailinf);
        usernameTextView = view.findViewById(R.id.tvUsernameinf);
        passwordTextView = view.findViewById(R.id.tvPassWordinf);
        profileImageView = view.findViewById(R.id.imageHinhAnhinf);
        btnLogout = view.findViewById(R.id.btnLogout);
       // btnRefresh = view.findViewById(R.id.btnRefresh);

        if (getArguments() != null) {
            updateUIFromArguments();
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });

     /*   btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshUserInfoFromDatabase();
            }
        });*/


        ImageView addUserImageView = view.findViewById(R.id.addUserImageView);
        addUserImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomerAD.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return view;
    }
// //////////////////////////////////Refresh
        private void updateUIFromArguments() {
        String nameC = getArguments().getString("nameC");
        String phone = getArguments().getString("phone");
        String email = getArguments().getString("email");
        String username = getArguments().getString("username");
        String password = getArguments().getString("password");
        String imageResName = getArguments().getString("imageResName");

        nameCTextView.setText(nameC);
        phoneTextView.setText(phone);
        emailTextView.setText(email);
        usernameTextView.setText(username);
        passwordTextView.setText(password);

        int imageResId = getResources().getIdentifier(imageResName, "drawable", getActivity().getPackageName());
        profileImageView.setImageResource(imageResId);
    }

    private void refreshUserInfoFromDatabase() {
        if (getArguments() != null) {
            int customerId = getArguments().getInt("customerid");
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            User user = dbHelper.getUserById(customerId);

            if (user != null) {
                // Cập nhật thông tin UI từ dữ liệu User
                nameCTextView.setText(user.getNameC());
                phoneTextView.setText(user.getPhone());
                emailTextView.setText(user.getEmail());
                usernameTextView.setText(user.getUsername());
                passwordTextView.setText(user.getPassword());

                int imageResId = getResources().getIdentifier(user.getImgUser(), "drawable", getActivity().getPackageName());
                profileImageView.setImageResource(imageResId);
            } else {
                Log.e("Fragment_user_info", "Không tìm thấy người dùng với customerId: " + customerId);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                String nameC = data.getStringExtra("nameC");
                String phone = data.getStringExtra("phone");
                String email = data.getStringExtra("email");
                String username = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                String imageResName = data.getStringExtra("imageResName");

                nameCTextView.setText(nameC);
                phoneTextView.setText(phone);
                emailTextView.setText(email);
                usernameTextView.setText(username);
                passwordTextView.setText(password);

                int imageResId = getResources().getIdentifier(imageResName, "drawable", getActivity().getPackageName());
                profileImageView.setImageResource(imageResId);
            }
        }
    }
}

