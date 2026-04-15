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

    @PostConstruct
    public void registerObserver() {
        subject.registerObserver(this);
    }

    @PreDestroy
    public void removeObserver() {
        subject.removeObserver(this);
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
