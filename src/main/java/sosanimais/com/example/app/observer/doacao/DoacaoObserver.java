package sosanimais.com.example.app.observer.doacao;

import sosanimais.com.example.app.model.entity.Doacao;

public interface DoacaoObserver {
    void onDoacaoRegistrada(Doacao doacao);
}
