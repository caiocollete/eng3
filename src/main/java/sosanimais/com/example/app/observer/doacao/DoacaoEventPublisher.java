package sosanimais.com.example.app.observer.doacao;

import org.springframework.stereotype.Component;
import sosanimais.com.example.app.model.entity.Doacao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DoacaoEventPublisher {
    private final CopyOnWriteArrayList<DoacaoObserver> observers;

    public DoacaoEventPublisher(List<DoacaoObserver> observers) {
        this.observers = new CopyOnWriteArrayList<>(observers);
    }

    public void registrar(DoacaoObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void remover(DoacaoObserver observer) {
        observers.remove(observer);
    }

    public void publicar(Doacao doacao) {
        for (DoacaoObserver observer : observers) {
            try {
                observer.onDoacaoRegistrada(doacao);
            } catch (RuntimeException e) {
                System.err.println("Erro ao notificar observer de doacao: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
