from openai import AsyncOpenAI
from dotenv import load_dotenv
import os

load_dotenv(verbose=True)
api_key = os.getenv("API_KEY")

gpt_client = AsyncOpenAI(api_key = api_key)
gpt_model = "gpt-4o-mini"

prompt = """
다음의 두 모델을 비교해줘
"""

client = AsyncOpenAI(api_key = api_key)

async def get_gpt_answer(target_layer : str, searched_layer : str):

    chat_completion = await client.chat.completions.create(
        model= gpt_model,
        messages=[{"role": "user", "content": prompt + "<target> {} \n <searched> {}".format(target_layer, searched_layer) }]
    )

    return chat_completion.choices[0].message.content