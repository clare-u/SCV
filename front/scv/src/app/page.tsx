"use client";

import { useRouter } from "next/navigation";
import Button from "@/components/button/Button";
import LoginButton from "@/components/button/LoginButton";

export default function Home() {
  const router = useRouter();
  const handleButtonClick = () => {
    router.push("/docs");
  };

  return (
    <>
      <div className="flex w-full max-w-[1200px] flex-col gap-[150px] px-40 py-[80px]">
        {/* 1문단 */}
        <div className="flex items-center justify-between">
          <div className="flex flex-col gap-40">
            <div className="flex flex-col gap-10">
              <div className="text-20 font-semibold text-green-600">
                💡 코딩 없는 모델 개발
              </div>
              <div className="text-40 font-bold">단순하게,</div>
              <div className="text-40 font-bold">머리속의 모델을</div>
              <div className="text-40 font-bold">블록으로</div>
            </div>
            <div className="flex flex-col gap-10">
              <div className="text-18">
                AI 모델을 만들기 위한 수많은 개발코드들,
              </div>
              <div className="text-18">번거로운 건 SCV가 다 해드릴게요</div>
              <div className="text-18">
                오직 블록만 드래그해 AI 모델을 만들어보세요
              </div>
            </div>
          </div>
          <img
            src="/landing-1.png"
            alt="블록 코딩 이미지"
            className="h-auto w-[600px] rounded-10 shadow-xl"
          />
        </div>

        {/* 2문단 */}
        <div className="flex items-center justify-between">
          <img
            src="/landing-2.png"
            alt="사용자 테스트 이미지"
            className="h-auto w-[400px] rounded-10 shadow-xl"
          />
          <div className="flex flex-col gap-40">
            <div className="flex flex-col gap-10">
              <div className="text-20 font-semibold text-green-600">
                💡 데이터 전처리
              </div>
              <div className="text-40 font-bold">
                번거로운 데이터 전처리 많죠?
              </div>
              <div className="text-40 font-bold">
                전처리도 드래그 앤 드롭으로 해결해요
              </div>
            </div>
            <div className="flex flex-col gap-10">
              <div className="text-18">간단한 드래그 앤 드롭만으로</div>
              <div className="text-18">
                쉽고 빠르게 데이터 전처리 후 테스트 해 볼 수 있어요
              </div>
            </div>
          </div>
        </div>

        {/* 3문단 */}
        <div className="flex items-center justify-between">
          <div className="flex flex-col gap-40">
            <div className="flex flex-col gap-10">
              <div className="text-20 font-semibold text-green-600">
                💡 다양한 코드 활용
              </div>
              <div className="text-40 font-bold">
                블록으로 코드를 얻으셨나요?
              </div>
              <div className="text-40 font-bold">
                코드를 마음껏 활용해보세요
              </div>
            </div>
            <div className="flex flex-col gap-10">
              <div className="text-18">.py 확장자의 파일을 제공해요</div>
              <div className="text-18">
                생성된 소스 코드들은 깃허브로 내보내기 가능해요
              </div>
              <div className="text-18">어디서든 다양하게 활용해 보세요</div>
            </div>
          </div>
          <img
            src="/landing-3.png"
            alt="깃허브로 내보내기 이미지"
            className="h-auto w-[500px] rounded-10 shadow-xl"
          />
        </div>

        {/* 4문단 */}
        <div className="flex items-center justify-between">
          <img
            src="/landing-4.png"
            alt="사용자 테스트 이미지"
            className="h-auto w-[400px] rounded-10 shadow-xl"
          />
          <div className="flex flex-col gap-40">
            <div className="flex flex-col gap-10">
              <div className="text-20 font-semibold text-green-600">
                💡 결과 보고서 열람
              </div>
              <div className="text-40 font-bold">
                어떻게 데이터를 처리하고 있을까요?
              </div>
              <div className="text-40 font-bold">
                시각화된 결과 보고서로 탐구해요
              </div>
            </div>
            <div className="flex flex-col gap-10">
              <div className="text-18">결과가 요약된 보고서를 제공해요</div>
              <div className="text-18">
                항목별 수치와 시각화된 이미지를 확인하세요
              </div>
            </div>
          </div>
        </div>

        {/* 5문단 */}
        <div className="flex items-center justify-between">
          <div className="flex flex-col gap-10">
            <div className="text-[30px] font-semibold">
              사용 방법, 어렵지 않아요
            </div>
            <div className="text-[30px] font-semibold">
              SCV의 공식문서를 보고 따라해보세요
            </div>
          </div>
          <div className="px-[60px]">
            <Button
              size="l"
              design="fill"
              color="green"
              onClick={handleButtonClick}
            >
              공식문서 바로가기
            </Button>
          </div>
        </div>

        {/* 6문단 */}
        <div className="flex flex-col items-center justify-center gap-[20px]">
          <div className="text-[30px] font-semibold">
            SCV는 모델 생성을 위한 모든 기능을 담았습니다
          </div>
          <div className="text-[30px] font-semibold">
            SCV, 바로 시작해보세요
          </div>
          <LoginButton />
        </div>
      </div>
    </>
  );
}
