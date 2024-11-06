import logging
from typing import Optional


def setup_logger(name: Optional[str] = None) -> logging.Logger:
    """
    공통 로거 설정

    Args:
        name: 로거 이름 (None일 경우 root 로거 사용)
    """
    logger = logging.getLogger(name)

    # 이미 핸들러가 설정되어 있다면 추가 설정하지 않음
    if logger.handlers:
        return logger

    logger.setLevel(logging.INFO)

    # 콘솔 핸들러 추가
    console_handler = logging.StreamHandler()
    console_handler.setLevel(logging.INFO)

    # 포맷터 설정
    formatter = logging.Formatter(
        '%(asctime)s [%(levelname)s] %(name)s: %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S'
    )
    console_handler.setFormatter(formatter)

    logger.addHandler(console_handler)

    return logger


# 기본 로거 생성
logger = setup_logger('model_train')