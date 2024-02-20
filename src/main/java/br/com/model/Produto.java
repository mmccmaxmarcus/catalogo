package br.com.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Produto {
    private byte[] imagem;
    private String codigo;
    private String descricao;
    private BigDecimal preco;

    public Produto(byte [] imagem, String codigo, String descricao, Double preco) {
        this.imagem = imagem;
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = new BigDecimal(preco);
    }
}
