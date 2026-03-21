package sosanimais.com.example.app.observer.doacao;

import org.springframework.stereotype.Component;
import sosanimais.com.example.app.model.entity.Doacao;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ContadorDoacaoObserver implements DoacaoObserver {
    private final AtomicInteger totalRegistradas = new AtomicInteger();

    @Override
    public void onDoacaoRegistrada(Doacao doacao) {
        totalRegistradas.incrementAndGet();
    }

    public int getTotalRegistradas() {
        return totalRegistradas.get();
    }
}
