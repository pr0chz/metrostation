package cz.prochy.cellar;

import com.github.ziplet.filter.compression.CompressingFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;
import java.util.Arrays;
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
        factory.setServerCustomizers(Arrays.asList(new JettyServerCustomizer() {
            @Override
            public void customize(Server server) {
                final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
                threadPool.setMaxThreads(10);
                threadPool.setMinThreads(10);
                threadPool.setIdleTimeout(20);
            }
        }));
    }

    @Bean
    public Filter gzipFilter() {
        return new CompressingFilter();
    }
}
