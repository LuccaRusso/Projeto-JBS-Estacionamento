package com.example.jbsestacionamento.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jbsestacionamento.R;
import com.example.jbsestacionamento.data.model.User;
import com.example.jbsestacionamento.data.model.UserDao;
import com.example.jbsestacionamento.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        requireActivity().setTitle("");
        setHasOptionsMenu(true);
        binding = FragmentSignUpBinding.inflate(inflater, container, false);

        binding.loginbtn.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(SignUpFragment.this);
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText emailEditText = binding.email;
        EditText usernameEditText = binding.username;
        EditText passwordEditText = binding.password;

        userDao = new UserDao();

        binding.signup.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(email, password, username);
            userDao.registerUser(user, requireContext(), getParentFragmentManager());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
