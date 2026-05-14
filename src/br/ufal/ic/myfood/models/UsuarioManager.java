package br.ufal.ic.myfood.models;

import java.io.File;
import java.util.*;

public class UsuarioManager {

    private final String PATH = "data/usuarios.json";

    private List<Usuario> carregar() {
        List<Usuario> lista = new ArrayList<>();
        for (Map<String, String> m : JsonUtil.carregarLista(PATH))
            lista.add(Usuario.fromMap(m));
        return lista;
    }

    private void salvar(List<Usuario> lista) {
        List<String> jsons = new ArrayList<>();
        for (Usuario u : lista) jsons.add(u.toJson());
        JsonUtil.salvarLista(PATH, jsons);
    }

    public void criarUsuario(String nome, String email, String senha, String endereco) {
        validarCamposBasicos(nome, email, senha, endereco);
        List<Usuario> lista = carregar();
        checarEmailDuplicado(lista, email);
        lista.add(new Usuario(String.valueOf(lista.size() + 1), nome, email, senha, endereco, (String) null));
        salvar(lista);
    }

    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) {
        validarCamposBasicos(nome, email, senha, endereco);
        if (cpf == null || cpf.trim().isEmpty() || cpf.length() != 14)
            throw new IllegalArgumentException("CPF invalido");
        List<Usuario> lista = carregar();
        checarEmailDuplicado(lista, email);
        lista.add(new Usuario(String.valueOf(lista.size() + 1), nome, email, senha, endereco, cpf));
        salvar(lista);
    }

    public void criarUsuario(String nome, String email, String senha,
                             String endereco, String veiculo, String placa) {
        validarCamposBasicos(nome, email, senha, endereco);
        if (veiculo == null || veiculo.trim().isEmpty())
            throw new IllegalArgumentException("Veiculo invalido");
        if (placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("Placa invalido");
        List<Usuario> lista = carregar();
        for (Usuario u : lista)
            if (placa.equals(u.getPlaca())) throw new IllegalArgumentException("Placa invalido");
        checarEmailDuplicado(lista, email);
        lista.add(new Usuario(String.valueOf(lista.size() + 1), nome, email, senha, endereco, veiculo, placa));
        salvar(lista);
    }

    public String getAtributoUsuario(String id, String atributo) {
        Usuario u = buscarPorId(id);
        if (atributo == null || atributo.trim().isEmpty())
            throw new IllegalArgumentException("Atributo invalido");
        switch (atributo) {
            case "nome":     return u.getNome();
            case "email":    return u.getEmail();
            case "senha":    return u.getSenha();
            case "endereco": return u.getEndereco();
            case "cpf":      return u.getCpf();
            case "veiculo":  return u.getVeiculo();
            case "placa":    return u.getPlaca();
            default: throw new IllegalArgumentException("Atributo invalido");
        }
    }

    public String login(String email, String senha) {
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty())
            throw new IllegalArgumentException("Login ou senha invalidos");
        for (Usuario u : carregar())
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) return u.getId();
        throw new IllegalArgumentException("Login ou senha invalidos");
    }

    public Usuario buscarPorId(String id) {
        for (Usuario u : carregar())
            if (u.getId().equals(id)) return u;
        throw new IllegalArgumentException("Usuario nao cadastrado.");
    }

    private void checarEmailDuplicado(List<Usuario> lista, String email) {
        for (Usuario u : lista)
            if (u.getEmail().equals(email))
                throw new IllegalArgumentException("Conta com esse email ja existe");
    }

    private void validarCamposBasicos(String nome, String email, String senha, String endereco) {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Nome invalido");
        if (email == null || email.trim().isEmpty() || !email.contains("@"))
            throw new IllegalArgumentException("Email invalido");
        if (senha == null || senha.trim().isEmpty())
            throw new IllegalArgumentException("Senha invalido");
        if (endereco == null || endereco.trim().isEmpty())
            throw new IllegalArgumentException("Endereco invalido");
    }
}
