/**
 * Copyright 2013 Flipkart Internet, pvt ltd.
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
