db.runCommand({
  'findAndModify': '%s',
  'update': {
    '$set': {
      'locked': true
    }
  },
  'upsert': true,
  'new': true
});