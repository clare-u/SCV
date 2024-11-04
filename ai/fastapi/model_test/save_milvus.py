from dotenv import load_dotenv
from pymilvus import connections, db, FieldSchema, MilvusClient
import os

load_dotenv(verbose=True)
db_name = os.getenv("DB_NAME")
collection_name = os.getenv("COLLECTION_NAME")
milvus_host_name = os.getenv("MILVUS_HOST_NAME")
milvus_port = os.getenv("MILVUS_PORT")

client = MilvusClient(
    uri="http://{}:{}".format(milvus_host_name, milvus_port),
    db_name=db_name
)

client.load_collection(
    collection_name=collection_name
)

def save_cka_to_milvus():
    