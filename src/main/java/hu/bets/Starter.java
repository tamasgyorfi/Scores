package hu.bets;

import hu.bets.common.messaging.MessageListener;
import hu.bets.config.ApplicationConfig;
import hu.bets.config.DatabaseConfig;
import hu.bets.config.MessagingConfig;
import hu.bets.config.WebConfig;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Starter {

    private static final Logger LOGGER = Logger.getLogger(Starter.class);

    private ApplicationContext context = new AnnotationConfigApplicationContext(
            ApplicationConfig.class,
            MessagingConfig.class,
            WebConfig.class,
            DatabaseConfig.class);

    public static void main(String[] args) {
        Starter starter = new Starter();

        starter.startMessaging(starter.context.getBean(MessageListener.class));
        starter.startServer(starter.context.getBean(Server.class));
    }

    private void startMessaging(MessageListener messageListener) {
        try {
            messageListener.receive();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void startServer(Server server) {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            LOGGER.error("Unable to start the embedded server.", e);
        }
    }
}