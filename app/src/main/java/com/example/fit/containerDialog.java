package com.example.fit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;

public class containerDialog extends DialogFragment {
    Database db;
    private ContainerHandler containerHandler;
    private  String user;
    private boolean editMode = false;
    private String collectionName;

    public containerDialog(Database db, String user) {
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
            collectionName = "atividades";
        } else if (getTargetFragment() instanceof  MetaFragment) {
            collectionName = "metas";
        }
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.container_dialog, null);

        Bundle args = getArguments();
        if (args != null) {
            String documentId = args.getString("documentId");
            String nomeAtividade = args.getString("nomeAtividade");
            Log.d("TAG", "onCreateDialog: " + documentId);
            String atividade = args.getString("atividade");
            int repeticoes = args.getInt("repeticoes");
            int km = args.getInt("km");
            int tempo = args.getInt("tempo");
            int calorias = args.getInt("calorias");

            Spinner atividadeSpinner = view.findViewById(R.id.tipoAtividadeSpinner);
            setupSpinner(atividadeSpinner, atividade);

            TextView nomeAtividadeView = view.findViewById(R.id.nomeAtividade);
            TextView repeticoesView = view.findViewById(R.id.txtValueRepeticoes);
            TextView kmStringView = view.findViewById(R.id.txtValueKm);
            TextView tempoView = view.findViewById(R.id.txtValueTempo);
            TextView caloriasView = view.findViewById(R.id.txtValueCalorias);

            setEnabled(false, nomeAtividadeView, atividadeSpinner, repeticoesView, kmStringView, tempoView, caloriasView);

            nomeAtividadeView.setText(nomeAtividade);
            repeticoesView.setText(String.valueOf(repeticoes));
            kmStringView.setText(String.valueOf(km));
            tempoView.setText(String.valueOf(tempo));
            caloriasView.setText(String.valueOf(calorias));

            Button delete = view.findViewById(R.id.deleteBtn);
            delete.setOnClickListener(v -> {
               db.deleteData(collectionName, documentId);
               dismiss();
               containerHandler.removeContainer(documentId);
               Toast.makeText(getActivity(), "Atividade removida", Toast.LENGTH_SHORT).show();
            });
            Button edit = view.findViewById(R.id.editBtn);

            if(getTargetFragment() instanceof  AtividadesFragment) {

                edit.setOnClickListener(v -> {
                    if (editMode) {
                        editMode = false;

                        setEnabled(false, nomeAtividadeView, atividadeSpinner, repeticoesView, kmStringView, tempoView, caloriasView);
                        edit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit, 0, 0, 0);
                    } else {
                        editMode = true;

                        setEnabled(true, nomeAtividadeView, atividadeSpinner, repeticoesView, kmStringView, tempoView, caloriasView);
                        edit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit_on, 0, 0, 0);

                        Toast.makeText(getActivity(), "Modo de edição", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                edit.setVisibility(View.GONE);
            }

            builder.setView(view)
                    .setPositiveButton("Concluir", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (editMode) {
                                String txtnomeAtividade = nomeAtividadeView.getText().toString().trim();
                                String txtValueTipoAtividade = atividadeSpinner.getSelectedItem().toString();

                                String repeticoesString = ((TextView) view.findViewById(R.id.txtValueRepeticoes)).getText().toString().trim();
                                int valueRepeticoes = repeticoesString.isEmpty() ? 0 : Integer.parseInt(repeticoesString);

                                String kmString = ((TextView) view.findViewById(R.id.txtValueKm)).getText().toString().trim();
                                int valueKM = kmString.isEmpty() ? 0 : Integer.parseInt(kmString);

                                String tempoString = ((TextView) view.findViewById(R.id.txtValueTempo)).getText().toString().trim();
                                int valueTempo = tempoString.isEmpty() ? 0 : Integer.parseInt(tempoString);

                                String caloriasString = ((TextView) view.findViewById(R.id.txtValueCalorias)).getText().toString().trim();
                                int valueCalorias = caloriasString.isEmpty() ? 0 : Integer.parseInt(caloriasString);

                                db.update(collectionName, documentId, db.createMap(txtnomeAtividade, txtValueTipoAtividade, valueRepeticoes, valueKM, valueTempo, valueCalorias));
                                containerHandler.removeContainer(documentId);
                                containerHandler.addContainer(documentId,  nomeAtividade, txtValueTipoAtividade, valueRepeticoes, valueKM, valueTempo, valueCalorias);
                                Toast.makeText(getActivity(), "Atividade editada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        return builder.create();
    }
        return null;
    }

    private void setupSpinner(Spinner spinner, String atividade) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.Opcoes, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(atividade)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private static void setEnabled(Boolean enabled,TextView nomeAtividadeView, Spinner atividadeSpinner, TextView repeticoesView, TextView kmStringView, TextView tempoView, TextView caloriasView) {
        nomeAtividadeView.setEnabled(enabled);
        atividadeSpinner.setEnabled(enabled);
        repeticoesView.setEnabled(enabled);
        kmStringView.setEnabled(enabled);
        tempoView.setEnabled(enabled);
        caloriasView.setEnabled(enabled);
    }
}
