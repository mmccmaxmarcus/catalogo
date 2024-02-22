package br.com.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Produto {
    private String linha;
    private byte[] imagem;
    private String codigo;
    private String descricao;
    private BigDecimal preco;
    private String qtde;

    public Produto(String linha, byte[] imagem, String codigo, String descricao, Double preco, String qtde) {
        this.linha = linha;
        this.imagem = imagem;
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = new BigDecimal(preco);
        this.qtde = qtde;
    }
}
