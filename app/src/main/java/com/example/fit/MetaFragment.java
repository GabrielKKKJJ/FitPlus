package com.example.fit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaFragment extends Fragment implements ContainerHandler {
    private Database db;
    private String user;
    private Map<String, View> containerMap = new HashMap<>();

    public MetaFragment(Database db, String user) {
        // Required empty public constructor
        this.db = db;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meta, container, false);

        Button addMeta = rootView.findViewById(R.id.addMeta);

        addMeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog dialog = new addDialog(db, user);
                dialog.setTargetFragment(MetaFragment.this, 0);
                dialog.show(getParentFragmentManager(), "Dialog");
            }
        });
        db.getCollection(user, "metas").observe(getViewLifecycleOwner(), documents -> {
            update(documents);
        });

        return rootView;
    }

    @Override
    public void addContainer(String documentId, String nomeAtividade ,String atividade, int repeticoes, int km, int tempo, int calorias) {}

    @Override
    public void addContainer(String documentId, String nomeAtividade, String tipoMeta, int repeticoes, int km, int tempo, int calorias, int progresso, int progressoMaximo) {
        LinearLayout metaContainer = getView().findViewById(R.id.metaContainer);

        // Infla o layout da meta
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View meta_component = inflater.inflate(R.layout.meta_component, metaContainer, false);

        PagesActivity.setText(meta_component, R.id.nomeAtividade, nomeAtividade);

        // barra de progresso
        ProgressBar progressBar = meta_component.findViewById(R.id.progressBar);
        int currentProgress = getProgresso(progresso, progressoMaximo);

        if (currentProgress >= 100) {
            meta_component.setBackground(getResources().getDrawable(R.drawable.completed_meta));
        }
        progressBar.setProgress(currentProgress);
        progressBar.setMax(100);

        PagesActivity.setText(meta_component, R.id.barPercentage, currentProgress+"%");

        // Armazenando informações do container usando setTag
        AtividadeInfo info = new AtividadeInfo(documentId, nomeAtividade, tipoMeta, repeticoes, km, tempo, calorias);
        Log.d("AtividadeInfo", "DocumentID: " + documentId);
        meta_component.setTag(info);

        meta_component.setOnClickListener(v -> {
            AtividadeInfo clickedInfo = (AtividadeInfo) v.getTag();

            Bundle bundle = new Bundle();
            bundle.putString("documentId", clickedInfo.getDocumentID());
            bundle.putString("nomeAtividade", clickedInfo.getnomeAtividade());
            bundle.putString("atividade", clickedInfo.getAtividade());
            bundle.putInt("repeticoes", clickedInfo.getRepeticoes());
            bundle.putInt("km", clickedInfo.getKm());
            bundle.putInt("tempo", clickedInfo.getTempo());
            bundle.putInt("calorias", clickedInfo.getCalorias());

            containerDialog dialog = new containerDialog(db, user);
            dialog.setArguments(bundle);
            dialog.setTargetFragment(MetaFragment.this, 0);
            dialog.show(getParentFragmentManager(), "Dialog");

            // Aqui você pode fazer qualquer outra coisa com as informações recuperadas
        });
        containerMap.put(documentId, meta_component);

        // Adicionando o TextView ao LinearLayout
        metaContainer.addView(meta_component);
    }

    @Override
    public void removeContainer(String documentId) {
        LinearLayout metaContainer = requireView().findViewById(R.id.metaContainer);
        View containerToRemove = containerMap.get(documentId);

        if (containerToRemove != null) {
            metaContainer.removeView(containerToRemove);
            containerMap.remove(documentId);
        } else {
            Log.e("AtividadesFragment", "Container with documentId " + documentId + " not found");
        }
    }

    public void update(List<Map<String, Object>> documentsList) {
        if (documentsList != null && !documentsList.isEmpty()) {
            for (Map<String, Object> data : documentsList) {
                if (data != null) {
                    // Extraindo os dados do mapa
                    String documentId = (String) data.get("documentId");
                    String nomeAtividade = (String) data.get("nomeAtividade");
                    String tipoMeta = (String) data.get("tipoMeta");
                    int repeticoes = Integer.parseInt(String.valueOf(data.getOrDefault("repeticoes", "0")));
                    int km = Integer.parseInt(String.valueOf(data.getOrDefault("km", "0")));
                    int tempo = Integer.parseInt(String.valueOf(data.getOrDefault("tempo", "0")));
                    int calorias = Integer.parseInt(String.valueOf(data.getOrDefault("calorias", "0")));
                    int progresso = Integer.parseInt(String.valueOf(data.getOrDefault("progresso", "0")));
                    int progressoMaximo = Integer.parseInt(String.valueOf(data.getOrDefault("progressoMaximo", "0")));

                    addContainer(documentId, nomeAtividade, tipoMeta, repeticoes, km, tempo, calorias, progresso, progressoMaximo);
                }
            }
        } else {
            Log.d("MetaFragment", "Lista de documentos está vazia ou é nula.");
        }
    }

    public static int getProgresso(int progresso, int progressoMaximo) {
        if (progressoMaximo == 0) {
            return 0;
        } else {
            int porcentagem = (int) ((float) progresso / (float) progressoMaximo * 100);
            if (porcentagem > 100) {
                return 100;
            } else {
                return (int) ((float) progresso / (float) progressoMaximo * 100);
            }
        }
    }

}