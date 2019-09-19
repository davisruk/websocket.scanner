package co.uk.boots.websocket.scanner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	public void configureMessageBroker (MessageBrokerRegistry reg) {
		reg.enableSimpleBroker("/topic");
		reg.setApplicationDestinationPrefixes("/app");
	}
	
	public void registerStompEndpoints (StompEndpointRegistry reg) {
		reg.addEndpoint("/ws").setAllowedOrigins("http://localhost:4200").withSockJS();
	}
}
