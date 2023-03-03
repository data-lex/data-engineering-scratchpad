from bson.json_util import dumps
from datetime import datetime
import boto3
import logging
import pymongo
import sys


# Connecting to MongoDB and retrieving the specified collection
client = pymongo.MongoClient('')
db = client['database']
coll = db['collection']
data = ''
s3 = boto3.resource('s3')
bucket = s3.Bucket('bucket')

try:
    with coll.watch([
     {'$match': {'operationType': {'$in': ["insert", "update", "replace", "delete"]}}},
     {'$project': {'fullDocument': "$fullDocument"}}],
     full_document="updateLookup", batch_size=5) as stream:
        for change in stream:
            data += dumps(change) + '\n'
            print(data)
            if sys.getsizeof(data) > 102400:
                bucket.put_object(
                    Key=str(datetime.datetime.now().timestamp()) + '.json',
                    Body=data)
                data = ''
except pymongo.errors.PyMongoError:
    logging.error(
        'Streamer encountered an error or the resume attempt failed.')
