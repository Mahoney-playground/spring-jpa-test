package uk.org.lidalia.springjpatest

import org.hibernate.dialect.PostgreSQL10Dialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories
class PersistenceConfiguration {

    @Bean
    fun entityManagerFactory(
        dataSource: DataSource
    ) = LocalContainerEntityManagerFactoryBean().apply {
        this.dataSource = dataSource
        this.setPackagesToScan("uk.org.lidalia.springjpatest")
        this.jpaVendorAdapter = HibernateJpaVendorAdapter()
        this.jpaPropertyMap = mapOf(
            "hibernate.hbm2ddl.auto" to "validate",
            "hibernate.dialect" to PostgreSQL10Dialect()
        )
    }

    @Bean
    fun transactionManager(
        entityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager =
        JpaTransactionManager().apply {
            this.entityManagerFactory = entityManagerFactory
        }
}
