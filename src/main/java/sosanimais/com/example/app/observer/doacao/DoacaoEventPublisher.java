package sosanimais.com.example.app.observer.doacao;
import sosanimais.com.example.app.model.entity.Doacao;
import java.util.ArrayList;
import java.util.List;

public class DoacaoEventPublisher {
    private final List<DoacaoObs> notificacoes = new ArrayList<>();

    public void adicionarObservador(DoacaoObs observer) {
        notificacoes.add(observer);
    }

    public void removerObservador(DoacaoObs observer) {
        notificacoes.remove(observer);
    }

    public void notificarDoacaoRegistrada(Doacao doacao) {
        DoacaoRegistradaEvent event = new DoacaoRegistradaEvent(doacao);
        for (DoacaoObs observer : notificacoes) {
            observer.onDoacaoRegistrada(event);
        }
    }

}