package com.example.jbsestacionamento;

import android.content.Context; // Importar Context
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.RenderEffect;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VeiculoAdapter extends RecyclerView.Adapter<VeiculoAdapter.VeiculoViewHolder> {

    private List<Veiculo> veiculos;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Veiculo veiculo);
    }

    public VeiculoAdapter(List<Veiculo> veiculos, Context context, OnItemClickListener listener) {
        this.veiculos = veiculos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VeiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new VeiculoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VeiculoViewHolder holder, int position) {
        Veiculo veiculo = veiculos.get(position);
        holder.bind(veiculo);

        // listener de clique para o CardView inteiro (itemView)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // mnotifica a Activity (ou quem estiver implementando a interface)
                // qual veículo foi clicado.
                listener.onItemClick(veiculo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return veiculos.size();
    }

    public void updateList(List<Veiculo> newList) {
        veiculos = newList;
        notifyDataSetChanged();
    }

    static class VeiculoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaca;
        TextView tvEntrada;
        TextView tvSaida;

        public VeiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaca = itemView.findViewById(R.id.txt_placa);
            tvEntrada = itemView.findViewById(R.id.txt_entrada);
            tvSaida = itemView.findViewById(R.id.txt_saida);
        }

        public void bind(Veiculo veiculo) {
            tvPlaca.setText("Veículo: " + veiculo.getPlaca());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH'h'mm");
            tvEntrada.setText("Entrada: " + veiculo.getEntrada().format(formatter));

            if (veiculo.getSaida() != null) {
                tvSaida.setText("Saída: " + veiculo.getSaida().format(formatter));
            } else {
                tvSaida.setText("Saída: --h--");
            }
        }
    }
}