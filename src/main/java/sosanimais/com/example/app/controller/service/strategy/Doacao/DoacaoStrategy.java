package sosanimais.com.example.app.controller.service.strategy.Doacao;

import sosanimais.com.example.app.model.entity.Doacao;

public interface DoacaoStrategy {
    abstract void validate(Doacao doacao);
}
