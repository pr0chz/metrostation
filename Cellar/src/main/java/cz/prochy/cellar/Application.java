/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.prochy.cellar;

import com.github.ziplet.filter.compression.CompressingFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application implements EmbeddedServletContainerCustomizer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        JettyEmbeddedServletContainerFactory factory = (JettyEmbeddedServletContainerFactory)container;
        factory.setPort(48989);
        factory.setSessionTimeout(20, TimeUnit.SECONDS);
        factory.setServerCustomizers(Collections.singletonList(server -> {
            final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
            threadPool.setMaxThreads(10);
            threadPool.setMinThreads(10);
            threadPool.setIdleTimeout(20);
        }));
    }

    @Bean
    public Filter gzipFilter() {
        return new CompressingFilter();
    }
}
