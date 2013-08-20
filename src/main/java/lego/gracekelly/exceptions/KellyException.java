package lego.gracekelly.exceptions;

/**
 * Exceptions thrown during Kelly operations
 */
public class KellyException extends Exception{

    public  KellyException(){
        super();
    }

    public KellyException(String message){
        super(message);
    }

    public KellyException(Throwable cause){
        super(cause);
    }

    public KellyException(String message, Throwable cause){
        super(message,cause);
    }

}
