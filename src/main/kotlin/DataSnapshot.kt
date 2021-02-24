package uk.org.lidalia.springjpatest

import java.io.Serializable
import javax.persistence.*
import org.hibernate.annotations.Immutable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Entity
@Immutable
@IdClass(DataSnapshotId::class)
@Table(name = "data_snapshot")
data class DataSnapshot(

  @Id
  val id: String,

  @Id
  val revision: Int,

  @Column
  val data: String,
)

@Embeddable
data class DataSnapshotId(val id: String, val revision: Int) : Serializable

interface DataSnapshotRepository : JpaRepository<DataSnapshot, DataSnapshotId> {

  // returns the latest snapshot for an id
  fun findFirstByIdOrderByRevisionDesc(id: String): DataSnapshot?

  // also returns the latest snapshot for an id
  @Query(
    value = "select * from latest_data_snapshot where id = :id",
    nativeQuery = true
  )
  fun findLatestById(@Param("id") id: String): DataSnapshot?
}
