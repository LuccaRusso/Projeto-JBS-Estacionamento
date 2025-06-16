package com.example.jbsestacionamento;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements VeiculoAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private VeiculoAdapter veiculoAdapter;
    private List<Veiculo> todosVeiculos;

    private MaterialButton btnTodos;
    private MaterialButton btnEntrou;
    private MaterialButton btnSaiu;
    private int btnSelecionado = 0;

    private EditText searchBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_id_home, container, false); // Certifique-se de renomear seu XML para fragment_home.xml
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        searchBar = view.findViewById(R.id.search_bar);
        btnTodos = view.findViewById(R.id.btn_all);
        btnEntrou = view.findViewById(R.id.btn_entered);
        btnSaiu = view.findViewById(R.id.btn_exited);

        todosVeiculos = geraVeiculos();

        veiculoAdapter = new VeiculoAdapter(todosVeiculos, requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(veiculoAdapter);

        btnTodos.setOnClickListener(v -> {
            veiculoAdapter.updateList(todosVeiculos);
            setSelectedButton(btnTodos);
        });

        btnEntrou.setOnClickListener(v -> {
            List<Veiculo> entrou = new ArrayList<>();
            for (Veiculo veiculo : todosVeiculos) {
                if (veiculo.getSaida() == null) {
                    entrou.add(veiculo);
                }
            }
            veiculoAdapter.updateList(entrou);
            setSelectedButton(btnEntrou);
        });

        btnSaiu.setOnClickListener(v -> {
            List<Veiculo> saiu = new ArrayList<>();
            for (Veiculo veiculo : todosVeiculos) {
                if (veiculo.getSaida() != null) {
                    saiu.add(veiculo);
                }
            }
            veiculoAdapter.updateList(saiu);
            setSelectedButton(btnSaiu);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                buscaPlaca();
            }
        });

        setSelectedButton(btnTodos);
    }

    private void setSelectedButton(MaterialButton selectedButton) {
        MaterialButton[] allButtons = {btnTodos, btnEntrou, btnSaiu};
        for (MaterialButton button : allButtons) {
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.unselected_item_color));
            button.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.unselected_text_color));
        }

        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selected_item_color));
        selectedButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.selected_text_color));
        btnSelecionado = getAtual(selectedButton);
    }

    @Override
    public void onItemClick(Veiculo veiculoClicado) {
        if (veiculoClicado.getSaida() == null) {
            showRegisterExitDialog(veiculoClicado);
        } else {
            Toast.makeText(requireContext(), "Veículo " + veiculoClicado.getPlaca() + " já registrou saída.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRegisterEntryDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_entry);

        TextInputEditText editTextPlate = dialog.findViewById(R.id.edit_text_plate);
        MaterialButton buttonSaveEntry = dialog.findViewById(R.id.button_save_entry);

        buttonSaveEntry.setOnClickListener(v -> {
            String plate = editTextPlate.getText().toString().trim().toUpperCase();

            if (plate.isEmpty() || plate.length() != 7) {
                editTextPlate.setError("A placa deve ter 7 caracteres (ex: ABC1234).");
                return;
            }

            for (Veiculo veiculo : todosVeiculos) {
                if (veiculo.getPlaca().equalsIgnoreCase(plate) && veiculo.getSaida() == null) {
                    Toast.makeText(requireContext(), "Veículo " + plate + " já está no estacionamento.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            todosVeiculos.add(0, new Veiculo(plate, LocalDateTime.now(), null));
            veiculoAdapter.updateList(getVeciulos(btnSelecionado));
            dialog.dismiss();
            Toast.makeText(requireContext(), "Veículo " + plate + " registrado com sucesso!", Toast.LENGTH_SHORT).show();
        });

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
    }

    private void showRegisterExitDialog(Veiculo veiculoToExit) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_exit);

        TextView tvExitPrompt = dialog.findViewById(R.id.text_view_exit_prompt);
        MaterialButton buttonYesExit = dialog.findViewById(R.id.button_yes_exit);
        MaterialButton buttonNoExit = dialog.findViewById(R.id.button_no_exit);

        tvExitPrompt.setText("Deseja registrar a saída do veículo " + veiculoToExit.getPlaca() + "?");

        buttonYesExit.setOnClickListener(v -> {
            veiculoToExit.setSaida(LocalDateTime.now());
            veiculoAdapter.updateList(getVeciulos(btnSelecionado));
            dialog.dismiss();
            Toast.makeText(requireContext(), "Saída de " + veiculoToExit.getPlaca() + " registrada com sucesso!", Toast.LENGTH_SHORT).show();
        });

        buttonNoExit.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
    }

    private List<Veiculo> geraVeiculos() {
        List<Veiculo> veiculos = new ArrayList<>();
        veiculos.add(new Veiculo("GIULIA", LocalDateTime.of(2025, 6, 6, 12, 45), LocalDateTime.of(2025, 6, 6, 14, 45)));
        veiculos.add(new Veiculo("NATI123", LocalDateTime.of(2025, 6, 6, 13, 10), null));
        veiculos.add(new Veiculo("JEFF123", LocalDateTime.of(2025, 6, 6, 11, 30), null));
        veiculos.add(new Veiculo("LUCCA1", LocalDateTime.of(2025, 6, 6, 10, 0), LocalDateTime.of(2025, 6, 6, 10, 50)));
        veiculos.add(new Veiculo("DANI123", LocalDateTime.of(2025, 6, 6, 9, 15), null));
        return veiculos;
    }

    private List<Veiculo> getVeciulos(int filtro) {
        List<Veiculo> veiculosFiltrados = new ArrayList<>();
        for (Veiculo veiculo : todosVeiculos) {
            if (filtro == 0 || (filtro == 1 && veiculo.getSaida() == null) || (filtro == 2 && veiculo.getSaida() != null)) {
                veiculosFiltrados.add(veiculo);
            }
        }
        return veiculosFiltrados;
    }

    private int getAtual(MaterialButton button) {
        if (button == btnTodos) return 0;
        else if (button == btnEntrou) return 1;
        else return 2;
    }

    private void buscaPlaca() {
        String query = searchBar.getText().toString().trim().toUpperCase();
        List<Veiculo> listaFiltrada = new ArrayList<>();
        for (Veiculo veiculo : getVeciulos(btnSelecionado)) {
            if (veiculo.getPlaca().toUpperCase().contains(query)) {
                listaFiltrada.add(veiculo);
            }
        }
        veiculoAdapter.updateList(listaFiltrada);
    }
}
