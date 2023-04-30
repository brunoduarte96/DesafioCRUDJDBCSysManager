package model;

import java.io.Serializable;
import java.util.Objects;

public class Fabricante implements Serializable {

    
	private static final long serialVersionUID = 1L;
	private Integer id;
    private String nome;
    private String paisOrigem;

    public Fabricante() {
    }
    
    

    public Fabricante(Integer id, String nome, String paisOrigem) {
		
		this.id = id;
		this.nome = nome;
		this.paisOrigem = paisOrigem;
	}



	

    public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fabricante that = (Fabricante) o;
        return id == that.id && Objects.equals(nome, that.nome) && Objects.equals(paisOrigem, that.paisOrigem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, paisOrigem);
    }
}
