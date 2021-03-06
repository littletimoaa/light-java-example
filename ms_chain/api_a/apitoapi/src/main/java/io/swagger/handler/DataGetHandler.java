package io.swagger.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.client.Client;
import com.networknt.config.Config;
import com.networknt.exception.ClientException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

public class DataGetHandler implements HttpHandler {
    static String CONFIG_NAME = "api_a";
    static String apibUrl = (String)Config.getInstance().getJsonMapConfig(CONFIG_NAME).get("api_b_endpoint");

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        List<String> list = new ArrayList<String>();
        try {
            CloseableHttpClient client = Client.getInstance().getSyncClient();
            HttpGet httpGet = new HttpGet(apibUrl);
            CloseableHttpResponse response = client.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode != 200){
                throw new Exception("Failed to call API B: " + responseCode);
            }
            List<String> apicList = (List<String>) Config.getInstance().getMapper().readValue(response.getEntity().getContent(),
                    new TypeReference<List<String>>(){});
            list.addAll(apicList);
        } catch (ClientException e) {
            throw new Exception("Client Exception: ", e);
        } catch (IOException e) {
            throw new Exception("IOException:", e);
        }
        // now add API B specific messages
        list.add("API A: Message 1");
        list.add("API A: Message 2");
        exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(list));
    }
}
