from pathlib import Path

# Project Base Path
BASE_DIR = Path(__file__).resolve().parent.parent.parent

# Logging Configuration
LOG_CONFIG = {
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'standard': {
            'format': '%(asctime)s [%(levelname)s] %(name)s: %(message)s'
        },
    },
    'handlers': {
        'default': {
            'level': 'INFO',
            'formatter': 'standard',
            'class': 'logging.StreamHandler',
        },
        'file': {
            'level': 'INFO',
            'formatter': 'standard',
            'class': 'logging.FileHandler',
            'filename': BASE_DIR / 'logs' / 'model_builder.log',
            'mode': 'a',
        },
    },
    'loggers': {
        '': {  # root logger
            'handlers': ['default', 'file'],
            'level': 'INFO',
            'propagate': True
        }
    }
}

# Supported Layer Types and Their Required Parameters
LAYER_CONFIGS = {
    'conv2d': {
        'required': ['in_channels', 'out_channels', 'kernel_size'],
        'optional': ['stride', 'padding', 'dilation', 'groups', 'bias']
    },
    'maxpool2d': {
        'required': ['kernel_size'],
        'optional': ['stride', 'padding', 'dilation', 'return_indices', 'ceil_mode']
    },
    'relu': {
        'required': [],
        'optional': ['inplace']
    },
    'linear': {
        'required': ['in_features', 'out_features'],
        'optional': ['bias']
    }
}