package com.example.fit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtividadesFragment extends Fragment implements ContainerHandler {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Database db;
    private String user;
    private Map<String, View> containerMap = new HashMap<>();

    public AtividadesFragment(Database db, String user) {
        this.db = db;
        this.user = user;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_atividades, container, false);

        Button opendialog = rootView.findViewById(R.id.createActivitieBtn);
        opendialog.setOnClickListener(v -> {
            addDialog dialog = new addDialog(db, user);
            dialog.setTargetFragment(AtividadesFragment.this, 0);
            dialog.show(getParentFragmentManager(), "Dialog");
        });

        Button logout = rootView.findViewById(R.id.logoutBtn);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            reload();
        });

        db.getCollection(user, "atividades").observe(getViewLifecycleOwner(), documents -> {
            update(documents);
        });


        return rootView;
    }

    @Override
    public void addContainer(String documentId, String nomeAtividade, String atividade, int repeticoes, int km, int tempo, int calorias) {
        LinearLayout atividadesContainer = requireView().findViewById(R.id.atividadesContainer);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View atividadeContainer = inflater.inflate(R.layout.atividade_component, atividadesContainer, false);

        PagesActivity.setText(atividadeContainer, R.id.nomeAtividade, nomeAtividade);
        PagesActivity.setText(atividadeContainer, R.id.valueReps, repeticoes);
        PagesActivity.setText(atividadeContainer, R.id.valueKm, km);
        PagesActivity.setText(atividadeContainer, R.id.valueCalorias, calorias);
        PagesActivity.setText(atividadeContainer, R.id.valueTempo, tempo);

        // Armazenando informações do container usando setTag
        AtividadeInfo info = new AtividadeInfo(documentId, nomeAtividade, atividade, repeticoes, km, tempo, calorias);
        Log.d("AtividadeInfo", "DocumentID: " + documentId);
        atividadeContainer.setTag(info);

        atividadeContainer.setOnClickListener(v -> {
            AtividadeInfo clickedInfo = (AtividadeInfo) v.getTag();

            Bundle bundle = new Bundle();
            bundle.putString("documentId", clickedInfo.getDocumentID());
            bundle.putString("nomeAtividade", clickedInfo.getnomeAtividade());
            bundle.putString("atividade", clickedInfo.getAtividade());
            bundle.putInt("repeticoes", clickedInfo.getRepeticoes());
            bundle.putInt("km", clickedInfo.getKm());
            bundle.putInt("tempo", clickedInfo.getTempo());
            bundle.putInt("calorias", clickedInfo.getCalorias());

            containerDialog dialog = new containerDialog(db , user);
            dialog.setArguments(bundle);
            dialog.setTargetFragment(AtividadesFragment.this, 0);
            dialog.show(getParentFragmentManager(), "Dialog");

            // Aqui você pode fazer qualquer outra coisa com as informações recuperadas
        });
        containerMap.put(documentId, atividadeContainer);

        atividadesContainer.addView(atividadeContainer);
    }

    @Override
    public void addContainer(String documentId, String nomeAtividade, String atividade, int repeticoes, int km, int tempo, int calorias, int progresso, int progressoMaximo) {

    }

    @Override
    public void removeContainer(String documentId) {
        LinearLayout atividadesContainer = requireView().findViewById(R.id.atividadesContainer);
        View containerToRemove = containerMap.get(documentId);

        if (containerToRemove != null) {
            atividadesContainer.removeView(containerToRemove);
            containerMap.remove(documentId);
        } else {
            Log.e("AtividadesFragment", "Container with documentId " + documentId + " not found");
        }
    }

    private void reload() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    public void update(List<Map<String, Object>> documentsList) {
        if (documentsList != null && !documentsList.isEmpty()) {
            for (Map<String, Object> data : documentsList) {
                if (data != null) {
                    // Extraindo os dados do mapa
                    String documentID = (String) data.get("documentId");
                    String nomeAtividade = (String) data.get("nomeAtividade");
                    String tipoAtividade = (String) data.get("tipoAtividade");
                    int repeticoes = Integer.parseInt(String.valueOf(data.getOrDefault("repeticoes", "0")));
                    int km = Integer.parseInt(String.valueOf(data.getOrDefault("km", "0")));
                    int tempo = Integer.parseInt(String.valueOf(data.getOrDefault("tempo", "0")));
                    int calorias = Integer.parseInt(String.valueOf(data.getOrDefault("calorias", "0")));

                    addContainer(documentID, nomeAtividade, tipoAtividade, repeticoes, km, tempo, calorias);
                }
            }
        } else {
            Log.d("AtividadesFragment", "Lista de documentos está vazia ou é nula.");
        }
    }
}
