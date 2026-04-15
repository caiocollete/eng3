package sosanimais.com.example.app.observer.doacao;

import org.springframework.stereotype.Component;
import sosanimais.com.example.app.model.entity.Doacao;

@Component
public class LogDoacaoObserver implements DoacaoObserver {
    private final DoacaoSubject subject;

    public LogDoacaoObserver(DoacaoSubject subject) {
        this.subject = subject;
    }

    @Override
    public void update() {
        DoacaoRegistradaEvent event = subject.getUltimaDoacaoRegistrada();
        if (event == null) {
            return;
        }

        Doacao doacao = event.getDoacao();
        System.out.println(
                "Doação registrada com sucesso. id=" + doacao.getId() +
                        ", doador=" + doacao.getNomeDoador() +
                        ", tipo=" + doacao.getTipo() +
                        ", data=" + doacao.getDataDoacao()
        );
    }
}
