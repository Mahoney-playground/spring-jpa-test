create table data_snapshot(
    id varchar(44) not null,
    revision integer not null,
    data text not null,
    primary key (id, revision)
);

create view latest_data_snapshot(id, revision, data) as
select data_snapshot.id, data_snapshot.revision, data_snapshot.data
from data_snapshot
join (
  select id, max(revision) as latest_revision
  from data_snapshot
  group by id
) latest_snapshot
on data_snapshot.id = latest_snapshot.id and data_snapshot.revision = latest_snapshot.latest_revision;
