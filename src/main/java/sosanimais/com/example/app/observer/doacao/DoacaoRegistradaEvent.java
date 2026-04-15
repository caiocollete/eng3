package sosanimais.com.example.app.observer.doacao;

import sosanimais.com.example.app.model.entity.Doacao;

public class DoacaoRegistradaEvent {
    private final Doacao doacao;

    public DoacaoRegistradaEvent(Doacao doacao) {
        this.doacao = doacao;
    }

    public Doacao getDoacao() {
        return doacao;
    }
}
