package com.example.jbsestacionamento;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jbsestacionamento.data.model.User;
import com.example.jbsestacionamento.data.model.UserDao;

public class SingUp extends AppCompatActivity {

    private EditText emailEditText, usernameEditText, passwordEditText;
    private Button signupButton;
    private TextView loginButton;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sing_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.singupActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializa os componentes com findViewById
        emailEditText = findViewById(R.id.emailSingup);
        usernameEditText = findViewById(R.id.usernameSingup);
        passwordEditText = findViewById(R.id.passwordSingup);
        signupButton = findViewById(R.id.signup);
        loginButton = findViewById(R.id.loginbtn);

        userDao = new UserDao();

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SingUp.this, Login.class);
            startActivity(intent);
            finish();
        });

        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(email, password, username);
            userDao.registerUser(user, this);
        });
    }
}
