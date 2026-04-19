package br.ufal.ic.myfood.models;

import com.google.gson.reflect.TypeToken;
import java.util.*;
import java.io.*;

public class UsuarioManager {

    private List<Usuario> usuarios;
    private int proximoId;

    private final String PATH = "data/usuarios.json";

    public UsuarioManager() {
        this.usuarios = carregar();
        this.proximoId = usuarios.size() + 1;
    }

    // Chamado pela Facade quando NÃO há cpf (usuário cliente)
    public void criarUsuario(String nome, String email, String senha, String endereco) {
        validarCamposBasicos(nome, email, senha, endereco);
        checarEmailDuplicado(email);
        usuarios.add(new Usuario(String.valueOf(proximoId++), nome, email, senha, endereco, null));
        salvar();
    }

    // Chamado pela Facade quando HÁ cpf (dono de empresa)
    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) {
        validarCamposBasicos(nome, email, senha, endereco);
        // CPF sempre obrigatório nesta versão, null e vazio são inválidos
        if (cpf == null || cpf.trim().isEmpty() || cpf.length() != 14) {
            throw new IllegalArgumentException("CPF invalido");
        }
        checarEmailDuplicado(email);
        usuarios.add(new Usuario(String.valueOf(proximoId++), nome, email, senha, endereco, cpf));
        salvar();
    }

    public String getAtributoUsuario(String id, String atributo) {
        Usuario u = buscarPorId(id);
        if (atributo == null || atributo.trim().isEmpty()) {
            throw new IllegalArgumentException("Atributo invalido");
        }
        switch (atributo) {
            case "nome":     return u.getNome();
            case "email":    return u.getEmail();
            case "senha":    return u.getSenha();
            case "endereco": return u.getEndereco();
            case "cpf":      return u.getCpf();
            default:         throw new IllegalArgumentException("Atributo invalido");
        }
    }

    public String login(String email, String senha) {
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Login ou senha invalidos");
        }
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                return u.getId();
            }
        }
        throw new IllegalArgumentException("Login ou senha invalidos");
    }

    public Usuario buscarPorId(String id) {
        for (Usuario u : usuarios) {
            if (u.getId().equals(id)) return u;
        }
        throw new IllegalArgumentException("Usuario nao cadastrado.");
    }

    // Auxiliares

    private void checarEmailDuplicado(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email)) {
                throw new IllegalArgumentException("Conta com esse email ja existe");
            }
        }
    }

    private void validarCamposBasicos(String nome, String email, String senha, String endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Email invalido");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha invalido");
        }
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereco invalido");
        }
    }

    // JSON

    private List<Usuario> carregar() {
        try {
            File f = new File(PATH);
            if (!f.exists()) return new ArrayList<>();
            return JsonUtil.fromJsonFile(PATH, new TypeToken<List<Usuario>>(){}.getType());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void salvar() {
        JsonUtil.toJsonFile(PATH, usuarios);
    }
}
