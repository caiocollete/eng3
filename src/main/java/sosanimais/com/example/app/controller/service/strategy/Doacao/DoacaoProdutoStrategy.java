package sosanimais.com.example.app.controller.service.strategy.Doacao;

import sosanimais.com.example.app.model.entity.Doacao;


public class DoacaoProdutoStrategy implements DoacaoStrategy {
    @Override
    public void validate(Doacao doacao) {
        if (doacao.getProduto().getNome().equals("") || doacao.getProduto().getDescricao().equals("")) {
            throw new IllegalArgumentException("É necessario informar um nome e descrição para o produto");
        }
    }
}
