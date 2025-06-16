package com.example.jbsestacionamento;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.jbsestacionamento.databinding.FragmentPerfilBinding;
import com.example.jbsestacionamento.data.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil extends Fragment {

    FragmentPerfilBinding binding;
    private User usuarioAtual;

    private FragmentManager fragmentManager;

    public Perfil() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            usuarioAtual = (User) getArguments().getSerializable("usuario");
        }

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (usuarioAtual != null) {
            binding.nomePerfil.setText(usuarioAtual.getName());
            binding.emailPerfil.setText(usuarioAtual.getEmail());
        } else {
            binding.nomePerfil.setText("Nome");
            binding.emailPerfil.setText("E-mail");
        }

        binding.editarNome.setOnClickListener(v -> {
            EditText nome = new EditText(getContext());
            nome.setText(usuarioAtual.getName());

            new AlertDialog.Builder(getContext())
                    .setTitle("Editar Nome")
                    .setView(nome)
                    .setPositiveButton("Salvar", (dialog, which) -> {
                        String novoNome = nome.getText().toString();
                        if (!novoNome.isEmpty()) {
                            usuarioAtual.setName(novoNome);
                            binding.nomePerfil.setText(novoNome);

                            FirebaseFirestore.getInstance()
                                .collection("Usuarios")
                                .document(String.valueOf(usuarioAtual.getId()))
                                .update("name", novoNome);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        binding.switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.emailPerfil.setVisibility(View.VISIBLE);
            } else {
                binding.emailPerfil.setVisibility(View.INVISIBLE);
            }
        });

        binding.btnRedireCadastrar.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(
                    fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main));
            navController.navigate(R.id.action_perfil_to_homeFragment);
        });

        binding.voltarPerfil.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(
                    fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main));
            navController.navigate(R.id.action_perfil_to_homeFragment);
        });

        binding.cardSair.setOnClickListener(v -> {
            usuarioAtual = null;

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}