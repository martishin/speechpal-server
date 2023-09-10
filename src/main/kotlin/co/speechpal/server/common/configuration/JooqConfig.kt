package co.speechpal.server.common.configuration

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy

@Configuration
class JOOQConfig(
    private val commonProperties: CommonProperties,
) {
    @Bean
    fun dslContext(connectionFactory: ConnectionFactory): DSLContext {
        val dslContext = DSL.using(TransactionAwareConnectionFactoryProxy(connectionFactory), SQLDialect.POSTGRES)
        dslContext.setSchema(commonProperties.sqlSchema)
        return dslContext
    }
}
