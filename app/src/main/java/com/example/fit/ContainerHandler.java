package com.example.fit;

public interface ContainerHandler {
    void addContainer(String documentId, String nomeAtividade, String atividade, int repeticoes, int km, int tempo, int calorias);
    void addContainer(String documentId, String nomeAtividade, String atividade, int repeticoes, int km, int tempo, int calorias, int progresso, int progressoMaximo);
    void removeContainer(String documentId);
}
