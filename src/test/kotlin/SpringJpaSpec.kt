package uk.org.lidalia.springjpatest

import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.sql.Connection
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.hibernate.dialect.PostgreSQL10Dialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.PlatformTransactionManager


@ContextConfiguration(classes = [(Initializer::class)])
class SpringJpaSpec(
  dataSnapshotRepository: DataSnapshotRepository,
  entityManager: EntityManager,
) : StringSpec({

  "check we can save and retrieve" {

    val data2 = dataSnapshotRepository.saveAndFlush(DataSnapshot("a", 2, "a2"))
    val data1 = dataSnapshotRepository.saveAndFlush(DataSnapshot("a", 1, "a1"))
    entityManager.clear()

    dataSnapshotRepository.findAll().shouldContainExactlyInAnyOrder(data1, data2)
    dataSnapshotRepository.findLatestById("a").shouldBe(data2)
    dataSnapshotRepository.findFirstByIdOrderByRevisionDesc("a").shouldBe(data2)
  }
}) {
  override fun extensions() = listOf(SpringExtension)

  companion object {
    @Suppress("unused")
    @JvmStatic
    fun flywayInit(connection: Connection) {
      Flyway.configure()
        .dataSource(SingleConnectionDataSource(connection, true))
        .load().migrate()
    }
  }
}

@Configuration
@EnableJpaRepositories
class Initializer {

  @Bean
  fun dataSource(): DataSource = DriverManagerDataSource("jdbc:tc:postgresql:13.2-alpine:///test?TC_DAEMON=true&TC_INITFUNCTION=uk.org.lidalia.springjpatest.SpringJpaSpec::flywayInit")

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
