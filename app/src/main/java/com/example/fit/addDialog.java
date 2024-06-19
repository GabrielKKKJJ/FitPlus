package com.example.fit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addDialog extends DialogFragment {
    private Database db;
    private ContainerHandler containerHandler;
    private Map<Integer, String> documentIdMap = new HashMap<>();
    private Map<Integer, String> tipoAtividadeMap = new HashMap<>();
    private  String user;

    public addDialog(Database db, String user) {
        this.db = db;
        this.user = user;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            containerHandler = (ContainerHandler) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddContainerHandler");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getDialog().getWindow().getDecorView();
        if(getTargetFragment() instanceof AtividadesFragment) {
            getDocs(view);
        } else if (getTargetFragment() instanceof  MetaFragment) {
            view.findViewById(R.id.metaLabel).setVisibility(View.GONE);
            view.findViewById(R.id.metaSpinner).setVisibility(View.GONE);
        }
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_dialog, null);

        Spinner atividadeSpinner = view.findViewById(R.id.tipoAtividadeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.Opcoes, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        atividadeSpinner.setAdapter(adapter);

        ArrayAdapter<String> metaAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item);
        metaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metaAdapter.add("N/A");

        Spinner metaSpinner = view.findViewById(R.id.metaSpinner);
        metaSpinner.setAdapter(metaAdapter);

        builder.setView(view)
                .setPositiveButton("Concluir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String txtnomeAtividade = getTextFromView(R.id.nomeAtividade).trim();
                        String txtValueTipoAtividade = atividadeSpinner.getSelectedItem().toString();

                        int valueRepeticoes = parseIntOrZero(getTextFromView(R.id.txtValueRepeticoes));
                        int valueKM = parseIntOrZero(getTextFromView(R.id.txtValueKm));
                        int valueTempo = parseIntOrZero(getTextFromView(R.id.txtValueTempo));
                        int valueCalorias = parseIntOrZero(getTextFromView(R.id.txtValueCalorias));

                        if (isInputValid(txtnomeAtividade, valueRepeticoes, valueKM, valueTempo, valueCalorias)) {
                            handleActivity(txtnomeAtividade, txtValueTipoAtividade, valueRepeticoes, valueKM, valueTempo, valueCalorias);
                        } else {
                            Toast.makeText(requireContext(), "Preencha algum campo", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private String getTextFromView(int viewId) {
                        return ((TextView) view.findViewById(viewId)).getText().toString();
                    }

                    private int parseIntOrZero(String value) {
                        return value.isEmpty() ? 0 : Integer.parseInt(value);
                    }

                    private boolean isInputValid(String nomeAtividade, int repeticoes, int km, int tempo, int calorias) {
                        return !nomeAtividade.isEmpty() && (repeticoes > 0 || km > 0 || tempo > 0 || calorias > 0);
                    }

                    private void handleActivity(String nomeAtividade, String tipoAtividade, int repeticoes, int km, int tempo, int calorias) {
                        if (getTargetFragment() instanceof AtividadesFragment) {
                            handleAtividadeFragment(nomeAtividade, tipoAtividade, repeticoes, km, tempo, calorias);
                        } else if (getTargetFragment() instanceof MetaFragment) {
                            handleMetaFragment(nomeAtividade, tipoAtividade, repeticoes, km, tempo, calorias);
                        }
                    }

                    private void handleAtividadeFragment(String nomeAtividade, String tipoAtividade, int repeticoes, int km, int tempo, int calorias) {
                        String metaSelecionada = metaSpinner.getSelectedItem().toString();

                        String documentId = String.valueOf(db.insertData("atividades", db.createMap(nomeAtividade, tipoAtividade, repeticoes, km, tempo, calorias)));
                        containerHandler.addContainer(documentId, nomeAtividade, tipoAtividade, repeticoes, km, tempo, calorias);

                        if (!metaSelecionada.equals("N/A")) {
                            int selectedPosition = metaSpinner.getSelectedItemPosition();
                            String selectedDocumentId = documentIdMap.get(selectedPosition);
                            int progresso = calorias + km + tempo + repeticoes;

                            if (!tipoAtividade.equals(tipoAtividadeMap.get(selectedPosition))) {
                                Toast.makeText(requireContext(), tipoAtividadeMap.get(selectedPosition), Toast.LENGTH_SHORT).show();
                            } else {
                                db.updateProgress("metas", selectedDocumentId, db.createMetaMapToUpdateProgress(progresso));
                            }
                        }
                    }

                    private void handleMetaFragment(String nomeAtividade, String tipoAtividade, int repeticoes, int km, int tempo, int calorias) {
                        int progressoMaximo = calorias + km + tempo + repeticoes;

                        String documentId = String.valueOf(db.insertData("metas", db.createMetaMap(nomeAtividade, tipoAtividade, repeticoes, km, tempo, calorias, 0, progressoMaximo)));
                        containerHandler.addContainer(documentId, nomeAtividade, tipoAtividade, repeticoes, km, tempo, calorias, 0, progressoMaximo);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private void getDocs(View view) {
        db.getCollection(user, "metas").observe(requireActivity(), documents -> {
            adicionarItensAoAdapter(documents, view);
        });
    }

    private void adicionarItensAoAdapter(List<Map<String, Object>> documentos, View view) {
        if (documentos != null && !documentos.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item);
            adapter.add("N/A");
            int index = 1;
            for (Map<String, Object> documento : documentos) {
                if (documento != null) {
                    Log.d("documento", documento.toString());
                    String nomeAtividade = (String) documento.get("nomeAtividade");
                    String tipoAtividade = (String) documento.get("tipoMeta");
                    int progressoAtual = Integer.parseInt(String.valueOf(documento.getOrDefault("progresso", "0")));
                    int progressoMaximo = Integer.parseInt(String.valueOf(documento.getOrDefault("progressoMaximo", "0")));
                    int progresso = MetaFragment.getProgresso(progressoAtual, progressoMaximo);

                    String itemText = nomeAtividade + " (" + progresso + "%)";
                    adapter.add(itemText);

                    // Armazena o ID do documento no HashMap

                    documentIdMap.put(index, (String) documento.get("documentId"));
                    tipoAtividadeMap.put(index, tipoAtividade);
                    index++;
                }
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner metaSpinner = view.findViewById(R.id.metaSpinner);
            metaSpinner.setAdapter(adapter);
        }
    }
}
