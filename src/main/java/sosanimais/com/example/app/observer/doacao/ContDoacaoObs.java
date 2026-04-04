package sosanimais.com.example.app.observer.doacao;

public class ContDoacaoObs implements DoacaoObs {
    
    private int totalRegistradas = 0;
    
    @Override
    public void onDoacaoRegistrada(DoacaoRegistradaEvent event) {
        totalRegistradas++;
        System.out.println("Total de doações registradas: " + totalRegistradas);
    }
    
    public int getTotalRegistradas() {
        return totalRegistradas;
    }
}