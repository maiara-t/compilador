package classes;

public class TokenRepresent {

    private Token tipo;
    private String lexema;

    public TokenRepresent() {
    }

    public TokenRepresent(Token tipo, String lexema) {
        this.tipo= tipo;
        this.lexema = lexema;
    }
    public Token getTipo() {
        return tipo;
    }
    public void setTipo(Token tipo){
        this.tipo = tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public void setTexto(String lexema){
        this.lexema= lexema;
    }
}

