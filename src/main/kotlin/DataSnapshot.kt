package uk.org.lidalia.springjpatest

import java.io.Serializable
import javax.persistence.*
import org.hibernate.annotations.Immutable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Entity
@Immutable
@Table(name = "data_snapshot")
data class DataSnapshot(
  @EmbeddedId
  val pk: DataSnapshotId,
  @Column
  val data: String,
) {
  constructor(id: String, revision: Int, data: String): this(DataSnapshotId(id, revision), data)
}

@Embeddable
data class DataSnapshotId(val id: String, val revision: Int) : Serializable

interface DataSnapshotRepository : JpaRepository<DataSnapshot, DataSnapshotId> {
  // returns the latest snapshot for an id
  fun findFirstByPkIdOrderByPkRevisionDesc(id: String): DataSnapshot?

  // also returns the latest snapshot for an id
  @Query(
    value = "select * from latest_data_snapshot where id = :id",
    nativeQuery = true
  )
  fun findLatestById(@Param("id") id: String): DataSnapshot?
}
