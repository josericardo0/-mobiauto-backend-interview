package exceptionhandler;

public class AcessoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AcessoException(String msg) {
        super(msg);
    }

}
