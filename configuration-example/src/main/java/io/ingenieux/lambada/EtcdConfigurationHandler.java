package io.ingenieux.lambada;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import io.ingenieux.lambada.runtime.LambadaFunction;
import jetcd.EtcdClient;
import jetcd.EtcdClientFactory;

public class EtcdConfigurationHandler {
    private final EtcdClient etcdService;

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper()
                    .enable(SerializationFeature.INDENT_OUTPUT);

    @LambadaFunction(name = "lb_etcdConfiguration", timeout = 45)
    public void showEtcdConfiguration(InputStream inputStream, OutputStream outputStream) throws Exception {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();

        for (Map.Entry<String, String> e : etcdService.list("/config").entrySet()) {
            objectNode.put(e.getKey(), e.getValue());
        }

        OBJECT_MAPPER.writeValue(outputStream, objectNode);
    }

    public EtcdConfigurationHandler() throws Exception {
        Properties p = new Properties();

        p.load(getClass().getResourceAsStream("config.properties"));

        String etcdUrl = p.getProperty("etcdUrl");

        this.etcdService = EtcdClientFactory.newInstance(etcdUrl);
    }

    public static void main(String[] args) throws Exception {
        EtcdConfigurationHandler handler = new EtcdConfigurationHandler();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        handler.showEtcdConfiguration(null, baos);

        System.err.println("Output: " + new String(baos.toByteArray()));
    }
}
