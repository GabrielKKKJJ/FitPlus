package com.example.fit;

public class AtividadeInfo {
    private final String documentID;
    private final String nomeAtividade;
    private final String atividade;
    private final int repeticoes;
    private final int km;
    private final int tempo;
    private final int calorias;

    public AtividadeInfo(String documentID, String nomeAtividade, String atividade, int repeticoes, int km, int tempo, int calorias) {
        this.documentID = documentID;
        this.nomeAtividade = nomeAtividade;
        this.atividade = atividade;
        this.repeticoes = repeticoes;
        this.km = km;
        this.tempo = tempo;
        this.calorias = calorias;
    }

    public String getDocumentID() {
        return documentID;
    }

    public String getnomeAtividade() {
        return nomeAtividade;
    }

    public String getAtividade() { return atividade;}

    public int getRepeticoes() {
        return repeticoes;
    }

    public int getKm() {
        return km;
    }

    public int getTempo() {
        return tempo;
    }

    public int getCalorias() {
        return calorias;
    }
}