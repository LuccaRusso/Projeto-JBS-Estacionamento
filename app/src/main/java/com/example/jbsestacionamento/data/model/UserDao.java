package com.example.jbsestacionamento.data.model;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.jbsestacionamento.FirstFragment;
import com.example.jbsestacionamento.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
            database.collection("Usuarios").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        Toast.makeText(context,"Esse email já possui cadastro", Toast.LENGTH_SHORT).show();
                    }
                    database.collection("Usuarios").document("contador").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            int id = 1;
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    id = doc.getLong("id").intValue() + 1;
                                }
                            }
                            user.setId(id);
                            user.setPassword(BCrypt.hashpw(user.getPassword(),BCrypt.gensalt()));
                            user.setAdmin(false);

                            Map<String, Object> contadorAtualizado = new HashMap<>();
                            contadorAtualizado.put("id", id);

                            database.collection("Usuarios").document("contador").set(contadorAtualizado);
                            database.collection("Usuarios").document(String.valueOf(id)).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Usuário salvo com sucesso", Toast.LENGTH_SHORT).show();

                                        //Fragment home = new HomeFragment;

                                        //fragmentManager.beginTransaction()
                                        //        .replace(R.id.nav_host_fragment, home)
                                        //        .addToBackStack(null)
                                        //        .commit();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(context, "Erro ao salvar", Toast.LENGTH_SHORT).show());
                        }
                    });

                }else {
                    Toast.makeText(context, "Erro ao verificar email", Toast.LENGTH_SHORT).show();
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

                        if (user.getAdmin()) {
                            // Exemplo: navegar para tela de Admin
//                            fragmentManager.beginTransaction()
//                                     .replace(R.id.nav_host_fragment, new AdminFragment())
//                                     .addToBackStack(null)
//                                     .commit();
                        } else {
                            fragmentManager.beginTransaction()
                                    .replace(R.id.nav_graph, new FirstFragment())
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
