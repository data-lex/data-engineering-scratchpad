def get_jsonlines_s3(role, bucket, key):
    client = boto3.client('s3')
    response = client.get_object(Bucket=bucket, Key=key)
    body = response['Body'].read().decode()
    dataset = [json.loads(line) for line in body.splitlines()]
    return dataset
