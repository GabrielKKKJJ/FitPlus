package com.example.fit;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ImcFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_imc, container, false);

        Button btnCalculateImc = rootView.findViewById(R.id.btnCalcular);
        btnCalculateImc.setOnClickListener(v -> {
            // Calcula o IMC
            calculateImc(rootView);

            // Encontra a View ativa
            View view = getActivity().getCurrentFocus();

            // Esconde o teclado caso alguma View esteja ativa
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        return rootView;
    }

    public void calculateImc(View view) {
        EditText editPeso = view.findViewById(R.id.editPeso);
        EditText editAltura = view.findViewById(R.id.editAltura);
        TextView textResult = view.findViewById(R.id.textResult);

        if (editPeso.getText().toString().isEmpty() || editAltura.getText().toString().isEmpty()) {
            textResult.setText("Por favor, preencha o peso e a altura.");
            return;
        }

        // Passa as Strings newEditPeso e newEditAltura para float
        float peso = Float.parseFloat(editPeso.getText().toString());
        float altura = Float.parseFloat(editAltura.getText().toString());
        Log.d("TAG", "Peso: " + peso);
        Log.d("TAG", "Altura: " + altura);
        // Faz o cálculo de IMC
        float imc = peso / (altura * altura);
        //Retorna o IMC
        textResult.setText(String.format("%.2f", imc));
    }

}