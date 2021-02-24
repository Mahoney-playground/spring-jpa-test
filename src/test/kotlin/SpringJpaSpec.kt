package uk.org.lidalia.springjpatest

import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import org.springframework.test.context.ContextConfiguration
import java.sql.Connection
import javax.persistence.EntityManager
import javax.sql.DataSource


@ContextConfiguration
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

  @Configuration
  @ComponentScan
  @Suppress("unused")
  class Initializer {

    @Bean
    fun dataSource(): DataSource = DriverManagerDataSource(
      "jdbc:tc:postgresql:13.2-alpine:///test?TC_DAEMON=true&TC_INITFUNCTION=uk.org.lidalia.springjpatest.FlywayInit::flywayInit"
    )
  }
}

@Suppress("unused")
object FlywayInit {
  @JvmStatic
  fun flywayInit(connection: Connection) {
    Flyway.configure()
      .dataSource(SingleConnectionDataSource(connection, true))
      .load().migrate()
  }
}
