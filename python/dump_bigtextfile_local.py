# nohup sh -c "cat backup.bson.gz | gunzip -c | bsondump | python3 extract.py" &
import fileinput
import orjson
from datetime import datetime

for line in fileinput.input():
    try:
        doc = orjson.loads(line)
        entid = doc['entidade_id']
        doctype = doc['document_type']
        day = datetime.today().day
        hour = datetime.today().hour
        with open('results/{}-{}_{}-{}.json'.format(entid, doctype, day, hour), 'a') as outfile:
            outfile.write('{}\n'.format(orjson.dumps(doc)))
    except:
        continue
