package sosanimais.com.example.app.controller.service.strategy.Doacao;

import sosanimais.com.example.app.model.entity.Doacao;

public class DoacaoDinheiroStrategy implements DoacaoStrategy {
    @Override
    public void validate(Doacao doacao) {
        if(doacao.getValor() == null || doacao.getValor() < 0) {
            throw new IllegalArgumentException("Valor invalido");
        }
    }
}
