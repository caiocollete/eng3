package sosanimais.com.example.app.observer.doacao;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ContadorDoacaoObserver implements DoacaoObserver {
    private final DoacaoSubject subject;
    private int totalRegistradas = 0;

    public ContadorDoacaoObserver(DoacaoSubject subject) {
        this.subject = subject;
    }

    @Override
    public void update() {
        if (subject.getUltimaDoacaoRegistrada() != null) {
            totalRegistradas++;
        }
    }

    public int getTotalRegistradas() {
        return totalRegistradas;
    }
}
