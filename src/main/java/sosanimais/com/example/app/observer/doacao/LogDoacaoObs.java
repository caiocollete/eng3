package sosanimais.com.example.app.observer.doacao;
import sosanimais.com.example.app.model.entity.Doacao;

public class LogDoacaoObs implements DoacaoObs {
    
    @Override
    public void onDoacaoRegistrada(DoacaoRegistradaEvent event) {
        Doacao doacao = event.getDoacao();
        System.out.println("=== DOAÇÃO REGISTRADA ===");
        System.out.println("Doador: " + doacao.getNomeDoador() + " (" + doacao.getId() + ")");
        System.out.println("Email: " + doacao.getEmail());
        System.out.println("Tipo: " + doacao.getTipo());
        System.out.println("Data: " + doacao.getDataDoacao());
        System.out.println("========================");
    }
}