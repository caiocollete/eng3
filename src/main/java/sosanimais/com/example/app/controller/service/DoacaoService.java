package sosanimais.com.example.app.controller.service;

import org.springframework.stereotype.Service;
import sosanimais.com.example.app.controller.service.strategy.Doacao.DoacaoValidator;
import sosanimais.com.example.app.model.DAL.DoacaoDAL;
import sosanimais.com.example.app.model.entity.Doacao;
import sosanimais.com.example.app.observer.doacao.DoacaoRegistradaEvent;
import sosanimais.com.example.app.observer.doacao.DoacaoSubject;

import java.util.Collections;
import java.util.List;

@Service
public class DoacaoService {
    private final DoacaoDAL doacaoDAL;
    private final DoacaoSubject doacaoSubject;

    public DoacaoService(DoacaoSubject doacaoSubject) {
        this.doacaoDAL = new DoacaoDAL();
        this.doacaoSubject = doacaoSubject;
    }

    public Doacao registrarDoacao(Doacao doacao) {
        if (doacao == null || doacao.getNomeDoador() == null || doacao.getNomeDoador().trim().isEmpty()
                || doacao.getEmail() == null || doacao.getEmail().trim().isEmpty()
                || doacao.getTipo() == null || doacao.getTipo().trim().isEmpty()
                || doacao.getDataDoacao() == null) {
            System.err.println("Dados da doação inválidos no serviço.");
            return null;
        }

        DoacaoValidator.validate(doacao);

        Doacao doacaoSalva = doacaoDAL.save(doacao);
        if (doacaoSalva != null && doacaoSalva.getId() != null) {
            doacaoSubject.setUltimaDoacaoRegistrada(new DoacaoRegistradaEvent(doacaoSalva));
        }

        return doacaoSalva;
    }

    public List<Doacao> listarTodasDoacoes() {
        try {
            return doacaoDAL.getAll();
        } catch (Exception e) {
            System.err.println("Erro no serviço ao listar todas as doações: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Doacao buscarDoacaoPorId(Long id) {
        if (id == null || id <= 0) {
            return null;
        }

        try {
            return doacaoDAL.getById(id);
        } catch (Exception e) {
            System.err.println("Erro no serviço ao buscar doação por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean deletarDoacao(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        try {
            return doacaoDAL.delete(id);
        } catch (Exception e) {
            System.err.println("Erro no serviço ao deletar doação com ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
