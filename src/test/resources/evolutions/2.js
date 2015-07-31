// --- !Ups
db.collection.find({
  'name': 'aname'
});

// --- !Downs
db.collection.find({

  'name': 'aname'
});