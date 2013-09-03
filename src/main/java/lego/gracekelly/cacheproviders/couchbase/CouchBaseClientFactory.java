/**
 * Copyright 2013 Flipkart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
     * @param urls
     * @param bucketName
     * @param password
     * @return {@link CouchbaseClient} instance.
     * @throws IOException
     */
    public static CouchbaseClient getCouchBaseClient(String[] urls, String bucketName, String password) throws IOException {

        List<URI> uris = new ArrayList<URI>();
        for (String url: urls){
            uris.add(URI.create(url));
        }

        CouchbaseConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder();
        CouchbaseClient couchbaseClient = new CouchbaseClient(builder.buildCouchbaseConnection(uris,bucketName,password));
        return couchbaseClient;
    }

}
