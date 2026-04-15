package sosanimais.com.example.app.observer.doacao;

public interface DoacaoSubject {
    void registerObserver(DoacaoObserver observer);

    void removeObserver(DoacaoObserver observer);

    void notifyObservers();

    void setUltimaDoacaoRegistrada(DoacaoRegistradaEvent event);

    DoacaoRegistradaEvent getUltimaDoacaoRegistrada();
}
