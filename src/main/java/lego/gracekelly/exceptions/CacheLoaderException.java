package lego.gracekelly.exceptions;

/**
 * Exceptions thrown during CacheLoader operations
 */
public class CacheLoaderException extends Exception{

    public  CacheLoaderException(){
        super();
    }

    public CacheLoaderException(String message){
        super(message);
    }

    public CacheLoaderException(Throwable cause){
        super(cause);
    }

    public CacheLoaderException(String message, Throwable cause){
        super(message,cause);
    }

}
