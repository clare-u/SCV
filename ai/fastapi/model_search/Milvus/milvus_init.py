from pymilvus import connections, db, FieldSchema, CollectionSchema, Collection, DataType

conn = connections.connect(host="localhost", port=19530)

database = None

if not "scv_database" in db.list_database():
    database = db.create_database("scv_database")

db.using_database("scv_database")

id_field = FieldSchema(
    name="model_version_layer_id", 
    dtype=DataType.VARCHAR, 
    max_length=30,
    is_primary=True, 
    description="model, version, layer id 를 concat 해서 사용")

vector_field = FieldSchema(
    name="cka_vec",
    dtype=DataType.FLOAT_VECTOR,
    dim=10000,
    description="cka 행렬 X 의 XX^T 를 취한 후에 Frobenius Norm으로 나눈 값")


cka_collection = Collection(
    name="cka_collection", 
    schema=CollectionSchema(fields=[id_field, vector_field], description="collection with cka. pk is model_version_layer_id"))