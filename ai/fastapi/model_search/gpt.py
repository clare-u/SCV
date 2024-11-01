from openai import AsyncOpenAI
from dotenv import load_dotenv
import os

load_dotenv(verbose=True)
api_key = os.getenv("API_KEY")

gpt_client = AsyncOpenAI(api_key = api_key)
gpt_model = "gpt-4o-mini"

prompt = """
-- Compare <target> model and <searched> model
-- first, compare them in their "Structures"
-- second, compare them in their "Performance", guess the reason why their performances were different.
-- third, with the point that <target> model's <target_layer>th layer(starts from 0) is turned out to be similar with <searched> model's <searched_layer>th layer(starts from 0) by the CKA(centered kernel alignment),
    tell me anything helpful to develop <target> model. guess the reason why two layers were similar.
-- There are no more information to give, so don't ask for further information
-- translate all to Korean.
"""

client = AsyncOpenAI(api_key = api_key)

async def get_gpt_answer(target_layer : str, searched_layer : str, target_layer_id, searched_layer_id, target_test_accuracy, searched_test_accuracy):

    chat_completion = await client.chat.completions.create(
        model= gpt_model,
        messages=[{"role": "user", 
        "content": prompt + "<target> {} <target test accuracy> {} \n <searched> {} <searched model test accuracy> {}".format(target_layer, target_test_accuracy, searched_layer, searched_test_accuracy) }]
    )

    return chat_completion.choices[0].message.content