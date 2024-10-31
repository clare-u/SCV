from setuptools import setup, find_packages

setup(
    name="neural_network_builder",
    version="0.1",
    packages=find_packages(),
    install_requires=[
        'torch',
        'pytest',
        'pydantic'
    ]
)