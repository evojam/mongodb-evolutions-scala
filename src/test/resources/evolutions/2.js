// --- !Ups
db.database.find({
  'name': 'aname'
});

// --- !Downs
db.database.find({

  'name': 'aname'
});