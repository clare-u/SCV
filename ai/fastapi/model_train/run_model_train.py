import subprocess
import time
import os
import requests
import signal


def wait_for_server(url="http://localhost:8001/docs", timeout=30):
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            requests.get(url)
            return True
        except requests.exceptions.ConnectionError:
            time.sleep(1)
    return False


def main():
    original_dir = os.getcwd()
    server_process = None

    try:
        # 절대 경로 설정
        project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        os.environ["PYTHONPATH"] = project_root

        # Mac/Linux에서 서버 시작
        server_process = subprocess.Popen(
            [
                "python", "-m", "uvicorn", "model_train.app:app", "--reload", "--port", "8001"
            ],
            env={**os.environ, "PYTHONPATH": os.environ.get("PYTHONPATH", "") + ":."})

        if not wait_for_server():
            print("서버 실행이 실패했습니다!")
            return

        print("서버가 실행되었습니다! 테스트 시작...")

        test_process = subprocess.run(
            ["python", "model_train/test_api.py"],
            check=True
        )

    except subprocess.CalledProcessError as e:
        print(f"Error running tests: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")
    finally:
        if server_process:
            # Mac/Linux에서 프로세스와 자식 프로세스들 종료
            try:
                # 프로세스 그룹 전체 종료
                os.killpg(os.getpgid(server_process.pid), signal.SIGTERM)
                server_process.wait(timeout=5)  # 5초 동안 종료 대기
            except:
                # 만약 여전히 살아있다면 강제 종료
                try:
                    os.killpg(os.getpgid(server_process.pid), signal.SIGKILL)
                except:
                    pass

        os.chdir(original_dir)


if __name__ == "__main__":
    main()

# Windows 용
# import subprocess
# import time
# import os
# import requests
#
#
# def wait_for_server(url="http://localhost:8001/docs", timeout=30):  # 포트 변경
#     start_time = time.time()
#     while time.time() - start_time < timeout:
#         try:
#             requests.get(url)
#             return True
#         except requests.exceptions.ConnectionError:
#             time.sleep(1)
#     return False
#
# def main():
#     original_dir = os.getcwd()
#
#     try:
#         # 포트 8001로 변경
#         server_process = subprocess.Popen(
#             [
#                 "python", "-m", "uvicorn", "model_train.app:app", "--reload", "--port", "8001"
#             ],
#             env={**os.environ, "PYTHONPATH": os.environ.get("PYTHONPATH", "") + ":."},
#             creationflags=subprocess.CREATE_NEW_PROCESS_GROUP
#         )
#
#         if not wait_for_server():
#             print("Server failed to start")
#             return
#
#         print("Server is ready! Starting tests...")
#
#         test_process = subprocess.run(
#             ["python", "model_train/test_api.py"],
#             check=True
#         )
#
#     except subprocess.CalledProcessError as e:
#         print(f"Error running tests: {e}")
#     except Exception as e:
#         print(f"Unexpected error: {e}")
#     finally:
#         if 'server_process' in locals():
#             subprocess.run(['taskkill', '/F', '/T', '/PID', str(server_process.pid)])
#
#         os.chdir(original_dir)
#
#
# if __name__ == "__main__":
#     main()