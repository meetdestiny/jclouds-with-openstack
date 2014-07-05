package com.fishandlime.jclouds;

import java.io.IOException;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;

/**
 * Hello world!
 *
 */
public class App 
{
	private final NovaApi novaApi;
    private final Set<String> zones;

    public static void main(String[] args) throws IOException {
        App app = new App();

        try {
            app.listServers();
            app.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            app.close();
        }
    }

    public App() {
        Iterable<SLF4JLoggingModule> modules = ImmutableSet.of(new SLF4JLoggingModule());

        String provider = "openstack-nova";
        String identity = "admin:admin"; // tenantName:userName
        String credential = "b16f62e43b8e4e68";

        novaApi = ContextBuilder.newBuilder(provider)
                .endpoint("http://192.168.1.154:5000/v2.0/")
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(NovaApi.class);
        zones = novaApi.getConfiguredZones();
    }

    private void listServers() {
        for (String zone : zones) {
            ServerApi serverApi = novaApi.getServerApiForZone(zone);

            System.out.println("Servers in " + zone);

            for (Server server : serverApi.listInDetail().concat()) {
                System.out.println(" Server: " + server);
            }
        }
    }

    public void close() throws IOException {
        Closeables.close(novaApi, true);
    }
}
