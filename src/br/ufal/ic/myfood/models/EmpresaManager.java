package br.ufal.ic.myfood.models;

import java.util.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class EmpresaManager {

    private List<Empresa> empresas;
    private int proximoId;
    private final String PATH = "data/empresas.json";

    private UsuarioManager usuarioManager;

    public EmpresaManager(UsuarioManager usuarioManager) {
        this.usuarioManager = usuarioManager;
        this.empresas = carregar();
        this.proximoId = empresas.size() + 1;
    }

    public String criarEmpresa(String tipoEmpresa, String idDono, String nome, String endereco, String tipoCozinha) {

        Usuario dono = buscarDonoOuErro(idDono);

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }

        for (Empresa e : empresas) {
            if (e.getNome().equals(nome) && !e.getIdDono().equals(idDono)) {
                throw new IllegalArgumentException("Empresa com esse nome ja existe");
            }
            if (e.getNome().equals(nome)
                    && e.getEndereco().equals(endereco)
                    && e.getIdDono().equals(idDono)) {
                throw new IllegalArgumentException("Proibido cadastrar duas empresas com o mesmo nome e local");
            }
        }

        Empresa nova = new Empresa(
                String.valueOf(proximoId++),
                nome,
                endereco,
                tipoCozinha,
                idDono
        );

        empresas.add(nova);
        salvar();
        return nova.getId();
    }

    public String getEmpresasDoUsuario(String idDono) {

        buscarDonoOuErro(idDono); // valida e checa CPF

        List<String> lista = new ArrayList<>();
        for (Empresa e : empresas) {
            if (e.getIdDono().equals(idDono)) {
                lista.add("[" + e.getNome() + ", " + e.getEndereco() + "]");
            }
        }
        return "{" + lista.toString() + "}";
    }

    public String getAtributoEmpresa(String idEmpresa, String atributo) {

        Empresa e = buscarPorId(idEmpresa);

        if (atributo == null || atributo.trim().isEmpty()) {
            throw new IllegalArgumentException("Atributo invalido");
        }

        switch (atributo) {
            case "nome":        return e.getNome();
            case "endereco":    return e.getEndereco();
            case "tipoCozinha": return e.getTipoCozinha();
            case "dono":
                try {
                    return usuarioManager.buscarPorId(e.getIdDono()).getNome();
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Usuario nao cadastrado");
                }
            default:
                throw new IllegalArgumentException("Atributo invalido");
        }
    }

    public String getIdEmpresa(String idDono, String nome, int indice) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }
        if (indice < 0) {
            throw new IllegalArgumentException("Indice invalido");
        }

        List<Empresa> lista = new ArrayList<>();
        for (Empresa e : empresas) {
            if (e.getIdDono().equals(idDono) && e.getNome().equals(nome)) {
                lista.add(e);
            }
        }

        if (lista.isEmpty()) {
            throw new IllegalArgumentException("Nao existe empresa com esse nome");
        }
        if (indice >= lista.size()) {
            throw new IllegalArgumentException("Indice maior que o esperado");
        }

        return lista.get(indice).getId();
    }

    /** Verifica se um usuário é dono de qualquer empresa cadastrada. */
    public boolean isDonoDeEmpresa(String idUsuario) {
        if (idUsuario == null) return false;
        for (Empresa e : empresas) {
            if (e.getIdDono().equals(idUsuario)) return true;
        }
        return false;
    }

    public Empresa buscarPorId(String id) {
        for (Empresa e : empresas) {
            if (e.getId().equals(id)) return e;
        }
        throw new IllegalArgumentException("Empresa nao cadastrada");
    }

    // Auxiliar privado >> resolve null/vazio como "sem permissão"

    private Usuario buscarDonoOuErro(String idDono) {
        // ID nulo ou vazio → usuário não tem permissão (não é dono)
        if (idDono == null || idDono.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario nao pode criar uma empresa");
        }
        Usuario dono;
        try {
            dono = usuarioManager.buscarPorId(idDono);
        } catch (Exception e) {
            throw new IllegalArgumentException("Usuario nao cadastrado");
        }
        // Usuários sem CPF são clientes — não podem criar empresa
        if (dono.getCpf() == null || dono.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario nao pode criar uma empresa");
        }
        return dono;
    }

    // JSON

    private List<Empresa> carregar() {
        Type tipo = new TypeToken<List<Empresa>>() {}.getType();
        List<Empresa> lista = JsonUtil.fromJsonFile(PATH, tipo);
        return (lista == null) ? new ArrayList<>() : lista;
    }

    private void salvar() {
        JsonUtil.toJsonFile(PATH, empresas);
    }
}
