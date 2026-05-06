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

    // criarEmpresa — restaurante

    public String criarEmpresa(String tipoEmpresa, String idDono, String nome,
                               String endereco, String tipoCozinha) {
        buscarDonoOuErro(idDono);
        validarNome(nome);
        validarDuplicata(idDono, nome, endereco);

        Empresa nova = new Empresa(String.valueOf(proximoId++), nome, endereco, tipoCozinha, idDono);
        empresas.add(nova);
        salvar();
        return nova.getId();
    }

    // criarEmpresa — mercado

    public String criarEmpresa(String tipoEmpresa, String idDono, String nome,
                               String endereco, String abre, String fecha, String tipoMercado) {
        if (tipoEmpresa == null || tipoEmpresa.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de empresa invalido");
        }
        buscarDonoOuErro(idDono);
        validarNome(nome);
        validarEndereco(endereco);
        validarHorario(abre, fecha);
        if (tipoMercado == null || tipoMercado.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de mercado invalido");
        }
        validarDuplicata(idDono, nome, endereco);

        Empresa nova = new Empresa(String.valueOf(proximoId++), nome, endereco,
                idDono, tipoEmpresa, abre, fecha, tipoMercado);
        empresas.add(nova);
        salvar();
        return nova.getId();
    }

    // criarEmpresa — farmácia

    public String criarEmpresa(String tipoEmpresa, String idDono, String nome,
                               String endereco, boolean aberto24Horas, int numeroFuncionarios) {
        if (tipoEmpresa == null || tipoEmpresa.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de empresa invalido");
        }
        buscarDonoOuErro(idDono);
        validarNome(nome);
        validarEndereco(endereco);
        validarDuplicata(idDono, nome, endereco);

        Empresa nova = new Empresa(String.valueOf(proximoId++), nome, endereco,
                idDono, tipoEmpresa, aberto24Horas, numeroFuncionarios);
        empresas.add(nova);
        salvar();
        return nova.getId();
    }

    // alterarFuncionamento

    public void alterarFuncionamento(String idMercado, String abre, String fecha) {
        Empresa e = buscarPorId(idMercado);
        if (!"mercado".equals(e.getTipoEmpresa())) {
            throw new IllegalArgumentException("Nao e um mercado valido");
        }
        validarHorario(abre, fecha);
        e.setAbre(abre);
        e.setFecha(fecha);
        salvar();
    }

    // getAtributoEmpresa

    public String getAtributoEmpresa(String idEmpresa, String atributo) {
        Empresa e = buscarPorId(idEmpresa);

        if (atributo == null || atributo.trim().isEmpty()) {
            throw new IllegalArgumentException("Atributo invalido");
        }

        switch (atributo) {
            case "nome":               return e.getNome();
            case "endereco":           return e.getEndereco();
            case "tipoCozinha":        return e.getTipoCozinha();
            case "abre":               return e.getAbre();
            case "fecha":              return e.getFecha();
            case "tipoMercado":        return e.getTipoMercado();
            case "aberto24Horas":      return String.valueOf(e.isAberto24Horas());
            case "numeroFuncionarios": return String.valueOf(e.getNumeroFuncionarios());
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

    // Demais métodos públicos

    public String getEmpresasDoUsuario(String idDono) {
        buscarDonoOuErro(idDono);
        List<String> lista = new ArrayList<>();
        for (Empresa e : empresas) {
            if (e.getIdDono().equals(idDono)) {
                lista.add("[" + e.getNome() + ", " + e.getEndereco() + "]");
            }
        }
        return "{" + lista.toString() + "}";
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

    public List<Empresa> getTodas() {
        return empresas;
    }

    // Validações privadas

    private Usuario buscarDonoOuErro(String idDono) {
        if (idDono == null || idDono.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario nao pode criar uma empresa");
        }
        Usuario dono;
        try {
            dono = usuarioManager.buscarPorId(idDono);
        } catch (Exception e) {
            throw new IllegalArgumentException("Usuario nao cadastrado");
        }
        if (dono.getCpf() == null || dono.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario nao pode criar uma empresa");
        }
        return dono;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }
    }

    private void validarEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereco da empresa invalido");
        }
    }

    private void validarDuplicata(String idDono, String nome, String endereco) {
        for (Empresa e : empresas) {
            if (e.getNome().equals(nome) && !e.getIdDono().equals(idDono)) {
                throw new IllegalArgumentException("Empresa com esse nome ja existe");
            }
            if (e.getNome().equals(nome) && e.getEndereco().equals(endereco)
                    && e.getIdDono().equals(idDono)) {
                throw new IllegalArgumentException("Proibido cadastrar duas empresas com o mesmo nome e local");
            }
        }
    }

    private void validarHorario(String abre, String fecha) {
        if (abre == null) throw new IllegalArgumentException("Horario invalido");
        if (fecha == null) throw new IllegalArgumentException("Horario invalido");
        validarFormato(abre);
        validarFormato(fecha);
        int[] a = parseHora(abre);
        int[] f = parseHora(fecha);
        if (a[0] > 23 || a[1] > 59) throw new IllegalArgumentException("Horario invalido");
        if (f[0] > 23 || f[1] > 59) throw new IllegalArgumentException("Horario invalido");
        if (f[0] * 60 + f[1] <= a[0] * 60 + a[1]) throw new IllegalArgumentException("Horario invalido");
    }

    private void validarFormato(String hora) {
        if (hora.isEmpty() || !hora.matches("\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Formato de hora invalido");
        }
    }

    private int[] parseHora(String hora) {
        String[] p = hora.split(":");
        return new int[]{ Integer.parseInt(p[0]), Integer.parseInt(p[1]) };
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
