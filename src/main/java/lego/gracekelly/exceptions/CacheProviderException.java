package lego.gracekelly.exceptions;

/**
 * Exceptions thrown during CacheProvider operations
 */
public class CacheProviderException extends Exception{

    public  CacheProviderException(){
        super();
    }

    public CacheProviderException(String message){
        super(message);
    }

    public CacheProviderException(Throwable cause){
        super(cause);
    }

    public CacheProviderException(String message, Throwable cause){
        super(message,cause);
    }

}
