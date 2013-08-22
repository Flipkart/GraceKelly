package lego.gracekelly.cacheproviders.couchbase;


import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a CouchbaseClient
 */
public class CouchBaseClientFactory {

    /**
     * Creates and returns {@link CouchbaseClient} based on the parameters provided.
     * @param url
     * @param bucketName
     * @param password
     * @return {@link CouchbaseClient} instance.
     * @throws IOException
     */
    public static CouchbaseClient getCouchBaseClient(String url, String bucketName, String password) throws IOException {

        List<URI> uris = new ArrayList<URI>();
        uris.add(URI.create(url));

        CouchbaseConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder();
        CouchbaseClient couchbaseClient = new CouchbaseClient(builder.buildCouchbaseConnection(uris,bucketName,password));
        return couchbaseClient;
    }

}
