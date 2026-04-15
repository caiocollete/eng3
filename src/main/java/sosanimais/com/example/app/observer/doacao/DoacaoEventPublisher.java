package sosanimais.com.example.app.observer.doacao;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DoacaoEventPublisher implements DoacaoSubject {
    private final List<DoacaoObserver> observers = new ArrayList<>();
    private volatile DoacaoRegistradaEvent ultimaDoacaoRegistrada;

    @Override
    public void registerObserver(DoacaoObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(DoacaoObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (DoacaoObserver observer : observers) {
            try {
                observer.update();
            } catch (RuntimeException e) {
                System.err.println("Erro ao notificar observer de doação: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setUltimaDoacaoRegistrada(DoacaoRegistradaEvent event) {
        this.ultimaDoacaoRegistrada = event;
        notifyObservers();
    }

    @Override
    public DoacaoRegistradaEvent getUltimaDoacaoRegistrada() {
        return ultimaDoacaoRegistrada;
    }
}
