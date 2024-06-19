package com.example.fit;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String user = mAuth.getCurrentUser().getEmail();

    private String[] documentID = new String[1];

    public MutableLiveData<List<Map<String, Object>>> getCollection(String user, String collection) {
        MutableLiveData<List<Map<String, Object>>> liveData = new MutableLiveData<>();
        db.collection("usuarios").document(user).collection(collection).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> documents = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            data.put("documentId", document.getId()); // Add document ID to the data map
                            documents.add(data);
                        }
                        liveData.postValue(documents);
                    } else {
                        Log.e("DB", "Error getting documents", task.getException());
                    }
                });
        return liveData;
    }

    public Task<String> insertData(String collection, Map<String, Object> data) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        db.collection("usuarios").document(user).collection(collection)
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    String documentID = documentReference.getId();
                    Log.d("DB", "DocumentSnapshot added with ID: " + documentID);
                    taskCompletionSource.setResult(documentID);
                })
                .addOnFailureListener(e -> {
                    Log.w("DB ERRO", "Error adding document", e);
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    public void update(String collection, String documentID, Map<String, Object> newData) {
        Log.d("DB", "Atualizando documento com ID: " + documentID);
        DocumentReference docRef = db.collection("usuarios").document(user).collection(collection).document(documentID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                docRef.update(newData)
                        .addOnSuccessListener(aVoid -> Log.d("DB", "Documento atualizado com sucesso!"))
                        .addOnFailureListener(e -> Log.w("DB ERRO", "Erro ao atualizar documento", e));
            } else {
                Log.d("DB", "Documento não encontrado");
            }
        });
    }

    public void updateProgress(String collection, String documentID, Map<String, Object> newData) {
        Log.d("DB", "Atualizando documento com ID: " + documentID);
        DocumentReference docRef = db.collection("usuarios").document(user).collection(collection).document(documentID);

        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object fieldValue = documentSnapshot.get("progresso");
                        if (fieldValue != null) {
                            String teste = String.valueOf(fieldValue);
                            int progressoAtual = Integer.parseInt(teste);
                            Log.d("DB", ""+progressoAtual);

                            int novoProgresso = (int) newData.get("progresso");
                            Log.d("DB", ""+novoProgresso);
                            int progressoAtualizado = progressoAtual + novoProgresso;

                            newData.put("progresso", String.valueOf(progressoAtualizado));

                            docRef.update(newData)
                                    .addOnSuccessListener(aVoid -> Log.d("DB", "Documento atualizado com sucesso!"))
                                    .addOnFailureListener(e -> Log.w("DB ERRO", "Erro ao atualizar documento", e));
                        } else {
                            Log.d("DB", "Campo 'progresso' não encontrado no documento");
                        }
                    } else {
                        Log.d("DB", "Documento não encontrado");
                    }
                })
                .addOnFailureListener(e -> Log.w("DB ERRO", "Erro ao obter documento", e));
    }



    public void deleteData(String collection, String documentId) {
        Log.d("DB", "Deletando documento com ID: " + documentId);
        db.collection("usuarios")
                .document(user).collection(collection).document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("DB", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w("DB ERRO", "Error deleting document", e));
    }

    public Map<String, Object> createMap(String nomeAtividade, String atividade, int repeticoes, int km, int tempo, int calorias) {
        Map<String, Object> MapData = new HashMap<>();
        MapData.put("nomeAtividade", nomeAtividade);
        MapData.put("tipoAtividade", atividade);
        MapData.put("repeticoes", repeticoes);
        MapData.put("km", km);
        MapData.put("tempo", tempo);
        MapData.put("calorias", calorias);
        return MapData;
    }

    public Map<String, Object> createMetaMap(String nomeMeta, String atividade, int repeticoes, int km, int tempo, int calorias, int progressoAtual, int progressoMaximo) {
        Map<String, Object> MapData = new HashMap<>();
        MapData.put("nomeAtividade", nomeMeta);
        MapData.put("tipoMeta", atividade);
        MapData.put("repeticoes", repeticoes);
        MapData.put("km", km);
        MapData.put("tempo", tempo);
        MapData.put("calorias", calorias);
        MapData.put("progresso", progressoAtual);
        MapData.put("progressoMaximo", progressoMaximo);
        return MapData;
    }

    public Map<String, Object> createMetaMapToUpdateProgress(int progresso) {
        Map<String, Object> MapData = new HashMap<>();
        MapData.put("progresso", progresso);
        return MapData;
    }
}
