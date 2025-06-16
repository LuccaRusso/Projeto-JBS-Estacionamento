package com.example.jbsestacionamento.data.model;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.jbsestacionamento.Home;
import com.example.jbsestacionamento.Perfil;
import com.example.jbsestacionamento.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class UserDao {

    private final FirebaseFirestore database;

    public UserDao() {
        this.database = FirebaseFirestore.getInstance();
    }

    public void registerUser(User user, Context context, FragmentManager fragmentManager) {
        if (user.getId() == 0) {
            database.collection("Usuarios").whereEqualTo("email", user.getEmail()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Toast.makeText(context, "Esse email já possui cadastro", Toast.LENGTH_SHORT).show();
                                return; // Sai do método se email já existe
                            }

                            // Continua com o cadastro se email não existe
                            database.collection("Usuarios").document("contador").get()
                                    .addOnCompleteListener(contadorTask -> {
                                        int id = 1;
                                        if (contadorTask.isSuccessful() && contadorTask.getResult().exists()) {
                                            id = contadorTask.getResult().getLong("id").intValue() + 1;
                                        }

                                        user.setId(id);
                                        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                                        user.setAdmin(false);

                                        Map<String, Object> contadorAtualizado = new HashMap<>();
                                        contadorAtualizado.put("id", id);

                                        // Atualiza contador e cadastra usuário
                                        database.collection("Usuarios").document("contador").set(contadorAtualizado);
                                        database.collection("Usuarios").document(String.valueOf(id)).set(user)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(context, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                                                    // Navega para a tela de login
                                                    NavController navController = NavHostFragment.findNavController(
                                                            fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main));
                                                    navController.navigate(R.id.action_signUpFragment_to_loginFragment);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(context, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                                                    Log.e("FirestoreError", "Erro ao cadastrar", e);
                                                });
                                    });
                        } else {
                            Toast.makeText(context, "Erro ao verificar email", Toast.LENGTH_SHORT).show();
                            Log.e("FirestoreError", "Erro ao verificar email", task.getException());
                        }
                    });
        }
    }
    public void loginUser(String email, String password, Context context, FragmentManager fragmentManager) {

        database.collection("Usuarios").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    User user = doc.toObject(User.class);

                    if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                        Toast.makeText(context, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("usuario", user);

                        Fragment perfilFragment = new Perfil();
                        perfilFragment.setArguments(bundle); // Passando o usuário para o fragment de perfil

                        if (user.getAdmin()) {
                            // Exemplo: navegar para tela de Admin
//                            fragmentManager.beginTransaction()
//                                     .replace(R.id.nav_host_fragment, new AdminFragment())
//                                     .addToBackStack(null)
//                                     .commit();
                        } else {
                            fragmentManager.beginTransaction()
                                    .replace(R.id.nav_graph, new Home())
                                    .addToBackStack(null)
                                    .commit();
                        }

                    } else {
                        Toast.makeText(context, "Senha incorreta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(context, "Erro ao consultar o banco", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
