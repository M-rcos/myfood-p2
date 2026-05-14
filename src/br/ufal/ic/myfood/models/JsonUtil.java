package br.ufal.ic.myfood.models;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class JsonUtil {

    public static void deleteFile(String path) {
        new File(path).delete();
    }

    private static String lerArquivo(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            return new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        } catch (Exception e) { return null; }
    }

    private static void escreverArquivo(String path, String conteudo) {
        try {
            File f = new File(path);
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
            Files.write(f.toPath(), conteudo.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static List<Map<String, String>> carregarLista(String path) {
        String conteudo = lerArquivo(path);
        if (conteudo == null || conteudo.trim().isEmpty()) return new ArrayList<>();
        try { return parseArray(conteudo.trim()); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public static void salvarLista(String path, List<String> itens) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < itens.size(); i++) {
            sb.append("  ").append(itens.get(i));
            if (i < itens.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        escreverArquivo(path, sb.toString());
    }

    public static String esc(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                       .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }

    public static String escLista(List<String> lista) {
        if (lista == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            sb.append(esc(lista.get(i)));
            if (i < lista.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String getString(Map<String, String> m, String chave) {
        String v = m.get(chave);
        if (v == null || v.equals("null")) return null;
        return v;
    }

    public static boolean getBoolean(Map<String, String> m, String chave) {
        return "true".equals(m.get(chave));
    }

    public static int getInt(Map<String, String> m, String chave) {
        try { return Integer.parseInt(m.getOrDefault(chave, "0")); }
        catch (Exception e) { return 0; }
    }

    public static double getDouble(Map<String, String> m, String chave) {
        try { return Double.parseDouble(m.getOrDefault(chave, "0")); }
        catch (Exception e) { return 0; }
    }

    public static List<String> getStringList(Map<String, String> m, String chave) {
        String raw = m.get(chave);
        if (raw == null || raw.trim().equals("[]")) return new ArrayList<>();
        return parseStringArray(raw);
    }

    private static List<Map<String, String>> parseArray(String json) {
        List<Map<String, String>> resultado = new ArrayList<>();
        int i = 0;
        while (i < json.length() && json.charAt(i) != '[') i++;
        i++;
        while (i < json.length()) {
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
            if (i >= json.length() || json.charAt(i) == ']') break;
            if (json.charAt(i) == '{') {
                int inicio = i;
                int prof = 0;
                boolean emStr = false;
                while (i < json.length()) {
                    char c = json.charAt(i);
                    if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) emStr = !emStr;
                    if (!emStr) {
                        if (c == '{') prof++;
                        else if (c == '}') { prof--; if (prof == 0) { i++; break; } }
                    }
                    i++;
                }
                resultado.add(parseObject(json.substring(inicio, i)));
            }
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
        }
        return resultado;
    }

    private static Map<String, String> parseObject(String json) {
        Map<String, String> mapa = new LinkedHashMap<>();
        int i = 0;
        while (i < json.length() && json.charAt(i) != '{') i++;
        i++;
        while (i < json.length()) {
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
            if (i >= json.length() || json.charAt(i) == '}') break;
            if (json.charAt(i) != '"') { i++; continue; }
            i++;
            StringBuilder chave = new StringBuilder();
            while (i < json.length() && json.charAt(i) != '"') {
                if (json.charAt(i) == '\\') i++;
                if (i < json.length()) chave.append(json.charAt(i++));
            }
            i++;
            while (i < json.length() && (json.charAt(i) == ':' || Character.isWhitespace(json.charAt(i)))) i++;
            if (i >= json.length()) break;
            char c = json.charAt(i);
            String valor;
            if (c == '"') {
                i++;
                StringBuilder sb = new StringBuilder();
                while (i < json.length() && json.charAt(i) != '"') {
                    if (json.charAt(i) == '\\') {
                        i++;
                        if (i < json.length()) {
                            char esc = json.charAt(i);
                            if      (esc == 'n')  sb.append('\n');
                            else if (esc == 't')  sb.append('\t');
                            else if (esc == '"')  sb.append('"');
                            else if (esc == '\\') sb.append('\\');
                            else if (esc == 'r')  sb.append('\r');
                            else                  sb.append(esc);
                        }
                    } else { sb.append(json.charAt(i)); }
                    i++;
                }
                i++;
                valor = sb.toString();
            } else if (c == '[') {
                int inicio = i; int prof = 0; boolean emS = false;
                while (i < json.length()) {
                    char ch = json.charAt(i);
                    if (ch == '"' && (i == 0 || json.charAt(i - 1) != '\\')) emS = !emS;
                    if (!emS) {
                        if (ch == '[') prof++;
                        else if (ch == ']') { prof--; if (prof == 0) { i++; break; } }
                    }
                    i++;
                }
                valor = json.substring(inicio, i);
            } else if (Character.isDigit(c) || c == '-') {
                StringBuilder sb = new StringBuilder();
                while (i < json.length() && "0123456789.-".indexOf(json.charAt(i)) >= 0)
                    sb.append(json.charAt(i++));
                valor = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder();
                while (i < json.length() && Character.isLetter(json.charAt(i)))
                    sb.append(json.charAt(i++));
                valor = sb.toString();
            }
            mapa.put(chave.toString(), valor);
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
        }
        return mapa;
    }

    public static List<String> parseStringArray(String json) {
        List<String> resultado = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return resultado;
        int i = 0;
        while (i < json.length() && json.charAt(i) != '[') i++;
        i++;
        while (i < json.length()) {
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
            if (i >= json.length() || json.charAt(i) == ']') break;
            if (json.charAt(i) == '"') {
                i++;
                StringBuilder sb = new StringBuilder();
                while (i < json.length() && json.charAt(i) != '"') {
                    if (json.charAt(i) == '\\') i++;
                    if (i < json.length()) sb.append(json.charAt(i++));
                }
                i++;
                resultado.add(sb.toString());
            }
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
        }
        return resultado;
    }
}
