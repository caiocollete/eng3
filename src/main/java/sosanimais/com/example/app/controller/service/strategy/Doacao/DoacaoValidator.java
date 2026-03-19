package sosanimais.com.example.app.controller.service.strategy.Doacao;

import sosanimais.com.example.app.model.entity.Doacao;

public class DoacaoValidator {
    public static void validate(Doacao doacao) {
        switch (doacao.getTipo()) {
            case "dinheiro":
                new DoacaoDinheiroStrategy().validate(doacao);
                break;
            default:
                new DoacaoProdutoStrategy().validate(doacao);
        }
    }
}
