package com.example.scopa.model;

import java.util.List;

public class MossaRequest {
    private int indexMano;
    private List<Integer> indiciTavolo;

    // Getter e Setter
    public int getIndexMano() { return indexMano; }
    public void setIndexMano(int indexMano) { this.indexMano = indexMano; }
    public List<Integer> getIndiciTavolo() { return indiciTavolo; }
    public void setIndiciTavolo(List<Integer> indiciTavolo) { this.indiciTavolo = indiciTavolo; }
}