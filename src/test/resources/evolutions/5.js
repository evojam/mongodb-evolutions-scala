// --- !Ups
db.collection.update({}, { '$set': { 'sth': 'AName' } }, { 'multi': true });

// --- !Downs
db.collection.update({}, { '$unset': { 'sth': true } }, { 'multi': true });