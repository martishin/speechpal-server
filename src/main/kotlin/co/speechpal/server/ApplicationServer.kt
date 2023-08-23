package co.speechpal.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(
    basePackages = [
        "co.speechpal.server.api",
        "co.speechpal.server.bot",
        "co.speechpal.server.common",
    ],
)
class ApplicationServer
