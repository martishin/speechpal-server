package co.speechpal.server.common.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "common")
class CommonProperties {
    lateinit var sqlSchema: String
}
