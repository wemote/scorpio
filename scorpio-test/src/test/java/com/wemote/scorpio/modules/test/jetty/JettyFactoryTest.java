package com.wemote.scorpio.modules.test.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JettyFactoryTest {

    @Test
    public void createServer() {
        Server server = JettyFactory.createServerInSource(1978, "/test");

        assertThat(server.getConnectors()[0].getPort()).isEqualTo(1978);
        assertThat(((WebAppContext) server.getHandler()).getContextPath()).isEqualTo("/test");
        assertThat(((WebAppContext) server.getHandler()).getWar()).isEqualTo("src/main/webapp");
    }
}
