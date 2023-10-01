package classes;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Analisador {
    private List<TokenRepresent> tokens;
    private String codigo;
    public Analisador(String codigo) {
        this.codigo = codigo;
        tokens = new ArrayList<>();
    }

    public List<TokenRepresent> analise() {

        Pattern patternPalavraReservada = Pattern.compile("(int|float|char|boolean|void|if|else|for|while|scanf|println|main|return|switch|case)");
        Pattern patternId = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
        Pattern patternTexto = Pattern.compile("\"[^\"]*\"");
        Pattern patternNumDec = Pattern.compile("\\d+\\.\\d+");
        Pattern patternNumInt = Pattern.compile("\\d+");
        Pattern patternComentario = Pattern.compile("//.*");
        Pattern patternOperadores = Pattern.compile("[=+\\-*/%&|!]");
        Pattern patternOperadoresComparacao = Pattern.compile("(==|!=|>=|<=|>|<)");
        Pattern patternOperadoresEspeciais = Pattern.compile("[(){};,]");



        String regexEspacoEmBranco = "[ \\t]+$";
        Pattern patternEspacoEmBranco = Pattern.compile(regexEspacoEmBranco, Pattern.MULTILINE);

        //espacos em branco ou tabulacoes no final da linha
        Matcher matcherEspacoEmBranco = patternEspacoEmBranco.matcher(codigo);

        //remove espaco em branco ou tabulacoes no final da linha
        String removeEspaco = matcherEspacoEmBranco.replaceAll("");

        // armazenar o conteúdo do arquivo
        StringBuffer arquivoBuffer = new StringBuffer();

        // Lê o código linha a linha e adiciona ao StringBuffer
        String[] lines = removeEspaco.split("\n");
        for (String line : lines) {
            arquivoBuffer.append(line).append('\n');
            // Analise cada linha do código-fonte
            Matcher matcher;


            matcher = patternId.matcher(line);
            while (matcher.find()) {
                tokens.add(new TokenRepresent(Token.ID, matcher.group()));
            }

            matcher = patternPalavraReservada.matcher(line);
            while (matcher.find()) {
                tokens.add(new TokenRepresent(Token.PALAVRA_RESERVADA, matcher.group()));
            }


            matcher = patternTexto.matcher(line);
            while (matcher.find()) {
                tokens.add(new TokenRepresent(Token.TEXTO, matcher.group()));
            }

            matcher = patternNumDec.matcher(line);
            while (matcher.find()) {
                tokens.add(new TokenRepresent(Token.NUM_DEC, matcher.group()));
            }
            matcher = patternNumInt.matcher(line);
            while (matcher.find()) {
                while (matcher.find()) {
                    tokens.add(new TokenRepresent(Token.NUM_INT, matcher.group()));
                }
            }


            matcher = patternComentario.matcher(line);
            while (matcher.find()) {
                tokens.add(new TokenRepresent(Token.COMENTARIOS, matcher.group()));
            }

            matcher = patternOperadores.matcher(line);
            while (matcher.find()) {
                tokens.add(new TokenRepresent(Token.OPERADORES, matcher.group()));
            }

            matcher = patternOperadoresComparacao.matcher(line);
            while (matcher.find()) {
                tokens.add(new TokenRepresent(Token.OPERADORES_COMPARACAO, matcher.group()));
            }

            matcher = patternOperadoresEspeciais.matcher(line);
            while (matcher.find()) {
                tokens.add(new TokenRepresent(Token.SIMBOLOS_ESPECIAIS, matcher.group()));
            }

        }

        return tokens;
    }



    public static void main(String[] args) {
        String caminhoArquivo = "teste.pdf";

        try {
            // Extrai o texto do arquivo usando o Apache Tika
            String codigo = extrairTextoDoArquivo(caminhoArquivo);

            Analisador lexer = new Analisador(codigo);
            List<TokenRepresent> tokens = lexer.analise();

            // Exibe os tokens identificados
            for (TokenRepresent token : tokens) {
                System.out.println(" Lexema: " + token.getLexema() +", Tipo: " + token.getTipo()  );
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public static String extrairTextoDoArquivo(String caminhoArquivo) throws IOException {
        // Código para extrair o texto do arquivo usando o Apache Tika
        try {
            File arquivo = new File(caminhoArquivo);
            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();

            parser.parse(arquivo.toURI().toURL().openStream(), handler, metadata, parseContext);

            return handler.toString();
        } catch (SAXException | TikaException e) {
            throw new IOException("Erro ao extrair texto do arquivo: " + e.getMessage());
        }
    }
}