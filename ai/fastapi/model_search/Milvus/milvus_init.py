from pymilvus import connections, db, FieldSchema, CollectionSchema, Collection, DataType, MilvusClient

conn = connections.connect(host="localhost", port=19530)

if not "test" in db.list_database():
    database = db.create_database("test")

client = MilvusClient(
    uri="http://localhost:19530",
    db_name="test"
)

db.using_database("test")

id_field = FieldSchema(
    name="model_version_layer_id", 
    dtype=DataType.VARCHAR, 
    max_length=30,
    is_primary=True, 
    description="model, version, layer id 를 concat 해서 사용")

accuracy_field = FieldSchema(
    name="test_accuracy", 
    dtype=DataType.FLOAT, 
    description="test 정확도")

layer_field = FieldSchema(
    name="layers",
    dtype=DataType.VARCHAR,
    max_length=1024,
    description="모델의 레이어 정보를 담은 JSON 파일"
)

vector_field = FieldSchema(
    name="cka_vec",
    dtype=DataType.FLOAT_VECTOR,
    dim=3,
    description="cka 행렬 X 의 XX^T 를 취한 후에 Frobenius Norm으로 나눈 값")


schema = CollectionSchema(fields=[id_field, accuracy_field, layer_field, vector_field], description="collection with cka. pk is model_version_layer_id")

index_params = client.prepare_index_params()

index_params.add_index(
    field_name="model_version_layer_id",
    index_type="INVERTED"
)

index_params.add_index(
    field_name="cka_vec", 
    index_type="FLAT",
    metric_type="IP",
)

client.create_collection(
    collection_name="cka_collection",
    schema=schema,
    index_params=index_params
)

client.release_collection(
    collection_name="cka_collection"
)