package sosanimais.com.example.app.observer.doacao;

import org.springframework.stereotype.Component;
import sosanimais.com.example.app.model.entity.Doacao;

@Component
public class LogDoacaoObserver implements DoacaoObserver {
    @Override
    public void onDoacaoRegistrada(Doacao doacao) {
        System.out.println(
                "Doacao registrada com sucesso. id=" + doacao.getId() +
                        ", doador=" + doacao.getNomeDoador() +
                        ", tipo=" + doacao.getTipo() +
                        ", data=" + doacao.getDataDoacao()
        );
    }
}
