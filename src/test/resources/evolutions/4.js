// --- !Ups
db.collection.update({}, { '$set': { 'sth': 'AName' } }, { 'multi': true });