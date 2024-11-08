import logging
import logging.config
from pathlib import Path
from ..config.settings import LOG_CONFIG


def setup_logger(name: str) -> logging.Logger:
    """로거 설정 및 반환

    Args:
        name: 로거 이름

    Returns:
        설정된 로거 인스턴스
    """
    # logs 디렉토리가 없으면 생성
    log_dir = Path(__file__).resolve().parent.parent.parent / 'logs'
    log_dir.mkdir(exist_ok=True)

    # 로깅 설정 적용
    logging.config.dictConfig(LOG_CONFIG)

    # 로거 가져오기
    logger = logging.getLogger(name)

    return logger