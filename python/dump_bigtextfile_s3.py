# nohup sh -c "aws s3 cp s3://mongo-data/backup.bson.gz - | gunzip -c | bsondump | python3 counter.py" &
import boto3
import fileinput
import json
import os
from datetime import datetime

s3 = boto3.client('s3')

try:
    for line in fileinput.input():
        doc = json.loads(line)

        with open('files/dump.json', 'a') as outfile:
            outfile.write('{}\n'.format(json.dumps(doc)))

        if (os.path.getsize('files/dump.json') > 1000000000):
            try:
                s3.upload_file(
                    'files/dump.json',
                    'bucket.name',
                    'path/to/key/' + str(datetime.now().timestamp()) + '.json'
                )
                os.remove('files/dump.json')
            except:
                continue

except:
    s3.upload_file(
        'files/dump.json',
        'bucket.name',
        'path/to/key/' + str(datetime.now().timestamp()) + '.json'
    )
    os.remove('files/dump.json')
